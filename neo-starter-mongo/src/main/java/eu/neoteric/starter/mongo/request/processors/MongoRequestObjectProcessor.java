package eu.neoteric.starter.mongo.request.processors;

import eu.neoteric.starter.mongo.request.FieldMapper;
import eu.neoteric.starter.request.RequestObject;
import eu.neoteric.starter.request.RequestObjectType;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

public interface MongoRequestObjectProcessor<T extends RequestObject> {

    Boolean apply(RequestObjectType key);

    List<Criteria> build(T field, Map<RequestObject, Object> fieldValues, FieldMapper fieldMapper);
}
