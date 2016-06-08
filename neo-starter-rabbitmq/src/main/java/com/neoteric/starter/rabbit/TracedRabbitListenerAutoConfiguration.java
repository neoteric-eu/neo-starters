package com.neoteric.starter.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.log4j.MDC;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass(RabbitTemplate.class)
@AutoConfigureAfter(TracedRabbitTemplateAutoConfiguration.class)
public class TracedRabbitListenerAutoConfiguration {

    private final StarterRabbitProperties starterRabbitProperties;
    private final SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;
    private final AmqpAdmin amqpAdmin;
    private final TracedRabbitTemplate tracedRabbitTemplate;
    private final MessageConverter messageConverter;

    @Autowired
    public TracedRabbitListenerAutoConfiguration(StarterRabbitProperties starterRabbitProperties,
                                                 @Qualifier("rabbitListenerContainerFactory") SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory,
                                                 AmqpAdmin amqpAdmin,
                                                 TracedRabbitTemplate tracedRabbitTemplate,
                                                 MessageConverter messageConverter) {
        this.starterRabbitProperties = starterRabbitProperties;
        this.rabbitListenerContainerFactory = rabbitListenerContainerFactory;
        this.amqpAdmin = amqpAdmin;
        this.tracedRabbitTemplate = tracedRabbitTemplate;
        this.messageConverter = messageConverter;
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
                .recoverer(new RetryMessageRecoverer(tracedRabbitTemplate, amqpAdmin, starterRabbitProperties))
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

    private static void setRequestIdOnMdc(Message message) {
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
}
