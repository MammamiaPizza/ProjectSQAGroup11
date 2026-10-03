package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TokeniserStateScriptCommentTest {

    private Element script(String html) {
        Document document = Jsoup.parse(html);
        Element script = document.select("script").first();
        assertNotNull(script);
        return script;
    }

    @Test
    public void preservesSingleQuotedSplitEndTagInsideEscapedScriptComment() {
        String source = "<script><!--\n"
                + "document.write('</scr' + 'ipt>');\n"
                + "//--></script>";

        assertEquals("<!--\n"
                + "document.write('</scr' + 'ipt>');\n"
                + "//-->", script(source).html());
    }

    @Test
    public void preservesDoubleQuotedSplitEndTagInsideEscapedScriptComment() {
        String source = "<script><!--\n"
                + "document.write(\"</scr\" + \"ipt>\");\n"
                + "//--></script>";

        assertEquals("<!--\n"
                + "document.write(\"</scr\" + \"ipt>\");\n"
                + "//-->", script(source).html());
    }

    @Test
    public void preservesMixedQuotedFragmentsThatDoNotFormAnEndTag() {
        String source = "<script><!--\n"
                + "var end = '</scr' + \"ipt>\";\n"
                + "//--></script>";

        assertEquals("<!--\n"
                + "var end = '</scr' + \"ipt>\";\n"
                + "//-->", script(source).html());
    }

    @Test
    public void preservesNonScriptEndTagLikeTextInsideEscapedScriptComment() {
        String source = "<script><!--\n"
                + "document.write('</style>');\n"
                + "//--></script>";

        assertEquals("<!--\n"
                + "document.write('</style>');\n"
                + "//-->", script(source).html());
    }

    @Test
    public void preservesOrdinaryScriptTextWithoutCommentEscaping() {
        String source = "<script>var value = '</scr' + 'ipt>';</script>";

        assertEquals("var value = '</scr' + 'ipt>';", script(source).html());
    }
}