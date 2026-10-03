package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XmlDeclarationAndCharsetRegressionTest {

    @Test
    public void parsesXmlDeclarationAsDeclarationWithAttributes() {
        Document document = Parser.xmlParser().parseInput(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>", "");

        assertTrue(document.childNode(0) instanceof XmlDeclaration);
        XmlDeclaration declaration = (XmlDeclaration) document.childNode(0);
        assertEquals("xml", declaration.name());
        assertEquals("1.0", declaration.attr("version"));
        assertEquals("UTF-8", declaration.attr("encoding"));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", declaration.outerHtml());
    }

    @Test
    public void normalizesSingleQuotedXmlDeclarationEncodingOnOutput() {
        Document document = Parser.xmlParser().parseInput(
                "<?xml encoding='UTF-8'?><body>One</body>", "");

        XmlDeclaration declaration = (XmlDeclaration) document.childNode(0);
        assertEquals("UTF-8", declaration.attr("encoding"));
        assertEquals("<?xml encoding=\"UTF-8\"?>", declaration.outerHtml());
    }

    @Test
    public void serializesProgrammaticXmlDeclarationAttributes() {
        XmlDeclaration declaration = new XmlDeclaration("xml", "", false);
        declaration.attr("version", "1.0");
        declaration.attr("encoding", "ISO-8859-1");

        assertEquals("xml version=\"1.0\" encoding=\"ISO-8859-1\"",
                declaration.getWholeDeclaration());
        assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>",
                declaration.outerHtml());
    }

    @Test
    public void detectsCharsetFromXmlEncodingDeclaration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>café</root>";
        Document document = DataUtil.load(
                new ByteArrayInputStream(xml.getBytes("ISO-8859-1")),
                null, "", Parser.xmlParser());

        assertEquals("ISO-8859-1", document.charset().name());
        assertEquals("café", document.select("root").text());
    }

    @Test
    public void defaultsXmlInputWithoutEncodingDeclarationToUtf8() throws Exception {
        String xml = "<?xml version=\"1.0\"?><root>café</root>";
        Document document = DataUtil.load(
                new ByteArrayInputStream(xml.getBytes("UTF-8")),
                null, "", Parser.xmlParser());

        assertEquals("UTF-8", document.charset().name());
        assertEquals("café", document.select("root").text());
    }

    @Test
    public void updatesExistingXmlDeclarationEncodingWhenDocumentCharsetChanges() {
        Document document = Parser.xmlParser().parseInput(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>", "");

        document.updateMetaCharsetElement(true);
        document.charset(Charset.forName("ISO-8859-1"));

        XmlDeclaration declaration = (XmlDeclaration) document.childNode(0);
        assertEquals("ISO-8859-1", declaration.attr("encoding"));
        assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>",
                declaration.outerHtml());
    }

    @Test
    public void addsEncodingToXmlDeclarationThatHasNoneWhenCharsetUpdatesEnabled() {
        Document document = Parser.xmlParser().parseInput(
                "<?xml version=\"1.0\"?><root/>", "");

        document.updateMetaCharsetElement(true);
        document.charset(Charset.forName("UTF-8"));

        XmlDeclaration declaration = (XmlDeclaration) document.childNode(0);
        assertEquals("1.0", declaration.attr("version"));
        assertEquals("UTF-8", declaration.attr("encoding"));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                declaration.outerHtml());
    }

    @Test
    public void leavesXmlDeclarationUntouchedWhenCharsetUpdatingIsDisabled() {
        Document document = Parser.xmlParser().parseInput(
                "<?xml version=\"1.0\" encoding=\"dontTouch\"?><root/>", "");

        document.updateMetaCharsetElement(false);
        document.outputSettings().charset("UTF-8");

        XmlDeclaration declaration = (XmlDeclaration) document.childNode(0);
        assertEquals("dontTouch", declaration.attr("encoding"));
        assertEquals("<?xml version=\"1.0\" encoding=\"dontTouch\"?>",
                declaration.outerHtml());
    }
}