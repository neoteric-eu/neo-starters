package com.neoteric.starter.rabbit;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.neoteric.starter.rabbit.StarterRabbitConstants.REQUEST_ID;

public class TracedRabbitTemplate extends RabbitTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(TracedRabbitTemplate.class);

    private final static MessagePostProcessor SET_REQUEST_ID_TO_MESSAGE = message -> {
        LOG.debug("Setting Request ID for message: {}", MDC.get(REQUEST_ID));
        message.getMessageProperties().setHeader(REQUEST_ID, MDC.get(REQUEST_ID));
        return message;
    };
    private final ContentTypeDelegatingMessageConverter messageConverter;

    public TracedRabbitTemplate(ConnectionFactory connectionFactory, ContentTypeDelegatingMessageConverter messageConverter) {
        super(connectionFactory);
        this.messageConverter = messageConverter;
        setBeforePublishPostProcessors(SET_REQUEST_ID_TO_MESSAGE);
        super.setMessageConverter(messageConverter);
    }

    @Override
    public void setBeforePublishPostProcessors(MessagePostProcessor... beforePublishPostProcessors) {
        List<MessagePostProcessor> postProcessors = new ArrayList<>(Arrays.asList(beforePublishPostProcessors));
        postProcessors.add(SET_REQUEST_ID_TO_MESSAGE);
        super.setBeforePublishPostProcessors(postProcessors.toArray(new MessagePostProcessor[postProcessors.size()]));
    }

    protected Message convertToJson(final Object object) {
        if (object instanceof Message) {
            return (Message) object;
        }
        return messageConverter.toMessage(object,
                MessagePropertiesBuilder.newInstance()
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build());
    }

    public void sendJson(Object message) throws AmqpException {
        super.convertAndSend(convertToJson(message));
    }

    public void correlationConvertAndSendJson(Object message, CorrelationData correlationData) throws AmqpException {
        super.correlationConvertAndSend(convertToJson(message), correlationData);
    }

    public void sendJson(String routingKey, final Object message) throws AmqpException {
        super.convertAndSend(routingKey, convertToJson(message));
    }

    public void sendJson(String routingKey, final Object message, CorrelationData correlationData) throws AmqpException {
        super.convertAndSend(routingKey, convertToJson(message), correlationData);
    }

    public void sendJson(String exchange, String routingKey, final Object message) throws AmqpException {
        super.convertAndSend(exchange, routingKey, convertToJson(message), (CorrelationData) null);
    }

    public void sendJson(String exchange, String routingKey, final Object message, CorrelationData correlationData) throws AmqpException {
        super.send(exchange, routingKey, convertToJson(message), correlationData);
    }

    public void sendJson(Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
        super.convertAndSend(convertToJson(message), messagePostProcessor);
    }

    public void sendJson(String routingKey, Object message, MessagePostProcessor messagePostProcessor)
            throws AmqpException {
        super.convertAndSend(routingKey, convertToJson(message), messagePostProcessor, null);
    }

    public void sendJson(String routingKey, Object message, MessagePostProcessor messagePostProcessor,
                         CorrelationData correlationData)
            throws AmqpException {
        super.convertAndSend(routingKey, convertToJson(message), messagePostProcessor, correlationData);
    }

    public void sendJson(String exchange, String routingKey, final Object message,
                         final MessagePostProcessor messagePostProcessor) throws AmqpException {
        super.convertAndSend(exchange, routingKey, convertToJson(message), messagePostProcessor, null);
    }

    public void sendJson(String exchange, String routingKey, final Object message,
                         final MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {

        Message messageToSend = messagePostProcessor.postProcessMessage(convertToJson(message));
        super.send(exchange, routingKey, messageToSend, correlationData);
    }
}
