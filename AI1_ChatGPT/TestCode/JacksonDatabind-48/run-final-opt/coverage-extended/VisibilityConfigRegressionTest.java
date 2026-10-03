import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VisibilityConfigRegressionTest {

    private static class DeserializationBean {
        @JsonProperty("groupname")
        private String groupname;

        public void setName(String value) {
        }
    }

    private static class SerializationBean {
        private String groupname = "staff";

        public String getName() {
            return "visible-getter";
        }
    }

    @Test
    public void defaultDeserializationVisibilityFindsPublicSetterAndExplicitField() {
        ObjectMapper mapper = new ObjectMapper();

        Set<String> properties = propertyNames(mapper.getDeserializationConfig()
                .introspect(mapper.constructType(DeserializationBean.class)));

        assertEquals(2, properties.size());
        assertTrue(properties.contains("name"));
        assertTrue(properties.contains("groupname"));
    }

    @Test
    public void disablingSetterVisibilityKeepsOnlyExplicitFieldForDeserialization() {
        ObjectMapper mapper = mapperWithFieldVisibilityAndNoSetters();

        Set<String> properties = propertyNames(mapper.getDeserializationConfig()
                .introspect(mapper.constructType(DeserializationBean.class)));

        assertEquals(1, properties.size());
        assertTrue(properties.contains("groupname"));
    }

    @Test
    public void explicitFieldCanStillBeDeserializedWhenSettersAreHidden() throws Exception {
        ObjectMapper mapper = mapperWithFieldVisibilityAndNoSetters();

        DeserializationBean bean = mapper.readValue(
                "{\"groupname\":\"staff\"}", DeserializationBean.class);

        assertEquals("staff", bean.groupname);
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void hiddenSetterDoesNotAcceptItsPropertyDuringDeserialization() throws Exception {
        ObjectMapper mapper = mapperWithFieldVisibilityAndNoSetters();

        mapper.readValue("{\"name\":\"should-not-bind\"}", DeserializationBean.class);
    }

    @Test
    public void serializationVisibilityRetainsFieldOverrideAfterGetterOverride() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);

        Set<String> properties = propertyNames(mapper.getSerializationConfig()
                .introspect(mapper.constructType(SerializationBean.class)));

        assertEquals(1, properties.size());
        assertTrue(properties.contains("groupname"));
        assertEquals("{\"groupname\":\"staff\"}",
                mapper.writeValueAsString(new SerializationBean()));
    }

    private ObjectMapper mapperWithFieldVisibilityAndNoSetters() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        return mapper;
    }

    private Set<String> propertyNames(BeanDescription description) {
        Set<String> names = new HashSet<String>();
        for (BeanPropertyDefinition property : description.findProperties()) {
            names.add(property.getName());
        }
        return names;
    }

@org.junit.Test
public void deserializationConfigRetainsSetterVisibilityOverrideWhenMapperFeaturesChange()
        throws Exception {
    com.fasterxml.jackson.databind.DeserializationConfig config =
            new com.fasterxml.jackson.databind.ObjectMapper().getDeserializationConfig()
                    .withVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.SETTER,
                            com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE);

    com.fasterxml.jackson.databind.DeserializationConfig changed =
            config.without(com.fasterxml.jackson.databind.MapperFeature.AUTO_DETECT_FIELDS);

    java.lang.reflect.Method method = String.class.getMethod("toString");
    org.junit.Assert.assertFalse(config.getDefaultVisibilityChecker().isSetterVisible(method));
    org.junit.Assert.assertFalse(changed.getDefaultVisibilityChecker().isSetterVisible(method));
}

@org.junit.Test
public void serializationConfigRetainsSetterVisibilityOverrideWhenMapperFeaturesChange()
        throws Exception {
    com.fasterxml.jackson.databind.SerializationConfig config =
            new com.fasterxml.jackson.databind.ObjectMapper().getSerializationConfig()
                    .withVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.SETTER,
                            com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE);

    com.fasterxml.jackson.databind.SerializationConfig changed =
            config.without(com.fasterxml.jackson.databind.MapperFeature.AUTO_DETECT_FIELDS);

    java.lang.reflect.Method method = String.class.getMethod("toString");
    org.junit.Assert.assertFalse(config.getDefaultVisibilityChecker().isSetterVisible(method));
    org.junit.Assert.assertFalse(changed.getDefaultVisibilityChecker().isSetterVisible(method));
}

@org.junit.Test
public void deserializationConfigParserFeatureOverrideIsAppliedAgainstFactoryDefaults() {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();
    com.fasterxml.jackson.databind.DeserializationConfig enabled =
            new com.fasterxml.jackson.databind.ObjectMapper().getDeserializationConfig()
                    .with(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS);
    com.fasterxml.jackson.databind.DeserializationConfig disabled =
            enabled.without(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS);

    org.junit.Assert.assertTrue(enabled.isEnabled(
            com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, factory));
    org.junit.Assert.assertFalse(disabled.isEnabled(
            com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, factory));
}

@org.junit.Test
public void serializationConfigGeneratorFeatureOverrideIsAppliedAgainstFactoryDefaults() {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();
    com.fasterxml.jackson.databind.SerializationConfig enabled =
            new com.fasterxml.jackson.databind.ObjectMapper().getSerializationConfig()
                    .with(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
    com.fasterxml.jackson.databind.SerializationConfig disabled =
            enabled.without(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);

    org.junit.Assert.assertTrue(enabled.isEnabled(
            com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, factory));
    org.junit.Assert.assertFalse(disabled.isEnabled(
            com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, factory));
}
}
