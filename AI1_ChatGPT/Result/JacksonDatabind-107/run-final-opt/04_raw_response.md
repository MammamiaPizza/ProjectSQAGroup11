@org.junit.Test
public void unknownTypeIdIsNullifiedWhenInvalidSubtypeFailuresAreDisabled() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

    GenericContent result = mapper.readValue(
            "{\"innerObjects\":[{\"type\":\"known\",\"name\":\"first\"},{\"type\":\"unknown\",\"name\":\"ignored\"}]}",
            GenericContent.class);

    org.junit.Assert.assertEquals(2, result.innerObjects.size());
    org.junit.Assert.assertEquals(KnownContent.class, result.innerObjects.get(0).getClass());
    org.junit.Assert.assertNull(result.innerObjects.get(1));
}

@org.junit.Test
public void missingTypeIdReportsCollectionPath() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    try {
        mapper.readValue("{\"innerObjects\":[{\"name\":\"missing\"}]}", GenericContent.class);
        org.junit.Assert.fail("Expected missing type id to fail");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertEquals(2, e.getPath().size());
    }
}