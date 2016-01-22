package com.neoteric.starter.mongo;

import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.request.LogicalOperatorType;
import com.neoteric.starter.request.OperatorType;
import org.springframework.data.mongodb.core.query.Criteria;

public interface Mappings {

    @FunctionalInterface
    interface LogicalOperatorFunction {
        Criteria apply(Criteria initial, Criteria... criteria);
    }

    ImmutableMap<LogicalOperatorType, LogicalOperatorFunction> LOGICAL_OPERATORS = ImmutableMap.<LogicalOperatorType, LogicalOperatorFunction>builder()
            .put(LogicalOperatorType.OR, Criteria::orOperator)
            .build();


    @FunctionalInterface
    interface OperatorFunction {
        Criteria apply(Criteria initial, Object value);
    }

    ImmutableMap<OperatorType, OperatorFunction> OPERATORS = ImmutableMap.<OperatorType, OperatorFunction>builder()
            .put(OperatorType.EQUAL, Criteria::is)
            .put(OperatorType.NOT_EQUAL, Criteria::ne)
            .put(OperatorType.LESS_THAN, Criteria::lt)
            .put(OperatorType.LESS_THAN_EQUAL, Criteria::lte)
            .put(OperatorType.GREATER_THAN, Criteria::gt)
            .put(OperatorType.GREATER_THAN_EQUAL, Criteria::gte)
            .put(OperatorType.IN, Criteria::in)
            .put(OperatorType.NOT_IN, Criteria::nin)
            .put(OperatorType.STARTS_WITH, (criteria, startsWith) -> criteria.regex((String)startsWith)) //TODO: Check it perf.
            .build();
}
