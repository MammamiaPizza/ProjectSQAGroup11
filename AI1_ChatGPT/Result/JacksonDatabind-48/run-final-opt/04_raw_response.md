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