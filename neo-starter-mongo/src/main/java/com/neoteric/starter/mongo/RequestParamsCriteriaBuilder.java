package com.neoteric.starter.mongo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neoteric.starter.request.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

import static com.neoteric.starter.mongo.Mappings.LOGICAL_OPERATORS;
import static com.neoteric.starter.mongo.Mappings.OPERATORS;

public class RequestParamsCriteriaBuilder {

    public static RequestParamsCriteriaBuilder newBuilder() {
        return new RequestParamsCriteriaBuilder();
    }

    public Criteria build(Map<RequestObject, Object> requestParams) {
        return build(requestParams, FieldMapper.of(Maps.<String, String>newHashMap()));
    }

    public Criteria build(Map<RequestObject, Object> requestParams, FieldMapper fieldMapper) {
        List<Criteria> joinedCriteria = Lists.newArrayList();

        requestParams.forEach(((key, value) -> {
            //value in root has to be map
            if (key.getType().equals(RequestObjectType.FIELD)) {
                List<Criteria> fieldCriteria = processField((RequestField) key, (Map) value);
                joinedCriteria.addAll(fieldCriteria);
            } else if (key.getType().equals((RequestObjectType.LOGICAL_OPERATOR))) {
                Criteria rootLogicalCriteria = processRootLogicalOperator((RequestLogicalOperator)key, (Map)value);
                joinedCriteria.add(rootLogicalCriteria);
            } else {
                throw new IllegalStateException("BAD TYPE");
            }
        }));

        return new Criteria().andOperator(joinedCriteria.stream().toArray(Criteria[]::new));
    }

    //TODO: can have only fields
    private Criteria processRootLogicalOperator(RequestLogicalOperator logicalOperator, Map<RequestObject, Object> rootLogicalValues) {
        List<Criteria> criteriaElements = Lists.newArrayList();
        rootLogicalValues.forEach((requestObject, logicalValue) -> {
            List<Criteria> fieldCriterias = processField((RequestField) requestObject, (Map)logicalValue);
            criteriaElements.addAll(fieldCriterias);
        });


        return LOGICAL_OPERATORS.get(logicalOperator.getOperator()).apply(new Criteria(), criteriaElements.stream().toArray(Criteria[]::new));
    }

    private List<Criteria> processField(RequestField field, Map<RequestObject, Object> fieldValues) {

        List<Criteria> allFieldCriteria = Lists.newArrayList();

        fieldValues.forEach((requestObject, operatorValue) -> {
            if (requestObject instanceof RequestOperator) {
                RequestOperator operator = (RequestOperator)requestObject;
                Criteria fieldCriteria = Criteria.where(field.getFieldName());
                OPERATORS.get(operator.getOperator()).apply(fieldCriteria, operatorValue);
                allFieldCriteria.add(fieldCriteria);
            }
                //TODO: Can be only one logical operator within field
            if (requestObject instanceof  RequestLogicalOperator) {
                RequestLogicalOperator logicalOperator = (RequestLogicalOperator)requestObject;
                Criteria logicalCriteria = processLogicalOperator(field, logicalOperator, (Map)operatorValue);
                allFieldCriteria.add(logicalCriteria);
            }
        });

        return allFieldCriteria;
    }

    //Not in root only operators are allowed
    private Criteria processLogicalOperator(RequestField field, RequestLogicalOperator logicalOperator, Map<RequestObject, Object> logicalOperatorValues) {
        List<Criteria> criteriaElements = Lists.newArrayList();
        logicalOperatorValues.forEach((requestObject, operatorValue) -> {
            //TODO: check if requestObject is not Operator - exception
            RequestOperator operator = (RequestOperator) requestObject;
            Criteria whereCriteria = Criteria.where(field.getFieldName());
            OPERATORS.get(operator.getOperator()).apply(whereCriteria, operatorValue);
            criteriaElements.add(whereCriteria);
        });
        return LOGICAL_OPERATORS.get(logicalOperator.getOperator()).apply(new Criteria(), criteriaElements.stream().toArray(Criteria[]::new));
    }


}
