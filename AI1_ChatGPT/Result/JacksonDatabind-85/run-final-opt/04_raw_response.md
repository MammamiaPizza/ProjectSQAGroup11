@org.junit.Test
public void stringShapeUsesConfiguredDefaultDateFormatWhenTimestampsAreEnabled() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
            "yyyy-MM-dd'X'HH:mm:ss", java.util.Locale.US);
    format.setTimeZone(java.util.TimeZone.getTimeZone("GMT+01:00"));
    mapper.setDateFormat(format);

    class Bean {
        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
        public java.util.Date date = new java.util.Date(0L);
    }

    org.junit.Assert.assertEquals("{\"date\":\"1970-01-01X01:00:00\"}",
            mapper.writeValueAsString(new Bean()));
}

@org.junit.Test
public void nonEmptyDatePropertyTreatsEpochAsEmpty() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    class Bean {
        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
        public java.util.Date epoch = new java.util.Date(0L);

        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
        public java.util.Date nonEmpty = new java.util.Date(1L);
    }

    org.junit.Assert.assertEquals("{\"nonEmpty\":1}", mapper.writeValueAsString(new Bean()));
}