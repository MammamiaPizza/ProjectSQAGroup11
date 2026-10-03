[

     @Test
     public void asStringReturnsValidXml() {
         W3CDom w3c = new W3CDom();
         org.jsoup.nodes.Document jsoupDoc = org.jsoup.Jsoup.parse("<div>Hello</div>");
         Document w3cDoc = w3c.fromJsoup(jsoupDoc);
         String xml = w3c.asString(w3cDoc);
         assertNotNull(xml);
         assertTrue(xml.contains("<div>Hello</div>"));
     }

     @Test
     public void convertSetsDocumentUriWhenLocationIsNotEmpty() {
         W3CDom w3c = new W3CDom();
         org.jsoup.nodes.Document jsoupDoc = org.jsoup.Jsoup.parse("<div>test</div>");
         jsoupDoc.setBaseUri("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Document w3cDoc = w3c.fromJsoup(jsoupDoc);
         assertEquals("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;", w3cDoc.getDocumentURI());]8;;
     }

     @Test(expected = IllegalStateException.class)
     public void fromJsoupWrapsParserConfigurationException() {
         W3CDom w3c = new W3CDom();
         w3c.factory = new javax.xml.parsers.DocumentBuilderFactory() {
             @Override
             public javax.xml.parsers.DocumentBuilder newDocumentBuilder() throws
javax.xml.parsers.ParserConfigurationException {
                 throw new javax.xml.parsers.ParserConfigurationException("test");
             }
             @Override
             public void setAttribute(String name, Object value) {
             }
             @Override
             public Object getAttribute(String name) {
                 return null;
             }
             @Override
             public void setFeature(String name, boolean value) throws
javax.xml.parsers.ParserConfigurationException {
             }
             @Override
             public boolean getFeature(String name) throws
javax.xml.parsers.ParserConfigurationException {
                 return false;
             }
         };
         org.jsoup.nodes.Document jsoupDoc = org.jsoup.Jsoup.parse("<div>test</div>");
         w3c.fromJsoup(jsoupDoc);
     }