package com.neoteric.starter.mongo.sort;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.neoteric.starter.mongo.request.FieldMapper;
import com.neoteric.starter.request.sort.RequestSort;
import com.neoteric.starter.request.sort.SortType;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestParamsSortBuilder {

    public static RequestParamsSortBuilder newBuilder() {
        return new RequestParamsSortBuilder();
    }

    private static final Map<SortType, Sort.Direction> SORT_MAPPING = ImmutableMap.of(
            SortType.ASC, Sort.Direction.ASC,
            SortType.DESC, Sort.Direction.DESC
    );

    public Optional<Sort> build(List<RequestSort> requestSortList) {
        return build(requestSortList, FieldMapper.of(Maps.<String, String>newHashMap()));
    }

    public Optional<Sort> build(List<RequestSort> requestSortList, FieldMapper fieldMapper) {
        Iterator<RequestSort> iterator = requestSortList.iterator();
        Sort sort = null;
        if (iterator.hasNext()) {
            RequestSort requestSort = iterator.next();
            sort = new Sort(SORT_MAPPING.get(requestSort.getType()), fieldMapper.get(requestSort.getFieldName()));
        }
        while (iterator.hasNext()) {
            RequestSort requestSort = iterator.next();
            sort = sort.and(new Sort(SORT_MAPPING.get(requestSort.getType()), fieldMapper.get(requestSort.getFieldName())));
        }
        return Optional.ofNullable(sort);
    }
}
