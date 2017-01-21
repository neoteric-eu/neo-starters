package eu.neoteric.starter.mongo.request.processors;

import com.google.common.collect.Lists;
import eu.neoteric.starter.mongo.request.FieldMapper;
import eu.neoteric.starter.mongo.request.processors.fields.MongoFieldSubProcessor;
import eu.neoteric.starter.mongo.request.processors.fields.MongoFieldToLogicalOperatorSubProcessor;
import eu.neoteric.starter.mongo.request.processors.fields.MongoFieldToOperatorSubProcessor;
import eu.neoteric.starter.request.RequestField;
import eu.neoteric.starter.request.RequestObject;
import eu.neoteric.starter.request.RequestObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MongoRequestFieldProcessor implements MongoRequestObjectProcessor<RequestField> {

    @Autowired
    private MongoFieldToOperatorSubProcessor mongoFieldToOperatorSubProcessor;

    @Autowired
    private MongoFieldToLogicalOperatorSubProcessor mongoFieldToLogicalOperatorSubProcessor;

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.FIELD.equals(key);
    }

    @Override
    public List<Criteria> build(RequestField field, Map<RequestObject, Object> fieldValues, FieldMapper fieldMapper) {
        List<Criteria> allFieldCriteria = Lists.newArrayList();

        fieldValues.forEach((requestObject, operatorValue) -> {
            Criteria criteria = Stream.<MongoFieldSubProcessor>of(mongoFieldToOperatorSubProcessor, mongoFieldToLogicalOperatorSubProcessor)
                    .filter(mongoFieldSubProcessor -> mongoFieldSubProcessor.apply(requestObject.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(requestObject.getType() + " cannot be applied to Field."))
                    .build(field, requestObject, operatorValue, fieldMapper);
            allFieldCriteria.add(criteria);
        });

        return allFieldCriteria;
    }
}
