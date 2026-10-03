@org.junit.Test
public void publicBeanWithDefaultConstructorDeserializesProperties() throws java.io.IOException {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.awt.Point point = mapper.readValue("{\"x\":4,\"y\":-3}", java.awt.Point.class);

    org.junit.Assert.assertEquals(4, point.x);
    org.junit.Assert.assertEquals(-3, point.y);
}

@org.junit.Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void beanWithoutScalarCreatorRejectsNumericInput() throws java.io.IOException {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    mapper.readValue("13", java.awt.Point.class);
}

@org.junit.Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void beanRejectsUnknownPropertyByDefault() throws java.io.IOException {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    mapper.readValue("{\"x\":1,\"y\":2,\"unexpected\":3}", java.awt.Point.class);
}