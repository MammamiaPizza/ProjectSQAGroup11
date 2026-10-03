@Test
public void rendersSystemIdentifierWhenPublicIdentifierIsEmpty() {
    DocumentType type = new DocumentType("html", "", "http://example.com/html.dtd", "");
    assertEquals("<!DOCTYPE html \"http://example.com/html.dtd\">", type.outerHtml());
}