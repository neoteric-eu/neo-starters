package eu.neoteric.starter.mongo.request.processors;

import com.google.common.collect.Lists;
import eu.neoteric.starter.mongo.request.FieldMapper;
import eu.neoteric.starter.mongo.request.Mappings;
import eu.neoteric.starter.request.RequestField;
import eu.neoteric.starter.request.RequestLogicalOperator;
import eu.neoteric.starter.request.RequestObject;
import eu.neoteric.starter.request.RequestObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

import static eu.neoteric.starter.mongo.request.Mappings.LOGICAL_OPERATORS;

public class MongoRequestLogicalOperatorProcessor implements MongoRequestObjectProcessor<RequestLogicalOperator> {

    @Autowired
    private MongoRequestFieldProcessor mongoRequestFieldProcessor;

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.LOGICAL_OPERATOR.equals(key);
    }

    @Override
    public List<Criteria> build(RequestLogicalOperator logicalOperator, Map<RequestObject, Object> rootLogicalValues, FieldMapper fieldMapper) {
        List<Criteria> criteriaElements = Lists.newArrayList();
        rootLogicalValues.forEach((requestObject, logicalValue) -> {
            if (!mongoRequestFieldProcessor.apply(requestObject.getType())) {
                throw new IllegalArgumentException(requestObject.getType() + " cannot be applied to LogicalOperator in root.");
            }
            if (!(logicalValue instanceof Map)) {
                throw new IllegalArgumentException("LogicalOperator expect Map as argument, but get: " + logicalValue);
            }
            List<Criteria> fieldCriterias = mongoRequestFieldProcessor.build((RequestField) requestObject, (Map) logicalValue, fieldMapper);
            criteriaElements.addAll(fieldCriterias);
        });
        Mappings.LogicalOperatorFunction logicalOperatorFunction = LOGICAL_OPERATORS.get(logicalOperator.getOperator());
        return Lists.newArrayList(logicalOperatorFunction.apply(new Criteria(), criteriaElements.toArray(new Criteria[0])));
    }

}
