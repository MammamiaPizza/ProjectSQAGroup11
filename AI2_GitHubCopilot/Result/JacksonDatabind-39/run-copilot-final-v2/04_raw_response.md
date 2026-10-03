@Test
public void testDeserializeWithTypeStartArray() throws Exception {
    com.fasterxml.jackson.core.JsonParser p = new
com.fasterxml.jackson.core.JsonFactory().createParser("[1,2]");
    p.nextToken(); // START_ARRAY
    Object result =
com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer.instance.deserializeWithType(
        p, (com.fasterxml.jackson.databind.DeserializationContext) null,
        (com.fasterxml.jackson.databind.jsontype.TypeDeserializer) null);
    org.junit.Assert.assertNull(result);
    org.junit.Assert.assertNull(p.getCurrentToken());
}

@Test
public void testDeserializeWithTypeFieldName() throws Exception {
    com.fasterxml.jackson.core.JsonParser p = new
com.fasterxml.jackson.core.JsonFactory().createParser("{"a":[1,2]}");
    p.nextToken(); // START_OBJECT
    p.nextToken(); // FIELD_NAME "a"
    Object result =
com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer.instance.deserializeWithType(
        p, (com.fasterxml.jackson.databind.DeserializationContext) null,
        (com.fasterxml.jackson.databind.jsontype.TypeDeserializer) null);
    org.junit.Assert.assertNull(result);
    org.junit.Assert.assertNull(p.getCurrentToken());
}

@Test
public void testDeserializeWithTypeScalar() throws Exception {
    com.fasterxml.jackson.core.JsonParser p = new
com.fasterxml.jackson.core.JsonFactory().createParser("42");
    p.nextToken(); // VALUE_NUMBER_INT
    Object result =
com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer.instance.deserializeWithType(
        p, (com.fasterxml.jackson.databind.DeserializationContext) null,
        (com.fasterxml.jackson.databind.jsontype.TypeDeserializer) null);
    org.junit.Assert.assertNull(result);
}