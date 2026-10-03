package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HtmlTreeBuilderStatePreNewlineTest {

    @Test
    public void skipsFirstNewlineImmediatelyInsidePre() {
        assertEquals("first line", preText("<pre>\nfirst line</pre>"));
    }

    @Test
    public void preservesContentWhenPreDoesNotStartWithNewline() {
        assertEquals("first line", preText("<pre>first line</pre>"));
    }

    @Test
    public void skipsOnlyOneOfMultipleLeadingNewlinesInsidePre() {
        assertEquals("\nfirst line", preText("<pre>\n\nfirst line</pre>"));
    }

    @Test
    public void preservesNewlinesThatOccurAfterInitialPreContent() {
        assertEquals("first\nsecond", preText("<pre>first\nsecond</pre>"));
    }

    @Test
    public void skipsInitialNewlineForUnclosedPreElement() {
        assertEquals("content", preText("<pre>\ncontent"));
    }

    @Test
    public void appliesInitialNewlineRuleToCaseInsensitivePreTag() {
        assertEquals("content", preText("<PRE>\ncontent</PRE>"));
    }

    private static String preText(String html) {
        Document document = Jsoup.parse(html);
        Element pre = document.select("pre").first();
        assertNotNull(pre);

        StringBuilder text = new StringBuilder();
        for (Node child : pre.childNodes()) {
            assertTrue(child instanceof TextNode);
            text.append(((TextNode) child).getWholeText());
        }
        return text.toString();
    }

@org.junit.Test
public void parsesTitleContentsAsRcData() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<title>One <b>Two</b> &amp; Three</title>");

    org.junit.Assert.assertEquals("One <b>Two</b> & Three", document.title());
}

@org.junit.Test
public void preservesStyleContentsAsRawtext() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<style>one <em>two</em> &amp; three</style>");

    org.junit.Assert.assertEquals("one <em>two</em> &amp; three", document.select("style").first().data());
}

@org.junit.Test
public void ignoresWhitespaceBeforeHtmlElement() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("\t\n<html><body>body</body></html>");

    org.junit.Assert.assertEquals(1, document.childNodeSize());
    org.junit.Assert.assertEquals("body", document.body().text());
}

@org.junit.Test
public void placesNonWhitespaceBeforeHtmlElementInBody() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("prefix<html><body>body</body></html>");

    org.junit.Assert.assertEquals("prefixbody", document.body().text());
}
}
