@Test
public void serializesWhitespaceOnlyPublicIdentifierAsSystemDoctype() {
    org.jsoup.nodes.DocumentType doctype = new org.jsoup.nodes.DocumentType(
        "html", " \t", "exampledtdfile.dtd", "");
    assertEquals("<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">", doctype.outerHtml());
}

@Test
public void treatsWhitespaceOnlyIdentifiersAsAbsent() {
    org.jsoup.nodes.DocumentType doctype = new org.jsoup.nodes.DocumentType(
        "html", " \t", " \t", "");
    assertEquals("<!doctype html>", doctype.outerHtml());
}