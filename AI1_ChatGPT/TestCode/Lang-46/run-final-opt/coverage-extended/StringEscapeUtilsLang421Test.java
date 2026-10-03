package org.apache.commons.lang;

import java.io.StringWriter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringEscapeUtilsLang421Test {

    @Test
    public void escapeJavaPreservesSlashInPlainText() {
        String input = "String with a slash (/) in it";

        assertEquals("String with a slash (/) in it", StringEscapeUtils.escapeJava(input));
    }

    @Test
    public void escapeJavaWriterPreservesSlashInPlainText() throws Exception {
        StringWriter writer = new StringWriter();

        StringEscapeUtils.escapeJava(writer, "String with a slash (/) in it");

        assertEquals("String with a slash (/) in it", writer.toString());
    }

    @Test
    public void escapeJavaPreservesLeadingTrailingAndRepeatedSlashes() {
        String input = "/path//to/resource/";

        assertEquals("/path//to/resource/", StringEscapeUtils.escapeJava(input));
    }

    @Test
    public void escapeJavaDoesNotAddSlashEscapesWhenOtherJavaEscapesAreNeeded() {
        String input = "a/b\\c\"d\n/e";

        assertEquals("a/b\\\\c\\\"d\\n/e", StringEscapeUtils.escapeJava(input));
    }

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
}
