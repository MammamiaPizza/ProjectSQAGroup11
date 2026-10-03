@Test
public void serializesW3cDocument() throws Exception {
    org.w3c.dom.Document document = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().newDocument();
    org.w3c.dom.Element root = document.createElement("root");
    root.setAttribute("id", "one");
    root.appendChild(document.createTextNode("value"));
    document.appendChild(root);

    String serialized = new W3CDom().asString(document);

    org.junit.Assert.assertTrue(serialized.contains("<root id=\"one\">value</root>"));
}

@Test(expected = IllegalStateException.class)
public void wrapsParserConfigurationFailure() {
    new FailingDocumentBuilderW3CDom().fromJsoup(org.jsoup.Jsoup.parse("<p>text</p>"));
}

private static class FailingDocumentBuilderW3CDom extends W3CDom {
    FailingDocumentBuilderW3CDom() {
        factory = new javax.xml.parsers.DocumentBuilderFactory() {
            @Override
            public javax.xml.parsers.DocumentBuilder newDocumentBuilder()
                throws javax.xml.parsers.ParserConfigurationException {
                throw new javax.xml.parsers.ParserConfigurationException("failure");
            }

            @Override
            public void setAttribute(String name, Object value) {
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public void setFeature(String name, boolean value) {
            }

            @Override
            public boolean getFeature(String name) {
                return false;
            }
        };
    }
}