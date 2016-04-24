package com.neoteric.starter.request.sort;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestSortTest {

    @Test
    public void testOfMethod() {
        RequestSort requestSort = RequestSort.of("fieldName", "asc");
        assertThat(requestSort.getFieldName()).isEqualTo("fieldName");
        assertThat(requestSort.getType()).isEqualTo(SortType.ASC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOfMethodShouldFailWhenIncorrectTypeSpecified() {
        RequestSort requestSort = RequestSort.of("fieldName", "wrongName");
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(RequestSort.class)
                .verify();
    }
}
