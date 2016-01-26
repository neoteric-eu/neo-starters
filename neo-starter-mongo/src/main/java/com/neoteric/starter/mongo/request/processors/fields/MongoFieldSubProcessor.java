package com.neoteric.starter.mongo.request.processors.fields;

import com.neoteric.starter.mongo.request.FieldMapper;
import com.neoteric.starter.request.RequestField;
import com.neoteric.starter.request.RequestObject;
import com.neoteric.starter.request.RequestObjectType;
import org.springframework.data.mongodb.core.query.Criteria;

public interface MongoFieldSubProcessor<T extends RequestObject> {

    Boolean apply(RequestObjectType key);

    Criteria build(RequestField parentField, T field, Object fieldValues, FieldMapper fieldMapper);
}
