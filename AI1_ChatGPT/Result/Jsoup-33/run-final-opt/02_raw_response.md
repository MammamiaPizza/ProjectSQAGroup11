package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlTreeBuilderRegressionTest {
    @Test
    public void handlesKnownEmptyBlocksAfterSelfClosingScript() {
        Document document = Jsoup.parse(
            "<script src=/foo /><div id=2><img/><img></div><a id=3 /><i />"
                + "<foo /><foo>One</foo> <hr /> hr text <hr> hr text two");

        assertEquals(
            "<script src=\"/foo\"></script><div id=\"2\"><img /><img /></div>"
                + "<a id=\"3\"></a><i></i><foo /><foo>One</foo> <hr /> hr text "
                + "<hr /> hr text two",
            document.body().html());
    }

    @Test
    public void selfClosingScriptDoesNotCauseFollowingMarkupToBecomeScriptText() {
        Document document = Jsoup.parse("<script src=/foo /><div id=after><img /></div>");

        assertEquals(
            "<script src=\"/foo\"></script><div id=\"after\"><img /></div>",
            document.body().html());
    }

    @Test
    public void explicitlyClosedScriptStillAllowsFollowingElements() {
        Document document = Jsoup.parse("<script src=/foo></script><div id=after>text</div>");

        assertEquals(
            "<script src=\"/foo\"></script><div id=\"after\">text</div>",
            document.body().html());
    }
}