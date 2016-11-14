package com.neoteric.starter.mongo.request;

import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.request.LogicalOperatorType;
import com.neoteric.starter.request.OperatorType;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            .put(OperatorType.IN, (criteria, in) -> criteria.in(((List) in).stream().toArray(Object[]::new)))
            .put(OperatorType.NOT_IN, (criteria, nin) -> criteria.nin(((List) nin).stream().toArray(Object[]::new)))
            .put(OperatorType.STARTS_WITH, (criteria, startsWith) -> criteria.regex(
                    Pattern.compile("^" + Pattern.quote((String) startsWith), Pattern.CASE_INSENSITIVE)))
            .put(OperatorType.ALL, (criteria, all) -> criteria.all(((List) all).stream().toArray(Object[]::new)))
            .put(OperatorType.REGEX, (criteria, regex) -> buildRegex(criteria, regex))
            .build();

    static Criteria buildRegex(Criteria criteria, Object regexValue) {
        if (regexValue instanceof List) {
            return criteria.all(((List<String>) regexValue).stream()
                    .map(regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                    .collect(Collectors.toList()));
        } else {
            return criteria.regex(Pattern.compile((String) regexValue, Pattern.CASE_INSENSITIVE));
        }
    }
}
