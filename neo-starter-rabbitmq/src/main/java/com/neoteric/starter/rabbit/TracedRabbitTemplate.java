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
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.neoteric.starter.Constants.REQUEST_ID;

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
        List<MessagePostProcessor> postProcessors = Arrays.asList(beforePublishPostProcessors);
        postProcessors.add(SET_REQUEST_ID_TO_MESSAGE);
        super.setAfterReceivePostProcessors(postProcessors.toArray(new MessagePostProcessor[postProcessors.size()]));
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

    public void sendJson(Object object) throws AmqpException {
        super.convertAndSend(convertToJson(object));
    }

    public void correlationConvertAndSendJson(Object object, CorrelationData correlationData) throws AmqpException {
        super.correlationConvertAndSend(convertToJson(object), correlationData);
    }

    public void sendJson(String routingKey, final Object object) throws AmqpException {
        super.convertAndSend(routingKey, convertToJson(object));
    }
}
