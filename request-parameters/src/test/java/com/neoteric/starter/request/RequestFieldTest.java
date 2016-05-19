package com.neoteric.starter.request;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestFieldTest {

    @Test
    public void testOfMethod() throws Exception {
        RequestField requestField = RequestField.of("foo");
        assertThat(requestField.getFieldName()).isEqualTo("foo");
    }

    @Test
    public void testTwoDifferentObjectsEqualsWhenSameNameGiven() throws Exception {
        RequestField requestField = RequestField.of("foo");
        assertThat(requestField).isEqualTo(RequestField.of("foo"));
    }

    @Test
    public void testTwoObjectsAreNotEqualWhenDifferentNames() throws Exception {
        RequestField requestField = RequestField.of("foo");
        assertThat(requestField).isNotEqualTo(RequestField.of("bar"));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(RequestField.class)
                .verify();
    }
}
