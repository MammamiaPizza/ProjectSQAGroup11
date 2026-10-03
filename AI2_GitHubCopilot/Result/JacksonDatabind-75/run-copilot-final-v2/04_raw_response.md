@Test(expected = IllegalArgumentException.class)
public void testShapeObjectThrowsException() throws Exception {
    // Verify that using Shape.OBJECT throws an exception as per bugfix
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValueAsString(new Object() {
        @JsonFormat(shape = Shape.OBJECT)
        public Color getColor() { return Color.GREEN; }
    });
}

@Test
public void testShapeScalarWithIndexFeature() throws Exception {
    // Cover branches where shape is SCALAR and indexing is enabled
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    String json = mapper.writeValueAsString(new Object() {
        @JsonFormat(shape = Shape.SCALAR)
        public Color getColor() { return Color.GREEN; }
    });
    assertEquals("{"color":1}", json);
}

@Test
public void testAcceptJsonFormatVisitor() throws Exception {
    // Cover acceptJsonFormatVisitor method branches
    ObjectMapper mapper = new ObjectMapper();
    JavaType type = mapper.constructType(Color.class);
    SerializerProvider prov = mapper.getSerializerProvider();
    JsonSerializer<Object> ser = prov.findValueSerializer(type);
    final Set<String> enumNames = new java.util.LinkedHashSet<String>();
    JsonFormatVisitorWrapper visitor = new JsonFormatVisitorWrapper.Base(prov) {
        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType t) {
            return new JsonStringFormatVisitor.Base() {
                @Override
                public void enumTypes(Set<String> enums) {
                    enumNames.addAll(enums);
                }
            };
        }
    };
    ser.acceptJsonFormatVisitor(visitor, type);
    assertTrue(enumNames.containsAll(java.util.Arrays.asList("RED", "GREEN", "BLUE")));
}

@Test
public void testGetSchemaNonIndexSerialization() throws Exception {
    // Cover getSchema when _serializeAsIndex is false
    ObjectMapper mapper = new ObjectMapper();
    SerializerProvider prov = mapper.getSerializerProvider();
    JsonSerializer<Object> ser = prov.findValueSerializer(Color.class);
    JsonNode schema = ser.getSchema(prov, null);
    assertEquals("object", schema.get("type").asText());
    assertTrue(schema.has("enum"));
    ArrayNode enumNode = (ArrayNode) schema.get("enum");
    assertNotNull(enumNode);
    Set<String> enumValues = new java.util.LinkedHashSet<String>();
    for (JsonNode n : enumNode) {
        enumValues.add(n.asText());
    }
    assertTrue(enumValues.containsAll(java.util.Arrays.asList("RED", "GREEN", "BLUE")));
}