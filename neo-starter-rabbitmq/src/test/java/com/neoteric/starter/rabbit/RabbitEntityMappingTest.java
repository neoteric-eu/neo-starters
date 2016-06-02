package com.neoteric.starter.rabbit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RabbitEntityMappingTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        this.context = new AnnotationConfigApplicationContext();
    }

    @After
    public void tearDown() {
        MDC.remove(StarterRabbitConstants.REQUEST_ID);
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void tracedRabbitTemplateExists() throws Exception {
        context.register(ObjectMapper.class, Jackson2JsonMessageConverter.class, RabbitAutoConfiguration.class, StarterRabbitAutoConfiguration.class);
        context.refresh();

        assertThat(this.context.getBeanNamesForType(TracedRabbitTemplate.class).length).isEqualTo(1);
    }

    @Test
    public void shouldSetMessageHeadersProperly() throws Exception {
        context.register(ObjectMapper.class, Jackson2JsonMessageConverter.class, RabbitAutoConfiguration.class, StarterRabbitAutoConfiguration.class);
        context.refresh();

        TracedRabbitTemplate tracedRabbitTemplate = context.getBean(TracedRabbitTemplate.class);
        TracedRabbitTemplate spyTracedRabbitTemplate = spy(tracedRabbitTemplate);
        doNothing().when(spyTracedRabbitTemplate).send(any(), any(),any(),any());
        MDC.put(StarterRabbitConstants.REQUEST_ID, "request1");

        spyTracedRabbitTemplate.sendJson(new FooEntity("test1"));

        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(spyTracedRabbitTemplate).send(eq(""), eq(""), messageArgumentCaptor.capture(), eq(null));

        Message message = messageArgumentCaptor.getValue();
        assertThat(message.getMessageProperties().getHeaders().size()).isEqualTo(2);
        assertThat(message.getMessageProperties().getHeaders().get("__EntityId__")).isEqualTo("fooEntity");
        assertThat(message.getMessageProperties().getHeaders().get("__TypeId__")).isEqualTo("com.neoteric.starter.rabbit.RabbitEntityMappingTest$FooEntity");
        assertThat(message.getMessageProperties().getContentType()).isEqualTo("application/json");
    }

    @Test
    public void shouldSerializeAndDeserializeMessageProperly() throws Exception {
        context.register(ObjectMapper.class, Jackson2JsonMessageConverter.class, RabbitAutoConfiguration.class, StarterRabbitAutoConfiguration.class);
        context.refresh();

        TracedRabbitTemplate tracedRabbitTemplate = context.getBean(TracedRabbitTemplate.class);
        TracedRabbitTemplate spyTracedRabbitTemplate = spy(tracedRabbitTemplate);
        doNothing().when(spyTracedRabbitTemplate).send(any(), any(),any(),any());

        spyTracedRabbitTemplate.sendJson(new FooEntity("test1"));

        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(spyTracedRabbitTemplate).send(eq(""), eq(""), messageArgumentCaptor.capture(), eq(null));

        Message message = messageArgumentCaptor.getValue();
        FooEntity result = (FooEntity) spyTracedRabbitTemplate.getMessageConverter().fromMessage(message);
        assertThat(result).isNotNull();
        assertThat(result.getProperty()).isEqualTo("test1");
    }

    @RabbitEntity("fooEntity")
    public static class FooEntity {

        String property;

        @JsonCreator
        public FooEntity(@JsonProperty("property") String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }
}
