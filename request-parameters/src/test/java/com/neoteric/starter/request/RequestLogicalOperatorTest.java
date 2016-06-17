package com.neoteric.starter.request;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLogicalOperatorTest {

    @Test
    public void testOfMethod() throws Exception {
        RequestLogicalOperator requestLogicalOperator = RequestLogicalOperator.of("$or");
        assertThat(requestLogicalOperator.getOperator()).isEqualTo(LogicalOperatorType.OR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfMethodShouldFailWhenIncorrectLogicalOperatorSpecified() throws Exception {
        RequestLogicalOperator requestLogicalOperator = RequestLogicalOperator.of("wrongLogicalOperator");
    }

    @Test
    public void testTwoDifferentObjectsEqualsWhenSameNameGiven() throws Exception {
        RequestLogicalOperator requestLogicalOperator = RequestLogicalOperator.of("$or");
        assertThat(requestLogicalOperator).isEqualTo(RequestLogicalOperator.of("$or"));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(RequestLogicalOperator.class)
                .verify();
    }
}
