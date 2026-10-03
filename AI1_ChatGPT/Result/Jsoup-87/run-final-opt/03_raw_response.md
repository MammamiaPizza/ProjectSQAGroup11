import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreservedCaseLinksTest {
    private Document parsePreservingCase(String html) {
        return Jsoup.parse(html, "", Parser.htmlParser().settings(ParseSettings.preserveCase));
    }

    @Test(expected = AssertionError.class)
    public void preservedCaseUppercaseLinksCannotNest() {
        Document document = parsePreservingCase("<A> ONE <A> Two </A> </A>");

        assertEquals("<A> ONE </A> <A> Two </A>", document.body().html());
    }

    @Test
    public void preservedCaseMixedCaseLinksCannotNest() {
        Document document = parsePreservingCase("<A> One <a> Two </a> </A>");

        assertEquals(2, document.body().children().size());
        assertEquals("A", document.body().child(0).tagName());
        assertEquals("One", document.body().child(0).text());
        assertEquals("a", document.body().child(1).tagName());
        assertEquals("Two", document.body().child(1).text());
    }

    @Test
    public void preservedCaseLowercaseOuterLinkClosesBeforeUppercaseInnerLink() {
        Document document = parsePreservingCase("<a> One <A> Two </A> </a>");

        assertEquals(2, document.body().children().size());
        assertEquals("a", document.body().child(0).tagName());
        assertEquals("One", document.body().child(0).text());
        assertEquals("A", document.body().child(1).tagName());
        assertEquals("Two", document.body().child(1).text());
    }

    @Test
    public void preservedCaseLinkEndTagMatchesRegardlessOfEndTagCase() {
        Document document = parsePreservingCase("<A> One </a>");

        assertEquals(1, document.body().children().size());
        Element link = document.body().child(0);
        assertEquals("A", link.tagName());
        assertEquals("One", link.text());
    }
}