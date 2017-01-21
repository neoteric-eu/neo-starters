package eu.neoteric.starter.request.sort;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SortParserTest {

    @Test
    public void shouldProduceEmptySort() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("EmptySort.json");

        List<RequestSort> requestSorts = SortParser.parseSort(rawSort);
        Assertions.assertThat(requestSorts)
                .hasSize(0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForIncorrectRootElement() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("IncorrectSortRoot.json");

        SortParser.parseSort(rawSort);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForMultipleSortFieldsWithIncorrectOrderOperator() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("MultipleSortWithIncorrectOrderOperator.json");

        SortParser.parseSort(rawSort);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForMultipleSortFieldsWithoutOrderOperator() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("MultipleSortWithoutOrderOperator.json");

        SortParser.parseSort(rawSort);
    }

    @Test
    public void testSingleFieldWithOperator() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("SingleSortRequest.json");

        List<RequestSort> requestSorts = SortParser.parseSort(rawSort);
        Assertions.assertThat(requestSorts)
                .hasSize(1)
                .contains(RequestSort.of("name", SortType.ASC));
    }

    @Test
    public void testMultipleFieldsSort() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("MultipleSortRequest.json");

        List<RequestSort> requestSorts = SortParser.parseSort(rawSort);
        Assertions.assertThat(requestSorts)
                .hasSize(2)
                .containsSequence(RequestSort.of("name", SortType.ASC), RequestSort.of("last", SortType.DESC));
    }

    @Test
    public void testMultipleFieldsSortWithCustomOrder() throws Exception {
        Map<String, Object> rawSort = readFiltersFromResources("MultipleSortWithCustomOrderRequest.json");

        List<RequestSort> requestSorts = SortParser.parseSort(rawSort);
        Assertions.assertThat(requestSorts)
                .hasSize(2)
                .containsSequence(RequestSort.of("last", SortType.DESC), RequestSort.of("name", SortType.ASC));
    }

    private Map<String, Object> readFiltersFromResources(String resourceName) throws IOException {
        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/sort/" + resourceName));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonBytes, Map.class);
    }

}
