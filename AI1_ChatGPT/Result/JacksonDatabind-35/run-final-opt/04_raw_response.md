@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void rejectsWrapperObjectWithoutTypeField() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT);

    mapper.readValue("{}", Object.class);
}

@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void rejectsWrapperObjectWithAdditionalFieldsAfterValue() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT);

    mapper.readValue("{\"java.util.ArrayList\":[],\"extra\":true}", Object.class);
}