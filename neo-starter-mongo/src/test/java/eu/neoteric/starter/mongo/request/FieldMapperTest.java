package eu.neoteric.starter.mongo.request;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldMapperTest {

    @Test
    public void testForNameWithRemappedValueShouldReturnRemappedValue() {
        String remappedValue = FieldMapper.of(ImmutableMap.of("name", "remappedName")).get("name");
        assertThat(remappedValue).isEqualTo("remappedName");
    }

    @Test
    public void testForNameWithoutRemappedValueShouldReturnOriginalName() {
        String remappedValue = FieldMapper.of(ImmutableMap.of("foo", "remappedName")).get("name");
        assertThat(remappedValue).isEqualTo("name");
    }

    @Test
    public void testForEmptyFieldMappingShouldReturnOriginalName() {
        String remappedValue = FieldMapper.of(ImmutableMap.of()).get("name");
        assertThat(remappedValue).isEqualTo("name");
    }
}
