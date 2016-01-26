package com.neoteric.starter.mongo.request.processors.fields;

import com.google.common.collect.Lists;
import com.neoteric.starter.mongo.request.FieldMapper;
import com.neoteric.starter.request.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

import static com.neoteric.starter.mongo.request.Mappings.LOGICAL_OPERATORS;

public enum MongoFieldToLogicalOperatorSubProcessor implements MongoFieldSubProcessor<RequestLogicalOperator> {

    INSTANCE;

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.LOGICAL_OPERATOR.equals(key);
    }

    @Override
    public Criteria build(RequestField field, RequestLogicalOperator logicalOperator, Object logicalOperatorObjectValues, FieldMapper fieldMapper) {
        if (!(logicalOperatorObjectValues instanceof Map)) {
            throw new IllegalArgumentException("LogicalOperator expect Map as argument, but get: " + logicalOperatorObjectValues);
        }
        Map<RequestObject, Object> logicalOperatorValues = (Map<RequestObject, Object>) logicalOperatorObjectValues;
        List<Criteria> criteriaElements = Lists.newArrayList();
        logicalOperatorValues.forEach((requestObject, operatorValue) -> {
            if (!MongoFieldToOperatorSubProcessor.INSTANCE.apply(requestObject.getType())) {
                throw new IllegalArgumentException(requestObject.getType() + " cannot be applied to LogicalOperator in non root.");
            }
            Criteria whereCriteria = MongoFieldToOperatorSubProcessor.INSTANCE
                    .build(field, (RequestOperator) requestObject, operatorValue, fieldMapper);
            criteriaElements.add(whereCriteria);
        });
        return LOGICAL_OPERATORS.get(logicalOperator.getOperator()).apply(new Criteria(), criteriaElements.stream().toArray(Criteria[]::new));
    }
}
