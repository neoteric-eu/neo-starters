package com.neoteric.starter.mongo.request;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neoteric.starter.request.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestParamsCriteriaBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParamsCriteriaBuilderTest.class);

    @Test
    public void testEmptyFiltersShouldProduceEmptyQuery() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of();
        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(criteria).isEqualTo(new Criteria());
    }

    // starting from LogicalOperator
    @Test
    public void testFilterStartingWithLogicalOperatorShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(RequestField.of("name"),
                        ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "John")
                ));
        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().orOperator(Criteria.where("name").regex("^John", "i")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterStartingWithLogicalOperatorFollowedByLogicalOperatorShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(RequestLogicalOperator.of(LogicalOperatorType.OR), Maps.newHashMap()));
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterStartingWithLogicalOperatorFollowedByOperatorShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), Maps.newHashMap()));
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterStartingWithLogicalOperatorFollowedByOperatorWithIncorrectValueShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(RequestField.of("name"), "incorrectTypeValue"));
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test
    public void testFilterStartingWithLogicalOperatorFollowedByFieldShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(
                        RequestField.of("count"), ImmutableMap.of(RequestOperator.of(OperatorType.LESS_THAN), 5)
                ));
        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().orOperator(Criteria.where("count").lt(5)));
    }

    // starting from Operator
    @Test(expected = IllegalArgumentException.class)
    public void testFilterStartingWithOperatorInRootShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), Maps.newHashMap());
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    // starting from Field
    @Test
    public void testFilterStartingWithFieldShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(RequestField.of("name"),
                ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "John")
        );
        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(Criteria.where("name").regex("^John", "i"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterStartingWithFieldFollowedByFieldShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"),
                ImmutableMap.of(RequestField.of("surname"), Maps.newHashMap()));
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test
    public void testFilterStartingWithFieldFollowedByLogicalOperatorShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(
                        RequestLogicalOperator.of(LogicalOperatorType.OR),
                        ImmutableMap.of(
                                RequestOperator.of(OperatorType.STARTS_WITH), "John",
                                RequestOperator.of(OperatorType.IN), Lists.newArrayList("John", "Bob"))
                )
        );

        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().orOperator(
                Criteria.where("name").regex("^John", "i"),
                Criteria.where("name").in("John", "Bob")));
    }

    @Test
    public void testFilterStartingWithFieldFollowedByOperatorShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(
                        RequestOperator.of(OperatorType.STARTS_WITH), "John",
                        RequestOperator.of(OperatorType.IN), Lists.newArrayList("John", "Bob")
                )
        );

        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().andOperator(
                Criteria.where("name").regex("^John", "i"),
                Criteria.where("name").in("John", "Bob")));
    }

    // from the middle, only field can be in the middle
    @Test
    public void testFilterFromFieldFollowedByLogicalOperatorToOperatorShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(
                        RequestLogicalOperator.of(LogicalOperatorType.OR),
                        ImmutableMap.of(
                                RequestOperator.of(OperatorType.STARTS_WITH), "John",
                                RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
                ));


        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().orOperator(
                Criteria.where("name").regex("^John", "i"),
                Criteria.where("name").in("Doe", "Smith")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterFromFieldFollowedByLogicalOperatorToLogicalOperatorShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(
                        RequestLogicalOperator.of(LogicalOperatorType.OR),
                        ImmutableMap.of(RequestLogicalOperator.of(LogicalOperatorType.OR),
                                ImmutableMap.of(
                                        RequestOperator.of(OperatorType.STARTS_WITH), "John",
                                        RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
                        )
                ));


        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterFromFieldFollowedByLogicalOperatorWithIncorrectValue() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(
                        RequestLogicalOperator.of(LogicalOperatorType.OR), "incorrectTypeValue"
                ));


        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterFromFieldFollowedByLogicalOperatorToFieldShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(
                        RequestLogicalOperator.of(LogicalOperatorType.OR),
                        ImmutableMap.of(RequestField.of("lastName"),
                                ImmutableMap.of(
                                        RequestOperator.of(OperatorType.STARTS_WITH), "John",
                                        RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
                        )
                ));

        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFilterFromFieldFollowedByFieldShouldFail() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("foo"),
                ImmutableMap.of(RequestField.of("bar"), Maps.newHashMap()));
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test
    public void testFilterFromFieldFollowedByOperatorShouldProduceProperCriteria() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(
                        RequestField.of("name"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "John"),
                        RequestField.of("lastName"), ImmutableMap.of(RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
                )
        );

        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().orOperator(
                Criteria.where("name").regex("^John", "i"),
                Criteria.where("lastName").in("Doe", "Smith")));
    }

    // other tests
    @Test
    public void testOfAllOperators() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.builder()
                        .put(RequestOperator.of(OperatorType.STARTS_WITH), "John")
                        .put(RequestOperator.of(OperatorType.IN), Lists.newArrayList("John", "Bob"))
                        .put(RequestOperator.of(OperatorType.LESS_THAN), 4)
                        .put(RequestOperator.of(OperatorType.EQUAL), "John")
                        .put(RequestOperator.of(OperatorType.GREATER_THAN), 1)
                        .put(RequestOperator.of(OperatorType.GREATER_THAN_EQUAL), 2)
                        .put(RequestOperator.of(OperatorType.LESS_THAN_EQUAL), 3)
                        .put(RequestOperator.of(OperatorType.NOT_EQUAL), "Bob")
                        .put(RequestOperator.of(OperatorType.NOT_IN), Lists.newArrayList("Brian", "Adam"))
                        .build()
        );

        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().andOperator(
                Criteria.where("name").regex("^John", "i"),
                Criteria.where("name").in("John", "Bob"),
                Criteria.where("name").lt(4),
                Criteria.where("name").is("John"),
                Criteria.where("name").gt(1),
                Criteria.where("name").gte(2),
                Criteria.where("name").lte(3),
                Criteria.where("name").ne("Bob"),
                Criteria.where("name").nin("Brian", "Adam")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRootElementWithIncorrectValue() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR), "incorrectTypeValue");
        RequestParamsCriteriaBuilder.newBuilder().build(filters);
    }

    @Test
    public void testProduceCriteriaWithRemappedNames() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(
                        RequestField.of("name"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "John"),
                        RequestField.of("secondName"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "Bob"),
                        RequestField.of("lastName"), ImmutableMap.of(RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
                )
        );

        FieldMapper fieldMapper = FieldMapper.of(ImmutableMap.of(
                "name", "remappedName",
                "lastName", "remappedLastName"
        ));
        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters, fieldMapper);
        assertThat(result).isEqualTo(new Criteria().orOperator(
                Criteria.where("remappedName").regex("^John", "i"),
                Criteria.where("secondName").regex("^Bob", "i"),
                Criteria.where("remappedLastName").in("Doe", "Smith")));
    }

    @Test
    public void testProduceCriteriaWithInitialCriteriaIncluded() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestLogicalOperator.of(LogicalOperatorType.OR),
                ImmutableMap.of(
                        RequestField.of("name"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "John"),
                        RequestField.of("secondName"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "Bob"),
                        RequestField.of("lastName"), ImmutableMap.of(RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
                )
        );

        FieldMapper fieldMapper = FieldMapper.of(ImmutableMap.of());
        Criteria result = RequestParamsCriteriaBuilder.newBuilder()
                .build(Criteria.where("initialField").is("initialValue"), filters, fieldMapper);
        assertThat(result).isEqualTo(new Criteria().andOperator(
                Criteria.where("initialField").is("initialValue"),
                new Criteria().orOperator(
                        Criteria.where("name").regex("^John", "i"),
                        Criteria.where("secondName").regex("^Bob", "i"),
                        Criteria.where("lastName").in("Doe", "Smith"))
                )
        );
    }

    @Test
    public void testProduceCriteriaWithMultipleRootLevelElements() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("name"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "John"),
                RequestField.of("secondName"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "Bob"),
                RequestField.of("lastName"), ImmutableMap.of(RequestOperator.of(OperatorType.IN), Lists.newArrayList("Doe", "Smith"))
        );

        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        assertThat(result).isEqualTo(new Criteria().andOperator(
                Criteria.where("name").regex("^John", "i"),
                Criteria.where("secondName").regex("^Bob", "i"),
                Criteria.where("lastName").in("Doe", "Smith")));
    }

    @Test
    public void testProduceCriteriaWithDateElements() throws Exception {
        Map<RequestObject, Object> filters = ImmutableMap.of(
                RequestField.of("date"), ImmutableMap.of(RequestOperator.of(OperatorType.LESS_THAN), "2016-01-01T22:54:36.115Z")
        );

        Criteria result = RequestParamsCriteriaBuilder.newBuilder().build(filters);
        Criteria expectedCriteria = Criteria.where("date").lt(ZonedDateTime.of(2016, 1, 1, 22, 54, 36, 115000000, ZoneId.of("Z")));
        assertThat(result).isEqualTo(expectedCriteria);
    }
}