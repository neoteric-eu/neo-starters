package eu.neoteric.starter.request.sort;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SortTypeTest {

    @Test
    public void shouldCreateSortTypeForProperName() {
        SortType sortType = SortType.fromString("asc");
        assertThat(SortType.ASC).isEqualTo(sortType);
    }

    @Test
    public void shouldCreateSortTypeForProperNameIgnoreCase() {
        SortType sortType = SortType.fromString("aSC");
        assertThat(SortType.ASC).isEqualTo(sortType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToCreateSortTypeOnIncorrectName() {
        SortType sortType = SortType.fromString("wrongName");
    }
}
