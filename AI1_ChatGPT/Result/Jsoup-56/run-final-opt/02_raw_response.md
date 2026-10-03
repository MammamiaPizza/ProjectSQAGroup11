import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DocumentTypeSystemIdentifierTest {

    @Test
    public void serializesSystemIdentifierWithSystemKeyword() {
        DocumentType doctype = new DocumentType("html", "", "exampledtdfile.dtd", "");

        assertEquals("<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">", doctype.outerHtml());
    }

    @Test
    public void serializesBlankPublicIdentifierAsSystemDoctype() {
        DocumentType doctype = new DocumentType("html", "   ", "legacy.dtd", "");

        assertEquals("<!DOCTYPE html SYSTEM \"legacy.dtd\">", doctype.outerHtml());
    }

    @Test
    public void serializesPublicIdentifierWithoutSystemIdentifier() {
        DocumentType doctype = new DocumentType("html", "-//Example//DTD Test//EN", "", "");

        assertEquals("<!DOCTYPE html PUBLIC \"-//Example//DTD Test//EN\">", doctype.outerHtml());
    }

    @Test
    public void serializesPublicAndSystemIdentifiersInTheirCorrectPositions() {
        DocumentType doctype = new DocumentType(
                "html",
                "-//Example//DTD Test//EN",
                "example.dtd",
                "");

        assertEquals(
                "<!DOCTYPE html PUBLIC \"-//Example//DTD Test//EN\" \"example.dtd\">",
                doctype.outerHtml());
    }

    @Test
    public void serializesHtml5DoctypeInLowerCaseWhenItHasNoIdentifiers() {
        DocumentType doctype = new DocumentType("html", "", "", "");

        assertEquals("<!doctype html>", doctype.outerHtml());
    }

    @Test
    public void htmlParserPreservesSystemIdentifierAndItsKeyword() {
        Document document = Jsoup.parse(
                "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\"><html><head></head><body></body></html>");
        DocumentType doctype = firstDoctype(document);

        assertEquals("html", doctype.attr("name"));
        assertEquals("", doctype.attr("publicId"));
        assertEquals("exampledtdfile.dtd", doctype.attr("systemId"));
        assertEquals("<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">", doctype.outerHtml());
    }

    @Test
    public void htmlParserPreservesPublicAndSystemIdentifiers() {
        Document document = Jsoup.parse(
                "<!DOCTYPE html PUBLIC \"-//Example//DTD Test//EN\" \"example.dtd\"><html></html>");
        DocumentType doctype = firstDoctype(document);

        assertEquals("-//Example//DTD Test//EN", doctype.attr("publicId"));
        assertEquals("example.dtd", doctype.attr("systemId"));
        assertEquals(
                "<!DOCTYPE html PUBLIC \"-//Example//DTD Test//EN\" \"example.dtd\">",
                doctype.outerHtml());
    }

    @Test
    public void xmlParserPreservesSystemDoctypeInformation() {
        Document document = Jsoup.parse(
                "<!DOCTYPE note SYSTEM \"note.dtd\"><note/>",
                "",
                Parser.xmlParser());
        DocumentType doctype = firstDoctype(document);

        assertEquals("note", doctype.attr("name"));
        assertEquals("", doctype.attr("publicId"));
        assertEquals("note.dtd", doctype.attr("systemId"));
        assertEquals("<!DOCTYPE note SYSTEM \"note.dtd\">", doctype.outerHtml());
    }

    private DocumentType firstDoctype(Document document) {
        for (Node node : document.childNodes()) {
            if (node instanceof DocumentType) {
                return (DocumentType) node;
            }
        }
        fail("Expected document to contain a DocumentType node");
        return null;
    }
}