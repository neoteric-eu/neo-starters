package com.neoteric.starter.mongo.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.neoteric.starter.mongo.MongoConvertersAutoConfiguration;
import com.neoteric.starter.mongo.model.FooModel;
import com.neoteric.starter.mongo.model.FooModelMother;
import com.neoteric.starter.mongo.sort.RequestParamsSortBuilder;
import com.neoteric.starter.mongo.test.EmbeddedMongoTest;
import com.neoteric.starter.mongo.test.NeotericEmbeddedMongoAutoConfiguration;
import com.neoteric.starter.request.FiltersParser;
import com.neoteric.starter.request.RequestObject;
import com.neoteric.starter.request.sort.RequestSort;
import com.neoteric.starter.request.sort.SortParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@EmbeddedMongoTest(dropCollections = "FooModel")
@ContextConfiguration(classes = {MongoConvertersAutoConfiguration.class,
        NeotericEmbeddedMongoAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class})
public class MongoCriteriaIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void initCriteriaTest() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
    }

    @Test
    public void testReturnObjectBasedOnStartsWithCriteria() throws Exception {
        mongoTemplate.insert(FooModel.newBuilder().setName("Johnny").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Paul").build());

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("startsWithFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(FooModel.newBuilder().setName("Johnny").build());
    }

    @Test
    public void testReturnObjectBasedOnInCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("inFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("John", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
    }

    @Test
    public void testReturnObjectBasedOnNinCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("ninFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
    }

    @Test
    public void testReturnObjectBasedOnGtCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("gtFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
    }

    @Test
    public void testReturnObjectBasedOnGteCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("gteFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
    }

    @Test
    public void testReturnObjectBasedOnLtCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("ltFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("John", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
    }

    @Test
    public void testReturnObjectBasedOnLteCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("lteFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("John", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
        assertThat(results.get(2)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
    }

    @Test
    public void testReturnObjectBasedOnEqCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("eqFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
    }

    @Test
    public void testReturnObjectBasedOnNeqCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("John", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("neqFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("John", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
    }

    @Test
    public void testReturnObjectBasedOnNameCriteria() throws Exception {
        mongoTemplate.insert(FooModel.newBuilder().setName("Johnny").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Paul").build());

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("basicOperatorsFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(FooModel.newBuilder().setName("Johnny").build());
    }

    @Test
    public void testReturnObjectBasedOnLogicalOperatorCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Paul", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Adam", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("logicalOperator.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Johnny", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Adam", 7));
    }

    @Test
    public void testReturnObjectBasedOnAndedCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("multipleRootElements.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
    }

    @Test
    public void testReturnObjectBasedOnMultipleOrCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Barry", 2));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 9));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("multipleOrElements.json"));
        List<FooModel> results = performCriteriaCall(criteria);

        assertThat(results.size()).isEqualTo(4);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Johnny", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
        assertThat(results.get(2)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
        assertThat(results.get(3)).isEqualTo(FooModelMother.fullyPopulated("Bogdan", 9));
    }

    @Test
    public void testReturnObjectBasedOnArrayOperatorsCriteria() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Barry", 2));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 9));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("basicArrayOperators.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Johnny", 1));
    }

    @Test
    public void testReturnObjectsSorted() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 9));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Jill", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 1));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Barry", 2));

        List<FooModel> results = performCriteriaCall(new Criteria(), RequestParamsSortBuilder.newBuilder().build(readSortFromResources("sort.json")).get());
        assertThat(results.size()).isEqualTo(5);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Johnny", 1));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Jill", 5));
        assertThat(results.get(2)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
        assertThat(results.get(3)).isEqualTo(FooModelMother.fullyPopulated("Bogdan", 9));
        assertThat(results.get(4)).isEqualTo(FooModelMother.fullyPopulated("Barry", 2));
    }

    @Test
    public void testReturnObjectsSortedByMultipleFields() throws Exception {
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 7));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 2));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 3));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 5));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 1));

        List<FooModel> results = performCriteriaCall(new Criteria(), RequestParamsSortBuilder.newBuilder().build(readSortFromResources("multipleSort.json")).get());
        assertThat(results.size()).isEqualTo(6);
        FooModel fooModel = results.get(0);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("James", 2));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("James", 5));
        assertThat(results.get(2)).isEqualTo(FooModelMother.fullyPopulated("James", 7));
        assertThat(results.get(3)).isEqualTo(FooModelMother.fullyPopulated("Bogdan", 1));
        assertThat(results.get(4)).isEqualTo(FooModelMother.fullyPopulated("Bogdan", 3));
        assertThat(results.get(5)).isEqualTo(FooModelMother.fullyPopulated("Bogdan", 5));
    }

    @Test
    public void testReturnObjectsBasedOnFiltersWithAdditionalCriteria() throws Exception {
        mongoTemplate.insert(FooModel.newBuilder().setName("Johnny").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("James").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Julian").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Bob").build());

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(
                Criteria.where("name").in(Lists.newArrayList("James", "Julian")), readFiltersFromResources("startsWithJFilters.json"));
        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModel.newBuilder().setName("James").build());
        assertThat(results.get(1)).isEqualTo(FooModel.newBuilder().setName("Julian").build());
    }

    @Test
    public void testReturnObjectsAdditionalCriteriaThatAreNotRemapped() throws Exception {
        mongoTemplate.insert(FooModel.newBuilder().setName("Johnny").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("James").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Julian").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Bob").build());

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(
                Criteria.where("name").in(Lists.newArrayList("James", "Julian")),
                readFiltersFromResources("emptyFilters.json"),
                FieldMapper.of(ImmutableMap.of("apiName", "name")));

        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModel.newBuilder().setName("James").build());
        assertThat(results.get(1)).isEqualTo(FooModel.newBuilder().setName("Julian").build());
    }

    @Test
    public void testReturnNoObjectsDueToFactThatAdditionalCriteriaShouldContainRemappedValues() throws Exception {
        mongoTemplate.insert(FooModel.newBuilder().setName("Johnny").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("James").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Julian").build());
        mongoTemplate.insert(FooModel.newBuilder().setName("Bob").build());

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(
                Criteria.where("apiName").in(Lists.newArrayList("James", "Julian")),
                readFiltersFromResources("emptyFilters.json"),
                FieldMapper.of(ImmutableMap.of("apiName", "name")));

        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void testReturnObjectsBasedOnDateLtParameters() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 7, fixedDateWithOffset(1)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 2, fixedDateWithOffset(2)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Julian", 5, fixedDateWithOffset(3)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Adam", 3, fixedDateWithOffset(4)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bob", 5, fixedDateWithOffset(5)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 1, fixedDateWithOffset(6)));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(
                readFiltersFromResources("zonedDateTimeLtFilters.json"));

        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Johnny", 7, fixedDateWithOffset(1)));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("James", 2, fixedDateWithOffset(2)));
    }

    @Test
    public void testReturnObjectsBasedOnDateGtParameters() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        mongoTemplate.insert(FooModelMother.fullyPopulated("Johnny", 7, fixedDateWithOffset(1)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("James", 2, fixedDateWithOffset(2)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Julian", 5, fixedDateWithOffset(3)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Adam", 3, fixedDateWithOffset(4)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bob", 5, fixedDateWithOffset(5)));
        mongoTemplate.insert(FooModelMother.fullyPopulated("Bogdan", 1, fixedDateWithOffset(6)));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(
                readFiltersFromResources("zonedDateTimeGtFilters.json"));

        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0)).isEqualTo(FooModelMother.fullyPopulated("Adam", 3, fixedDateWithOffset(4)));
        assertThat(results.get(1)).isEqualTo(FooModelMother.fullyPopulated("Bob", 5, fixedDateWithOffset(5)));
        assertThat(results.get(2)).isEqualTo(FooModelMother.fullyPopulated("Bogdan", 1, fixedDateWithOffset(6)));
    }

    @Test
    public void testReturnObjectBasedOnAllCriteria() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        mongoTemplate.insert(FooModelMother.withTags("Johnny", "Neoteric", "Devops", "Java"));
        mongoTemplate.insert(FooModelMother.withTags("James", "Work", "Java"));
        mongoTemplate.insert(FooModelMother.withTags("Julian", "MongoDB", "Java", "Neoteric"));
        mongoTemplate.insert(FooModelMother.withTags("Adam", "Neoteric", "JS", "Devops"));
        mongoTemplate.insert(FooModelMother.withTags("Bob", "Neoteric", "Java", "Intellij"));
        mongoTemplate.insert(FooModelMother.withTags("Bogdan", "Java"));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("allFilters.json"));

        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0)).isEqualTo(FooModelMother.withTags("Johnny", "Neoteric", "Devops", "Java"));
        assertThat(results.get(1)).isEqualTo(FooModelMother.withTags("Julian", "MongoDB", "Java", "Neoteric"));
        assertThat(results.get(2)).isEqualTo(FooModelMother.withTags("Bob", "Neoteric", "Java", "Intellij"));
    }

    @Test
    public void testReturnObjectBasedOnAllAndInCriteria() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        mongoTemplate.insert(FooModelMother.withTags("Johnny", "Neoteric", "Devops", "Java"));
        mongoTemplate.insert(FooModelMother.withTags("James", "Work", "Java"));
        mongoTemplate.insert(FooModelMother.withTags("Julian", "MongoDB", "Java", "Neoteric"));
        mongoTemplate.insert(FooModelMother.withTags("Adam", "Neoteric", "JS", "Devops"));
        mongoTemplate.insert(FooModelMother.withTags("Bob", "Neoteric", "Java", "Intellij"));
        mongoTemplate.insert(FooModelMother.withTags("Bogdan", "Java"));

        Criteria criteria = RequestParamsCriteriaBuilder.newBuilder().build(readFiltersFromResources("allAndInFilters.json"));

        List<FooModel> results = performCriteriaCall(criteria);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo(FooModelMother.withTags("Johnny", "Neoteric", "Devops", "Java"));
        assertThat(results.get(1)).isEqualTo(FooModelMother.withTags("Julian", "MongoDB", "Java", "Neoteric"));
    }

    private ZonedDateTime fixedDateWithOffset(int days) {
        return ZonedDateTime.of(2016, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")).plusDays(days - 1);
    }

    private List<FooModel> performCriteriaCall(Criteria criteria) {
        Query query = new Query()
                .addCriteria(criteria);

        return mongoTemplate.find(query, FooModel.class);
    }

    private List<FooModel> performCriteriaCall(Criteria criteria, Sort sort) {
        Query query = new Query()
                .addCriteria(criteria)
                .with(sort);

        return mongoTemplate.find(query, FooModel.class);
    }

    private Map<RequestObject, Object> readFiltersFromResources(String resourceName) throws IOException {
        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/criteria-tests/" + resourceName));
        ObjectMapper mapper = new ObjectMapper();
        return FiltersParser.parseFilters(mapper.readValue(jsonBytes, Map.class));
    }

    private List<RequestSort> readSortFromResources(String resourceName) throws IOException {
        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/criteria-tests/" + resourceName));
        ObjectMapper mapper = new ObjectMapper();
        return SortParser.parseSort(mapper.readValue(jsonBytes, Map.class));
    }
}