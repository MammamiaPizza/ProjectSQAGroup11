import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DocumentTitleRegressionTest {

    @Test
    public void titleSetterRoundTripsOrdinaryTitleAndCreatesTitleInHead() {
        Document document = Document.createShell("http://example.com/");

        document.title("Example page");

        assertEquals("Example page", document.title());
        Element title = document.head().getElementsByTag("title").first();
        assertNotNull(title);
        assertEquals("Example page", title.text());
    }

    @Test
    public void titleSetterPreservesSpacesBetweenWords() {
        Document document = Document.createShell("");

        document.title("Hello there now");

        assertEquals("Hello there now", document.title());
    }

    @Test
    public void titleGetterTrimsLeadingAndTrailingWhitespace() {
        Document document = Document.createShell("");

        document.title("  Hello there now  ");

        assertEquals("Hello there now", document.title());
    }

    @Test
    public void titleSetterUpdatesExistingTitleRatherThanAddingAnother() {
        Document document = Document.createShell("");
        document.title("First title");

        document.title("Second title");

        assertEquals("Second title", document.title());
        assertEquals(1, document.head().getElementsByTag("title").size());
    }

    @Test
    public void titleGetterReturnsEmptyStringWhenNoTitleExists() {
        Document document = Document.createShell("");

        assertEquals("", document.title());
    }

    @Test
    public void titleGetterIncludesTextFromMultipleTitleTextNodes() {
        Document document = Document.createShell("");
        Element title = document.head().appendElement("title");
        title.appendText("Hello ");
        title.appendText("there now");

        assertEquals("Hello there now", document.title());
    }
}