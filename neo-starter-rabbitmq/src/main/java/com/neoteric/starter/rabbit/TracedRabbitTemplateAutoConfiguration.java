package com.neoteric.starter.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@AutoConfigureAfter({RabbitAutoConfiguration.class, JacksonAutoConfiguration.class})
@PropertySource("classpath:rabbit-defaults.properties")
@EnableConfigurationProperties({StarterRabbitProperties.class, RabbitProperties.class})
public class TracedRabbitTemplateAutoConfiguration {

    private final ObjectMapper objectMapper;
    private final StarterRabbitProperties starterRabbitProperties;
    private final ConnectionFactory connectionFactory;

    public TracedRabbitTemplateAutoConfiguration(ConnectionFactory connectionFactory, ObjectMapper objectMapper,
                                                 StarterRabbitProperties starterRabbitProperties) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
        this.starterRabbitProperties = starterRabbitProperties;
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter jacksonMessageConverter = new Jackson2JsonMessageConverter();
        jacksonMessageConverter.setJsonObjectMapper(objectMapper);
        jacksonMessageConverter.setJavaTypeMapper(new RabbitEntityTypeMapper(starterRabbitProperties));

        ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
        messageConverter.addDelegate(MessageProperties.CONTENT_TYPE_JSON, jacksonMessageConverter);
        return messageConverter;
    }

    @Bean
    public TracedRabbitTemplate tracedRabbitTemplate() {
        return new TracedRabbitTemplate(connectionFactory, messageConverter());
    }
}