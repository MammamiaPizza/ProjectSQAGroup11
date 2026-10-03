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
}
