package com.neoteric.starter.rabbit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class MessageRecovererTest {

    public static final String X_ORIGINAL_EXCHANGE = "x-original-exchange";
    public static final String X_ORIGINAL_ROUTING_KEY = "x-original-routingKey";
    public static final String X_DEATH = "x-death";
    public static final String DEATH_COUNT = "count";
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String X_MESSAGE_TTL = "x-message-ttl";

    @Mock
    AmqpTemplate errorTemplate;

    @Mock
    AmqpAdmin amqpAdmin;

    MessageProperties messageProperties;
    StarterRabbitProperties starterRabbitProperties;
    Message message;

    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
        messageProperties = new MessageProperties();
        Map<String, Object> headers = messageProperties.getHeaders();
        headers.put(X_ORIGINAL_EXCHANGE, "testExchange");
        headers.put(X_ORIGINAL_ROUTING_KEY, "test-key");
        messageProperties.setReceivedExchange("testExchange");
        messageProperties.setConsumerQueue("testConsumer");
        messageProperties.setReceivedRoutingKey("test-key");
        message = new Message(null, messageProperties);

        starterRabbitProperties = new StarterRabbitProperties();
        starterRabbitProperties.setRetryMessageTTL(100);
    }

    @Test
    public void shouldResendMessage() {
        RetryMessageRecoverer retryMessageRecoverer = new RetryMessageRecoverer(errorTemplate, amqpAdmin, starterRabbitProperties);

        retryMessageRecoverer.recover(message, new Exception("Blank ex"));

        verify(errorTemplate).send("dle-testExchange", "retry.test-key", message);
    }

    @Test
    public void shouldCreateQueueAndBindingForResend() {
        RetryMessageRecoverer retryMessageRecoverer = new RetryMessageRecoverer(errorTemplate, amqpAdmin, starterRabbitProperties);

        retryMessageRecoverer.recover(message, new Exception("Blank ex"));

        ArgumentCaptor<Queue> queueCaptor = ArgumentCaptor.forClass(Queue.class);
        verify(amqpAdmin).declareQueue(queueCaptor.capture());
        Queue queue = queueCaptor.getValue();
        assertThat(queue.getName()).isEqualTo("retry-testConsumer");
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.isExclusive()).isFalse();
        assertThat(queue.isAutoDelete()).isFalse();

        ArgumentCaptor<Binding> bindingCaptor = ArgumentCaptor.forClass(Binding.class);
        verify(amqpAdmin).declareBinding(bindingCaptor.capture());
        Binding binding = bindingCaptor.getValue();
        assertThat(binding.getDestination()).isEqualTo("retry-testConsumer");
        assertThat(binding.getDestinationType()).isEqualTo(Binding.DestinationType.QUEUE);
        assertThat(binding.getExchange()).isEqualTo("dle-testExchange");
        assertThat(binding.getRoutingKey()).isEqualTo("retry.test-key");
    }

    @Test
    public void shouldDeclareDleExchangeForResend() {
        RetryMessageRecoverer retryMessageRecoverer = new RetryMessageRecoverer(errorTemplate, amqpAdmin, starterRabbitProperties);

        retryMessageRecoverer.recover(message, new Exception("Blank ex"));

        ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
        verify(amqpAdmin).declareExchange(exchangeCaptor.capture());
        Exchange exchange = exchangeCaptor.getValue();
        assertThat(exchange.getName()).isEqualTo("dle-testExchange");
        assertThat(exchange.isDurable()).isTrue();
        assertThat(exchange.isAutoDelete()).isFalse();
    }

    @Test
    public void shouldPutMessageInDleExchangeWithDleKeyAfter3Attemps() {
        RetryMessageRecoverer retryMessageRecoverer = new RetryMessageRecoverer(errorTemplate, amqpAdmin, starterRabbitProperties);
        messageProperties.getHeaders().put(X_DEATH, Lists.newArrayList(ImmutableMap.of(DEATH_COUNT, 3l)));

        retryMessageRecoverer.recover(message, new Exception("Blank ex"));

        verify(errorTemplate).send("dle-testExchange", "dle.test-key", message);
    }
}
