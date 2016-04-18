package com.neoteric.starter.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.log4j.MDC;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.neoteric.starter.rabbit.StarterRabbitConstants.REQUEST_ID;

@Slf4j
@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@AutoConfigureAfter(RabbitAutoConfiguration.class)
@EnableConfigurationProperties(StarterRabbitProperties.class)
public class StarterRabbitAutoConfiguration {

    @Autowired
    StarterRabbitProperties rabbitProperties;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("rabbitListenerContainerFactory")
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;

    @Autowired
    MessageConverter messageConverter;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Bean
    public RabbitEntityTypeMapper rabbitEntityTypeMapper() {
        return new RabbitEntityTypeMapper(rabbitProperties);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter(ObjectMapper objectMapper, RabbitEntityTypeMapper rabbitEntityTypeMapper) {
        Jackson2JsonMessageConverter jacksonMessageConverter = new Jackson2JsonMessageConverter();
        jacksonMessageConverter.setJsonObjectMapper(objectMapper);
        jacksonMessageConverter.setJavaTypeMapper(rabbitEntityTypeMapper);
        return jacksonMessageConverter;
    }

    @Bean
    public ContentTypeDelegatingMessageConverter messageConverter(Jackson2JsonMessageConverter jacksonMessageConverter) {
        ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
        messageConverter.addDelegate(MessageProperties.CONTENT_TYPE_JSON, jacksonMessageConverter);
        return messageConverter;
    }

    @PostConstruct
    public void setAdviceChain() {
        // the order of Advice chain is important to retain requestId in LogOnRetryListener
        rabbitListenerContainerFactory.setAdviceChain(tracingOnListener(), retryOperations());
        rabbitListenerContainerFactory.setMessageConverter(messageConverter);
    }

    private Advice retryOperations() {
        return RetryInterceptorBuilder.stateless()
                .retryOperations(defaultRetryTemplate())
                .recoverer(new RetryMessageRecoverer(rabbitTemplate, amqpAdmin, rabbitProperties.getDleExchange(),
                        rabbitProperties.getRetryMessageTTL()))
                .build();
    }

    private RetryTemplate defaultRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new LogOnRetryListener());
        retryTemplate.setBackOffPolicy(defaultBackOffPolicy());
        retryTemplate.setRetryPolicy(defaultRetryPolicy());
        return retryTemplate;
    }

    private RetryPolicy defaultRetryPolicy() {
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(1);

        Map<Class<? extends Throwable>, RetryPolicy> dontRetryExceptions = new HashMap<>();
        dontRetryExceptions.put(AmqpRejectAndDontRequeueException.class, new NeverRetryPolicy());

        return new ListenerExceptionClassifierRetryPolicy(dontRetryExceptions, simpleRetryPolicy);
    }

    private ExponentialBackOffPolicy defaultBackOffPolicy() {
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(10000);
        policy.setMultiplier(1);
        policy.setMaxInterval(500000);
        return policy;
    }

    private Advice tracingOnListener() {
        MethodInterceptor methodInterceptor = invocation -> {
            Message message = (Message) invocation.getArguments()[1];
            setRequestIdOnMdc(message);
            Object result;
            try {
                result = invocation.proceed();
            } finally {
                MDC.remove(REQUEST_ID);
            }
            return result;
        };
        return methodInterceptor;
    }

    public static void setRequestIdOnMdc(Message message) {
        String requestId = String.valueOf(message.getMessageProperties().getHeaders().get(REQUEST_ID));
        if (StringUtils.isEmpty(requestId)) {
            LOG.warn("No Request ID found in Message");
            return;
        }
        if (!StringUtils.isEmpty(MDC.get(REQUEST_ID))) {
            LOG.warn("Request ID already found in MDC: {}, overriding with: {}", MDC.get(REQUEST_ID), requestId);
        }
        MDC.put(REQUEST_ID, requestId);
    }

    @Bean
    public TracedRabbitTemplate tracedRabbitTemplate(ConnectionFactory connectionFactor, ContentTypeDelegatingMessageConverter messageConverter) {
        return new TracedRabbitTemplate(connectionFactor, messageConverter);
    }
}
