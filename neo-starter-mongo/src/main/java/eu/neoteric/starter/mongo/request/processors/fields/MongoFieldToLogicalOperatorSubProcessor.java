package eu.neoteric.starter.mongo.request.processors.fields;

import com.google.common.collect.Lists;
import eu.neoteric.starter.mongo.request.FieldMapper;
import eu.neoteric.starter.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

import static eu.neoteric.starter.mongo.request.Mappings.LOGICAL_OPERATORS;

public class MongoFieldToLogicalOperatorSubProcessor implements MongoFieldSubProcessor<RequestLogicalOperator> {

    @Autowired
    private MongoFieldToOperatorSubProcessor mongoFieldToOperatorSubProcessor;

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
            if (!mongoFieldToOperatorSubProcessor.apply(requestObject.getType())) {
                throw new IllegalArgumentException(requestObject.getType() + " cannot be applied to LogicalOperator in non root.");
            }
            Criteria whereCriteria = mongoFieldToOperatorSubProcessor.build(field, (RequestOperator) requestObject, operatorValue, fieldMapper);
            criteriaElements.add(whereCriteria);
        });
        return LOGICAL_OPERATORS.get(logicalOperator.getOperator()).apply(new Criteria(), criteriaElements.stream().toArray(Criteria[]::new));
    }
}
