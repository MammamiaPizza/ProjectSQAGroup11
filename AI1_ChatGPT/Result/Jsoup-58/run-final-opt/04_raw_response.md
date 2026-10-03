@org.junit.Test
public void documentWithUnsafeBodyAttributeIsInvalid() {
    org.jsoup.safety.Cleaner cleaner = new org.jsoup.safety.Cleaner(org.jsoup.safety.Whitelist.basic());
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<body onclick='alert(1)'><p>Safe</p></body>");

    org.junit.Assert.assertFalse(cleaner.isValid(document));
}

@org.junit.Test
public void documentWithCommentThatCleanerDiscardsIsInvalid() {
    org.jsoup.safety.Cleaner cleaner = new org.jsoup.safety.Cleaner(org.jsoup.safety.Whitelist.basic());
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse("<p>Safe</p><!-- discard me -->");

    org.junit.Assert.assertFalse(cleaner.isValid(document));
}

@org.junit.Test
public void cleanWithOutputSettingsUsesProvidedPrettyPrintSetting() {
    org.jsoup.nodes.Document.OutputSettings outputSettings =
        new org.jsoup.nodes.Document.OutputSettings().prettyPrint(false);

    String clean = org.jsoup.Jsoup.clean(
        "<p>One</p><p>Two</p>", "", org.jsoup.safety.Whitelist.basic(), outputSettings);

    org.junit.Assert.assertEquals("<p>One</p><p>Two</p>", clean);
}

@org.junit.Test
public void parseWithXmlParserPreservesRootElementCase() {
    org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(
        "<Root><Item /></Root>", "", org.jsoup.parser.Parser.xmlParser());

    org.junit.Assert.assertEquals("Root", document.child(0).tagName());
}