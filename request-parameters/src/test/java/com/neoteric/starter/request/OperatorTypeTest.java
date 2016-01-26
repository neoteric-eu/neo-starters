package com.neoteric.starter.request;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OperatorTypeTest {

    @Test
    public void shouldReturnTrueForEqOperator() throws Exception {
        assertThat(OperatorType.contains("$eq")).isTrue();
    }

    @Test
    public void shouldReturnFalseForUnknownOperator() throws Exception {
        assertThat(OperatorType.contains("$foo")).isFalse();
    }

    @Test
    public void shouldReturnOperatorTypeForEqualOperator() throws Exception {
        assertThat(OperatorType.EQUAL).isEqualTo(OperatorType.fromString("$eq"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnIncorrectNameForOperatorType() throws Exception {
        OperatorType.fromString("foo");
    }
}