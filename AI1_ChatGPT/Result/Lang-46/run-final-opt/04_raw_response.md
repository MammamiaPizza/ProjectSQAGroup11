@Test
public void escapeAndUnescapeCsvHandlesQuotedSpecialCharacters() throws java.io.IOException {
    String input = "a,\"b\"\n";
    String expected = "\"a,\"\"b\"\"\n\"";

    assertEquals(expected, StringEscapeUtils.escapeCsv(input));

    java.io.StringWriter writer = new java.io.StringWriter();
    StringEscapeUtils.escapeCsv(writer, input);
    assertEquals(expected, writer.toString());

    assertEquals(input, StringEscapeUtils.unescapeCsv(expected));
}

@Test
public void escapeCsvLeavesPlainAndNullValuesUnchanged() throws java.io.IOException {
    assertEquals("plain text", StringEscapeUtils.escapeCsv("plain text"));
    assertEquals(null, StringEscapeUtils.escapeCsv(null));

    java.io.StringWriter writer = new java.io.StringWriter();
    StringEscapeUtils.escapeCsv(writer, "plain text");
    StringEscapeUtils.escapeCsv(writer, null);
    assertEquals("plain text", writer.toString());
}