package com.neoteric.starter.rabbit;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.util.Assert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Slf4j
public class RetryMessageRecoverer implements MessageRecoverer {

    public static final String X_EXCEPTION_STACKTRACE = "x-exception-stacktrace";
    public static final String X_EXCEPTION_MESSAGE = "x-exception-message";
    public static final String X_ORIGINAL_EXCHANGE = "x-original-exchange";
    public static final String X_ORIGINAL_ROUTING_KEY = "x-original-routingKey";
    public static final String X_DEATH = "x-death";
    public static final String DEATH_COUNT = "count";
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String X_MESSAGE_TTL = "x-message-ttl";
    public static final int MSG_COUNT_LIMIT = 3;

    private final AmqpTemplate errorTemplate;
    private volatile String retryRoutingKeyPrefix = "retry.";
    private volatile String retryQueuePrefix = "retry-";
    private volatile String dleRoutingKeyPrefix = "dle.";
    private volatile String dleQueuePrefix = "dle-";
    private volatile String dleExchangePrefix = "dle-";
    private final AmqpAdmin amqpAdmin;
    private final String predefinedDleExchange;
    private final int retryMessageTTL;

    public RetryMessageRecoverer(AmqpTemplate errorTemplate, AmqpAdmin amqpAdmin, String predefinedDleExchange, int retryMessageTTL) {
        this.amqpAdmin = amqpAdmin;
        Assert.notNull(errorTemplate, "'errorTemplate' cannot be null");
        this.errorTemplate = errorTemplate;
        this.predefinedDleExchange = predefinedDleExchange;
        this.retryMessageTTL = retryMessageTTL;
    }

    @Override
    public void recover(Message message, Throwable cause) {
        String originalExchange = message.getMessageProperties().getReceivedExchange();
        String originalQueueName = message.getMessageProperties().getConsumerQueue();
        String originalRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        Long messageCount = getXDeathCount(message);
        headers.put(X_EXCEPTION_STACKTRACE, getStackTraceAsString(cause));
        headers.put(X_EXCEPTION_MESSAGE, cause.getCause() != null ? cause.getCause().getMessage() : cause.getMessage());
        headers.put(X_ORIGINAL_EXCHANGE, originalExchange);
        headers.put(X_ORIGINAL_ROUTING_KEY, message.getMessageProperties().getReceivedRoutingKey());

        String targetExchange = declareDleExchange(originalExchange);
        String targetRoutingKey;
        if (messageCount < MSG_COUNT_LIMIT) {
            targetRoutingKey = retryRoutingKey(originalRoutingKey);
            createAdditionalBindedQueue(retryQueue(originalQueueName), targetRoutingKey, targetExchange, ImmutableMap.of(
                    X_DEAD_LETTER_EXCHANGE, originalExchange,
                    X_DEAD_LETTER_ROUTING_KEY, originalRoutingKey,
                    X_MESSAGE_TTL, retryMessageTTL));

        } else {
            targetRoutingKey = dleRoutingKey(originalRoutingKey);
            createAdditionalBindedQueue(dleQueue(originalQueueName), targetRoutingKey, targetExchange, ImmutableMap.of());
        }

        this.errorTemplate.send(targetExchange, targetRoutingKey, message);

        LOG.info("Republishing failed message to exchange: '{}' with routing key: '{}'. Redelivery nr: {} ",
                targetExchange, targetRoutingKey, messageCount);
    }


    private Long getXDeathCount(Message message) {
        List<Object> xDeathHeader = (List<Object>) message.getMessageProperties().getHeaders().get(X_DEATH);
        if (xDeathHeader == null || xDeathHeader.isEmpty()) {
            return 0L;
        }
        Map<String, Object> xDeathHeaderProperties = (Map<String, Object>) xDeathHeader.get(0);
        return (Long) xDeathHeaderProperties.getOrDefault(DEATH_COUNT, 0);
    }

    private String declareDleExchange(String originalExchangeName) {
        String dleExchangeName = dleExchange(originalExchangeName);
        boolean durable = true;
        boolean autoDelete = false;
        DirectExchange exchange = new DirectExchange(dleExchangeName, durable, autoDelete);
        amqpAdmin.declareExchange(exchange);
        return dleExchangeName;
    }

    private void createAdditionalBindedQueue(String queueName, String routingKey, String exchange, Map<String, Object> properties) {
        boolean durable = true;
        boolean exclusive = false;
        boolean autoDelete = false;
        Queue queue = new Queue(queueName, durable, exclusive, autoDelete, properties);
        amqpAdmin.declareQueue(queue);
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchange, routingKey, null);
        amqpAdmin.declareBinding(binding);
    }

    private String retryQueue(String originalQueueName) {
        return this.retryQueuePrefix + originalQueueName;
    }

    private String retryRoutingKey(String originalRoutingKey) {
        return this.retryRoutingKeyPrefix + originalRoutingKey;
    }

    private String dleQueue(String originalQueueName) {
        return this.dleQueuePrefix + originalQueueName;
    }

    private String dleRoutingKey(String originalRoutingKey) {
        return this.dleRoutingKeyPrefix + originalRoutingKey;
    }

    private String dleExchange(String originalExchange) {
        return Strings.isNullOrEmpty(predefinedDleExchange) ? (dleExchangePrefix + originalExchange) : predefinedDleExchange;
    }

    @SuppressWarnings("squid:S1148")
    private String getStackTraceAsString(Throwable cause) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        cause.printStackTrace(printWriter);
        return stringWriter.getBuffer().toString();
    }
}
