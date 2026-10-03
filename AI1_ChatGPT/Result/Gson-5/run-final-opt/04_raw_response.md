public void testParseAcceptsHourOnlyTimeZoneOffsets() throws Exception {
  String positiveValue = "1970-01-01T01:00:00+01";
  java.text.ParsePosition positivePosition = new java.text.ParsePosition(0);
  java.util.Date positive = com.google.gson.internal.bind.util.ISO8601Utils.parse(
      positiveValue, positivePosition);
  assertEquals(0L, positive.getTime());
  assertEquals(positiveValue.length(), positivePosition.getIndex());

  String negativeValue = "1970-01-01T00:00:00-01";
  java.text.ParsePosition negativePosition = new java.text.ParsePosition(0);
  java.util.Date negative = com.google.gson.internal.bind.util.ISO8601Utils.parse(
      negativeValue, negativePosition);
  assertEquals(60L * 60L * 1000L, negative.getTime());
  assertEquals(negativeValue.length(), negativePosition.getIndex());
}

public void testFormatUsesRequestedMillisecondsAndTimeZone() {
  java.util.Date date = new java.util.Date(123L);

  assertEquals("1970-01-01T00:00:00Z",
      com.google.gson.internal.bind.util.ISO8601Utils.format(date));
  assertEquals("1970-01-01T00:00:00.123Z",
      com.google.gson.internal.bind.util.ISO8601Utils.format(date, true));

  java.util.TimeZone offset = new java.util.SimpleTimeZone(90 * 60 * 1000, "plusNinety");
  assertEquals("1970-01-01T01:30:00.123+01:30",
      com.google.gson.internal.bind.util.ISO8601Utils.format(date, true, offset));
}