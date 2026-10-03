package org.jsoup.nodes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DocumentTypeRegressionTest {

    @Test
    public void rendersPublicAndSystemIdentifiersWithSeparateQuotes() {
        DocumentType type = new DocumentType(
                "html",
                "-//IETF//DTD HTML 2.0//",
                "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd",
                "");

        assertEquals(
                "<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML 2.0//\" \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">",
                type.outerHtml());
    }

    @Test
    public void rendersTheProvidedDoctypeName() {
        DocumentType type = new DocumentType("svg", "", "", "");

        assertEquals("<!DOCTYPE svg>", type.outerHtml());
    }

    @Test
    public void hasDoctypeNodeName() {
        DocumentType type = new DocumentType("html", "", "", "");

        assertEquals("#doctype", type.nodeName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsEmptyName() {
        new DocumentType("", "", "", "");
    }

@Test
public void rendersSystemIdentifierWhenPublicIdentifierIsEmpty() {
    DocumentType type = new DocumentType("html", "", "http://example.com/html.dtd", "");
    assertEquals("<!DOCTYPE html \"http://example.com/html.dtd\">", type.outerHtml());
}
}
