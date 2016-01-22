package com.neoteric.starter.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.request.FiltersParser;
import com.neoteric.starter.request.RequestObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class RequestParamsCriteriaBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParamsCriteriaBuilderTest.class);

    @Test
    public void testSome() throws Exception {

        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/requestWithOrBetweenFields.json"));
//        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/request2.json"));
//        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/request3.json"));
//        byte[] jsonBytes = Files.readAllBytes(Paths.get("src/test/resources/request4.json"));


        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapa = mapper.readValue(jsonBytes, Map.class);

        LOG.warn("MAPA: {}", mapa);

        Map<RequestObject, Object> filterMap = FiltersParser.parseFilters(mapa);

        RequestParamsCriteriaBuilder criteriaBuilder = RequestParamsCriteriaBuilder.newBuilder();
        Criteria build = criteriaBuilder.build(filterMap);

        LOG.warn("Criteria: {}", build.getCriteriaObject());
    }

    @Test
    public void testCriteria() throws Exception {

        Criteria criteria = new Criteria().andOperator(
                new Criteria().orOperator(
                        Criteria.where("name").is("John"),
                        Criteria.where("last").is("Doe")),
                Criteria.where("createdAt").lt("timestamp"),
                Criteria.where("createdAt").gt("t4"),
                new Criteria().orOperator(
                        Criteria.where("updatedAt").lt("t1"),
                        Criteria.where("updatedAt").gt("t2")),
                Criteria.where("updatedAt").lt("timestamp3"));
        LOG.error("CRITERIA: {}", criteria.getCriteriaObject());

    }
}