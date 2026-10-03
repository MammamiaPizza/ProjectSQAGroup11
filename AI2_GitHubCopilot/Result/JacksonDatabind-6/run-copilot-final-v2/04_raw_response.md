@Test
public void testFormatISO8601WithNewInstance() {
    StdDateFormat df = new StdDateFormat();
    String result = df.format(new java.util.Date(0L));
    assertNotNull(result);
    assertTrue(result.length() > 0);
}
@Test
public void testParseRFC1123WithNewInstance() throws java.text.ParseException {
    StdDateFormat df = new StdDateFormat();
    java.util.Date d = df.parse("Tue, 15 Nov 1994 08:12:31 GMT");
    assertNotNull(d);
}
@Test
public void testParseTimestampPositive() throws java.text.ParseException {
    StdDateFormat df = new StdDateFormat();
    java.util.Date d = df.parse("1400000000000");
    assertEquals(1400000000000L, d.getTime());
}
@Test
public void testParseTimestampNegative() throws java.text.ParseException {
    StdDateFormat df = new StdDateFormat();
    java.util.Date d = df.parse("-500000000000");
    assertEquals(-500000000000L, d.getTime());
}