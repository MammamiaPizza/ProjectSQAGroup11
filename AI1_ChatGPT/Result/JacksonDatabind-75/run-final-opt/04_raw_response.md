@Test
public void classAnnotatedNumberShapeUsesEnumOrdinal() throws Exception {
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER)
    enum NumericColor {
        RED, GREEN
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    org.junit.Assert.assertEquals("\"1\"".replace("\"", ""),
            mapper.writeValueAsString(NumericColor.GREEN));
}

@Test
public void enumSchemaReflectsGlobalIndexSerializationFeature() throws Exception {
    enum SchemaColor {
        RED, GREEN
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    org.junit.Assert.assertEquals("string",
            mapper.generateJsonSchema(SchemaColor.class).getSchemaNode().get("type").asText());

    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_INDEX);
    org.junit.Assert.assertEquals("integer",
            mapper.generateJsonSchema(SchemaColor.class).getSchemaNode().get("type").asText());
}

@Test
public void enumFormatVisitorReportsStringValuesAndIndexedNumberType() throws Exception {
    enum VisitorColor {
        RED, GREEN
    }

    final java.util.Set<String>[] stringValues = new java.util.Set[] { null };
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.acceptJsonFormatVisitor(VisitorColor.class,
            new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {
                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor expectStringFormat(
                        com.fasterxml.jackson.databind.JavaType type) {
                    return new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor.Base() {
                        @Override
                        public void enumTypes(java.util.Set<String> enums) {
                            stringValues[0] = new java.util.LinkedHashSet<String>(enums);
                        }
                    };
                }
            });
    org.junit.Assert.assertEquals(
            new java.util.LinkedHashSet<String>(java.util.Arrays.asList("RED", "GREEN")),
            stringValues[0]);

    final com.fasterxml.jackson.core.JsonParser.NumberType[] numberType =
            new com.fasterxml.jackson.core.JsonParser.NumberType[] { null };
    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_INDEX);
    mapper.acceptJsonFormatVisitor(VisitorColor.class,
            new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {
                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor expectIntegerFormat(
                        com.fasterxml.jackson.databind.JavaType type) {
                    return new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor.Base() {
                        @Override
                        public void numberType(com.fasterxml.jackson.core.JsonParser.NumberType type) {
                            numberType[0] = type;
                        }
                    };
                }
            });
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonParser.NumberType.INT, numberType[0]);
}

@Test
public void objectShapeIsRejectedForEnumProperty() throws Exception {
    enum ObjectColor {
        RED, GREEN
    }
    class ObjectShapeBean {
        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT)
        public ObjectColor color = ObjectColor.GREEN;
    }

    try {
        new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(new ObjectShapeBean());
        org.junit.Assert.fail("Expected enum OBJECT property shape to be rejected");
    } catch (Exception e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Unsupported serialization shape (OBJECT)"));
    }
}