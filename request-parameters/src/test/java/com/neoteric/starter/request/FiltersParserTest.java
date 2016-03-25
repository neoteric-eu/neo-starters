package com.neoteric.starter.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class FiltersParserTest {

    @Test
    public void shouldProduceEmptyFilters() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("EmptyFilters.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
        assertThat(filters)
                .hasSize(0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForIncorrectRootElement() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("IncorrectFiltersStartingNeitherFromFieldOrAnOperator.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
    }

    @Test
    public void testSingleFieldWithOperator() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("SingleFieldWithOperator.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
        assertThat(filters)
                .hasSize(1)
                .containsOnlyKeys(RequestField.of("name"));

        assertThat((Map) filters.get(RequestField.of("name")))
                .containsEntry(RequestOperator.of(OperatorType.EQUAL.getName()), "John");
    }

    @Test
    public void testMultipleFieldsWithOperator() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("MultipleFieldsWithOperator.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
        assertThat(filters)
                .hasSize(2)
                .containsOnlyKeys(RequestField.of("name"), RequestField.of("size"));

        assertThat((Map) filters.get(RequestField.of("name")))
                .containsEntry(RequestOperator.of(OperatorType.EQUAL.getName()), "John");
        assertThat((Map) filters.get(RequestField.of("size")))
                .containsEntry(RequestOperator.of(OperatorType.GREATER_THAN_EQUAL.getName()), "5");
    }

    @Test
    public void testLogicalOperator() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("LogicalOperator.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
        assertThat(filters)
                .hasSize(1)
                .containsOnlyKeys(RequestLogicalOperator.of(LogicalOperatorType.OR));

        assertThat((Map) filters.get(RequestLogicalOperator.of(LogicalOperatorType.OR)))
                .hasSize(2)
                .containsEntry(RequestField.of("name"), ImmutableMap.of(RequestOperator.of(OperatorType.EQUAL), "John"))
                .containsEntry(RequestField.of("surname"), ImmutableMap.of(RequestOperator.of(OperatorType.STARTS_WITH), "Doe"));
    }

    @Test
    public void testMultipleNestedOperators() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("MultipleNestedOperators.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
        assertThat(filters)
                .hasSize(3)
                .containsOnlyKeys(RequestField.of("name"), RequestField.of("lastName"), RequestLogicalOperator.of(LogicalOperatorType.OR));

        assertThat((Map) filters.get(RequestField.of("name")))
                .hasSize(2)
                .containsEntry(RequestOperator.of(OperatorType.STARTS_WITH), "John")
                .containsEntry(RequestOperator.of(OperatorType.IN), Lists.newArrayList("Johnny", "Abc"));

        assertThat((Map) filters.get(RequestField.of("lastName")))
                .hasSize(1)
                .containsEntry(RequestLogicalOperator.of(LogicalOperatorType.OR), ImmutableMap.of(
                        RequestOperator.of(OperatorType.STARTS_WITH), "John",
                        RequestOperator.of(OperatorType.IN), Lists.newArrayList("Johnny", "Abc")
                ));

        assertThat((Map) filters.get(RequestLogicalOperator.of(LogicalOperatorType.OR)))
                .hasSize(2)
                .containsEntry(RequestField.of("name"), ImmutableMap.of(RequestOperator.of(OperatorType.EQUAL), "John"))
                .containsEntry(RequestField.of("last"), ImmutableMap.of(RequestOperator.of(OperatorType.EQUAL), "Doe"));
    }

    @Test
    public void testRegexOperator() throws Exception {
        Map<String, Object> rawFilters = readFiltersFromResources("RegexOperator.json");

        Map<RequestObject, Object> filters = FiltersParser.parseFilters(rawFilters);
        assertThat((Map) filters.get(RequestField.of("name")))
                .hasSize(1)
                .containsEntry(RequestOperator.of(OperatorType.REGEX), "John");
    }

    private Map<String, Object> readFiltersFromResources(String resourceName) throws IOException {
        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/requests/" + resourceName));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonBytes, Map.class);
    }

}
