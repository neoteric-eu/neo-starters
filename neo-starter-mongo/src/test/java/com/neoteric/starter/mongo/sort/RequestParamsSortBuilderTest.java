package com.neoteric.starter.mongo.sort;

import com.google.common.collect.Lists;
import com.neoteric.starter.request.sort.RequestSort;
import com.neoteric.starter.request.sort.SortType;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestParamsSortBuilderTest {

    @Test
    public void testShouldReturnEmptySortOperationForEmptyRequestSortList() {
        Optional<Sort> result = RequestParamsSortBuilder.newBuilder().build(Lists.newArrayList());
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void testShouldBuildSortOperation() {
        RequestSort requestSort1 = RequestSort.of("field1", SortType.DESC);
        RequestSort requestSort2 = RequestSort.of("field2", SortType.ASC);
        RequestSort requestSort3 = RequestSort.of("field3", SortType.DESC);

        Optional<Sort> result = RequestParamsSortBuilder.newBuilder()
                .build(Lists.newArrayList(requestSort1, requestSort2, requestSort3));
        assertThat(result.isPresent()).isTrue();
    }
}
