package com.neoteric.starter.mongo.request.processors.fields;

import com.google.common.collect.ImmutableList;
import com.neoteric.starter.mongo.request.FieldMapper;
import com.neoteric.starter.request.RequestField;
import com.neoteric.starter.request.RequestObjectType;
import com.neoteric.starter.request.RequestOperator;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.neoteric.starter.mongo.request.Mappings.OPERATORS;

public enum MongoFieldToOperatorSubProcessor implements MongoFieldSubProcessor<RequestOperator> {

    INSTANCE;
    private final List<OperatorValueParser> operatorValueParsers = ImmutableList.of(
            ZonedDateTimeValueParser.INSTANCE, GeneralOperatorValueParser.INSTANCE);

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.OPERATOR.equals(key);
    }

    @Override
    // TODO : how to check value, it can be String, Boolean, Integer, List !!, Double and maybe more
    public Criteria build(RequestField field, RequestOperator operator, Object operatorValue, FieldMapper fieldMapper) {
        String remappedName = fieldMapper.get(field.getFieldName());
        Criteria fieldCriteria = Criteria.where(remappedName);

        Object parsedValue = operatorValueParsers.stream()
                .filter(operatorValueParser -> operatorValueParser.apply(operatorValue))
                .findFirst()
                .map(operatorValueParser -> operatorValueParser.parse(operatorValue)).get();
        return OPERATORS.get(operator.getOperator()).apply(fieldCriteria, parsedValue);
    }

    interface OperatorValueParser {
        boolean apply(Object operatorValue);

        Object parse(Object operatorValue);
    }

    enum ZonedDateTimeValueParser implements OperatorValueParser {
        INSTANCE;

        private DateTimeFormatter isoDateTime = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        @Override
        public boolean apply(Object operatorValue) {
            try {
                isoDateTime.parse(operatorValue.toString());
                return true;

            } catch (DateTimeParseException ex) {
                return false;
            }
        }

        @Override
        public Object parse(Object operatorValue) {
            return ZonedDateTime.from(isoDateTime.parse(operatorValue.toString()));
        }
    }

    enum GeneralOperatorValueParser implements OperatorValueParser {
        INSTANCE;

        @Override
        public boolean apply(Object operatorValue) {
            return true;
        }

        @Override
        public Object parse(Object operatorValue) {
            return operatorValue;
        }
    }
}
