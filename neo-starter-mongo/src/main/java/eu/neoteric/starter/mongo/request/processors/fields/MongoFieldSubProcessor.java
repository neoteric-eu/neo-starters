package eu.neoteric.starter.mongo.request.processors.fields;

import eu.neoteric.starter.mongo.request.FieldMapper;
import eu.neoteric.starter.request.RequestField;
import eu.neoteric.starter.request.RequestObject;
import eu.neoteric.starter.request.RequestObjectType;
import org.springframework.data.mongodb.core.query.Criteria;

public interface MongoFieldSubProcessor<T extends RequestObject> {

    Boolean apply(RequestObjectType key);

    Criteria build(RequestField parentField, T field, Object fieldValues, FieldMapper fieldMapper);
}
