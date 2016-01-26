package com.neoteric.starter.mongo.request.processors;

import com.google.common.collect.Lists;
import com.neoteric.starter.mongo.request.FieldMapper;
import com.neoteric.starter.mongo.request.Mappings;
import com.neoteric.starter.request.RequestField;
import com.neoteric.starter.request.RequestLogicalOperator;
import com.neoteric.starter.request.RequestObject;
import com.neoteric.starter.request.RequestObjectType;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

import static com.neoteric.starter.mongo.request.Mappings.LOGICAL_OPERATORS;

public enum MongoRequestLogicalOperatorProcessor implements MongoRequestObjectProcessor<RequestLogicalOperator> {

    INSTANCE;

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.LOGICAL_OPERATOR.equals(key);
    }

    @Override
    public List<Criteria> build(RequestLogicalOperator logicalOperator, Map<RequestObject, Object> rootLogicalValues, FieldMapper fieldMapper) {
        List<Criteria> criteriaElements = Lists.newArrayList();
        rootLogicalValues.forEach((requestObject, logicalValue) -> {
            if (!MongoRequestFieldProcessor.INSTANCE.apply(requestObject.getType())) {
                throw new IllegalArgumentException(requestObject.getType() + " cannot be applied to LogicalOperator in root.");
            }
            if (!(logicalValue instanceof Map)) {
                throw new IllegalArgumentException("LogicalOperator expect Map as argument, but get: " + logicalValue);
            }
            List<Criteria> fieldCriterias = MongoRequestFieldProcessor.INSTANCE.build((RequestField) requestObject, (Map) logicalValue, fieldMapper);
            criteriaElements.addAll(fieldCriterias);
        });
        Mappings.LogicalOperatorFunction logicalOperatorFunction = LOGICAL_OPERATORS.get(logicalOperator.getOperator());
        return Lists.newArrayList(logicalOperatorFunction.apply(new Criteria(), criteriaElements.toArray(new Criteria[0])));
    }

}
