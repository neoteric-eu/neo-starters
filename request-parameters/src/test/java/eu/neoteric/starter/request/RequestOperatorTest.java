package eu.neoteric.starter.request;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestOperatorTest {

    @Test
    public void testOfMethod() throws Exception {
        RequestOperator requestOperator = RequestOperator.of("$eq");
        assertThat(requestOperator.getOperator()).isEqualTo(OperatorType.EQUAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfMethodShouldFailWhenIncorrectOperatorSpecified() throws Exception {
        RequestOperator requestOperator = RequestOperator.of("wrongLogicalOperator");
    }

    @Test
    public void testTwoDifferentObjectsEqualsWhenSameNameGiven() throws Exception {
        RequestOperator requestOperator = RequestOperator.of("$eq");
        assertThat(requestOperator).isEqualTo(RequestOperator.of("$eq"));
    }

    @Test
    public void testTwoObjectsAreNotEqualWhenDifferentNames() throws Exception {
        RequestOperator requestOperator = RequestOperator.of("$eq");
        assertThat(requestOperator).isNotEqualTo(RequestOperator.of("$startsWith"));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(RequestOperator.class)
                .verify();
    }
}
