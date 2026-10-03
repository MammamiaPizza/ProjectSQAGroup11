@org.junit.Test
public void testStringShapeWithoutPatternUsesProviderTimeZone() throws Exception {
    class Value {
        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
        public java.util.Date date = new java.util.Date(0L);
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.setTimeZone(java.util.TimeZone.getTimeZone("GMT-03:00"));

    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(mapper.writeValueAsString(new Value()));
    org.junit.Assert.assertEquals("1969-12-31T21:00:00.000-0300", node.get("date").asText());
}

@org.junit.Test
public void testStringShapeWithoutPatternHonorsExplicitTimeZone() throws Exception {
    class Value {
        @com.fasterxml.jackson.annotation.JsonFormat(
                shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING,
                timezone = "GMT+02:00")
        public java.util.Date date = new java.util.Date(0L);
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(mapper.writeValueAsString(new Value()));
    org.junit.Assert.assertEquals("1970-01-01T02:00:00.000+0200", node.get("date").asText());
}

@org.junit.Test
public void testStringShapePatternUsesConfiguredLocale() throws Exception {
    class Value {
        @com.fasterxml.jackson.annotation.JsonFormat(
                shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING,
                pattern = "MMMM",
                locale = "fr",
                timezone = "UTC")
        public java.util.Date date = new java.util.Date(0L);
    }

    java.text.SimpleDateFormat expectedFormat =
            new java.text.SimpleDateFormat("MMMM", java.util.Locale.FRENCH);
    expectedFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(mapper.writeValueAsString(new Value()));

    org.junit.Assert.assertEquals(expectedFormat.format(new java.util.Date(0L)), node.get("date").asText());
}

@org.junit.Test
public void testRootDateAndNonEmptyDateHandling() throws Exception {
    class Value {
        public java.util.Date date;

        Value(long timestamp) {
            date = new java.util.Date(timestamp);
        }
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    org.junit.Assert.assertEquals("0", mapper.writeValueAsString(new java.util.Date(0L)));

    mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY);
    org.junit.Assert.assertFalse(mapper.readTree(mapper.writeValueAsString(new Value(0L))).has("date"));
    org.junit.Assert.assertTrue(mapper.readTree(mapper.writeValueAsString(new Value(1L))).has("date"));
}