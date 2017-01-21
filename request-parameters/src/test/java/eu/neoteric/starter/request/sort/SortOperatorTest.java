package eu.neoteric.starter.request.sort;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SortOperatorTest {

    @Test
    public void shouldCreateSortOperatorForProperName() {
        SortOperator sortOperator = SortOperator.fromString("$order");
        assertThat(SortOperator.ORDER).isEqualTo(sortOperator);
    }

    @Test
    public void shouldCreateSortOperatorForProperNameIgnoreCase() {
        SortOperator sortOperator = SortOperator.fromString("$oRDEr");
        assertThat(SortOperator.ORDER).isEqualTo(sortOperator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateSortOperatorOnIncorrectName() {
        SortOperator sortOperator = SortOperator.fromString("$wrongName");
    }

    @Test
    public void shouldReturnTrueForOrderOperator() {
        assertThat(SortOperator.contains("$order")).isTrue();
    }

    @Test
    public void shouldReturnFalseForUnknownOperator() {
        assertThat(SortOperator.contains("$foo")).isFalse();
    }
}
