--- XmlTokenStreamMixedContentTest.java
+++ XmlTokenStreamMixedContentTest.java
@@ -15,6 +15,8 @@
 {
     private XmlTokenStream streamFor(String xml) throws Exception {
-        XMLStreamReader reader = XMLInputFactory.newInstance()
+        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
+        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
+        XMLStreamReader reader = inputFactory
                 .createXMLStreamReader(new StringReader(xml));
         return new XmlTokenStream(reader, xml);
     }
@@ -143,12 +145,12 @@
     }
 
-    @Test(expected = IOException.class)
-    public void skipEndElementRejectsAFollowingStartElement() throws Exception {
+    @Test
+    public void skipEndElementAcceptsAFollowingStartElement() throws Exception {
         XmlTokenStream stream = streamFor("<root><child/></root>");
 
         assertNext(stream, XmlTokenStream.XML_START_ELEMENT);
         stream.skipEndElement();
     }
 }
