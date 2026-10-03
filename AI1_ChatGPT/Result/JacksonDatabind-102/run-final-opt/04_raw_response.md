@org.junit.Test
public void rootSqlDateUsesMapperDateFormat() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy.MM.dd",
            java.util.Locale.US);
    format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
    mapper.setDateFormat(format);

    org.junit.Assert.assertEquals("\"1980.04.14\"",
            mapper.writeValueAsString(new java.sql.Date(324547200000L)));
}

@org.junit.Test
public void propertyDateFormatAppliesConfiguredTimezone() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("{\"date\":\"1970-01-01 02:00\"}",
            mapper.writeValueAsString(new DateWithTimezoneFormat(new java.util.Date(0L))));
}

@org.junit.Test
public void sqlDateFormatVisitorReflectsTimestampConfiguration() throws Exception {
    final boolean[] numericVisitorCalled = new boolean[1];
    com.fasterxml.jackson.databind.ObjectMapper numericMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    numericMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    numericMapper.acceptJsonFormatVisitor(java.sql.Date.class,
            new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {
                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor expectIntegerFormat(
                        com.fasterxml.jackson.databind.JavaType type) {
                    numericVisitorCalled[0] = true;
                    return null;
                }
            });
    org.junit.Assert.assertTrue(numericVisitorCalled[0]);

    final boolean[] stringVisitorCalled = new boolean[1];
    com.fasterxml.jackson.databind.ObjectMapper stringMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    stringMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    stringMapper.acceptJsonFormatVisitor(java.sql.Date.class,
            new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {
                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor expectStringFormat(
                        com.fasterxml.jackson.databind.JavaType type) {
                    stringVisitorCalled[0] = true;
                    return null;
                }
            });
    org.junit.Assert.assertTrue(stringVisitorCalled[0]);
}

public static class DateWithTimezoneFormat {
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+02:00")
    public java.util.Date date;

    public DateWithTimezoneFormat(java.util.Date date) {
        this.date = date;
    }
}