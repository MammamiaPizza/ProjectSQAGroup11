package org.apache.commons.lang;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class StringEscapeUtilsLang52Test {

    @Test
    public void escapeJavaScriptEscapesSlashInScriptClosingSequence() {
        assertEquals("alert(\\'aaa\\');<\\/script>\\'",
                StringEscapeUtils.escapeJavaScript("alert('aaa');</script>'"));
    }

    @Test
    public void escapeJavaScriptEscapesStandaloneAndAdjacentSlashes() {
        assertEquals("\\/", StringEscapeUtils.escapeJavaScript("/"));
        assertEquals("<\\/", StringEscapeUtils.escapeJavaScript("</"));
        assertEquals("a\\/b\\/\\/c", StringEscapeUtils.escapeJavaScript("a/b//c"));
    }

    @Test
    public void escapeJavaScriptWriterEscapesSlashAndMatchesStringOverload() throws Exception {
        String input = "var value = '</script>';";
        String expected = "var value = \\'<\\/script>\\';";
        StringWriter writer = new StringWriter();

        StringEscapeUtils.escapeJavaScript(writer, input);

        assertEquals(expected, writer.toString());
        assertEquals(StringEscapeUtils.escapeJavaScript(input), writer.toString());
    }

    @Test
    public void escapeJavaScriptStillEscapesJavaScriptQuotesAndBackslashes() {
        assertEquals("\\'\\\"\\\\",
                StringEscapeUtils.escapeJavaScript("'\"\\"));
    }

@Test
public void escapeJavaEscapesCharactersAcrossUnicodeRanges() {
    assertEquals("\\u1000\\u0100\\u0080",
            StringEscapeUtils.escapeJava("\u1000\u0100\u0080"));
}

@Test
public void escapeJavaWriterRejectsNullWriterAndIgnoresNullInput() throws java.io.IOException {
    try {
        StringEscapeUtils.escapeJava((java.io.Writer) null, "value");
        assertEquals("exception expected", "no exception");
    } catch (IllegalArgumentException expected) {
        assertEquals("The Writer must not be null", expected.getMessage());
    }

    java.io.StringWriter writer = new java.io.StringWriter();
    StringEscapeUtils.escapeJava(writer, null);
    assertEquals("", writer.toString());
}

@Test
public void escapeAndUnescapeHtmlStringAndWriterOverloadsHandleEntities() throws java.io.IOException {
    String input = "<>&\"";
    String escaped = "&lt;&gt;&amp;&quot;";

    assertEquals(escaped, StringEscapeUtils.escapeHtml(input));
    assertEquals(input, StringEscapeUtils.unescapeHtml(escaped));

    java.io.StringWriter escapedWriter = new java.io.StringWriter();
    StringEscapeUtils.escapeHtml(escapedWriter, input);
    StringEscapeUtils.escapeHtml(escapedWriter, null);
    assertEquals(escaped, escapedWriter.toString());

    java.io.StringWriter unescapedWriter = new java.io.StringWriter();
    StringEscapeUtils.unescapeHtml(unescapedWriter, escaped);
    assertEquals(input, unescapedWriter.toString());
}

@Test
public void escapeAndUnescapeXmlStringAndWriterOverloadsHandlePredefinedEntities() throws java.io.IOException {
    String input = "<>&\"'";
    String escaped = "&lt;&gt;&amp;&quot;&apos;";

    assertEquals(escaped, StringEscapeUtils.escapeXml(input));
    assertEquals(input, StringEscapeUtils.unescapeXml(escaped));

    java.io.StringWriter escapedWriter = new java.io.StringWriter();
    StringEscapeUtils.escapeXml(escapedWriter, input);
    assertEquals(escaped, escapedWriter.toString());

    java.io.StringWriter unescapedWriter = new java.io.StringWriter();
    StringEscapeUtils.unescapeXml(unescapedWriter, escaped);
    assertEquals(input, unescapedWriter.toString());
}
}
