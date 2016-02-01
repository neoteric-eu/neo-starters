package com.neoteric.starter.request;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogicalOperatorTypeTest {

    @Test
    public void shouldReturnTrueForOrOperator() throws Exception {
        assertThat(LogicalOperatorType.contains("$or")).isTrue();
    }

    @Test
    public void shouldReturnFalseForUnknownOperator() throws Exception {
        assertThat(LogicalOperatorType.contains("$tor")).isFalse();
    }

    @Test
    public void shouldReturnLogicalOperatorTypeForOrOperator() throws Exception {
        assertThat(LogicalOperatorType.OR).isEqualTo(LogicalOperatorType.fromString("$or"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnIncorrectNameForLogicalOperatorType() throws Exception {
        LogicalOperatorType.fromString("or");
    }
}