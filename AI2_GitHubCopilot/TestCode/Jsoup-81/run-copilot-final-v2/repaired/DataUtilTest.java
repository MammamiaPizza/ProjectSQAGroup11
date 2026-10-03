package org.jsoup.helper;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

public class DataUtilTest {

 private static final String BASE_URI = "]8;id=md-v5bp81;http://example.com/http://example.com/]8;;]8;;";]8;;

 @Test
 public void supportsXmlCharsetDeclaration() throws Exception {
     String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><html><body><p>Hell\u00F6
W\u00F6rld!</p></body></html>";
     byte[] bytes = xml.getBytes(Charset.forName("ISO-8859-1"));
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("Hell\u00F6 W\u00F6rld!", doc.body().text());
     assertNotNull(doc.charset());
     assertTrue(doc.charset().name().toUpperCase().contains("8859"));
 }

 @Test
 public void testXmlCharsetUtf8() throws Exception {
     String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html><body><p>Hell\u00F6
W\u00F6rld!</p></body></html>";
     byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("Hell\u00F6 W\u00F6rld!", doc.body().text());
     assertEquals(StandardCharsets.UTF_8, doc.charset());
 }

 @Test
 public void testXmlCharsetWindows1252() throws Exception {
     String xml = "<?xml version=\"1.0\" encoding=\"windows-1252\"?><html><body><p>\u20ACuro
Sign</p></body></html>";
     byte[] bytes = xml.getBytes(Charset.forName("windows-1252"));
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("\u20ACuro Sign", doc.body().text());
     assertNotNull(doc.charset());
 }

 @Test
 public void testXmlCharsetSingleQuotedEncoding() throws Exception {
     String xml = "<?xml version='1.0' encoding='ISO-8859-1'?><html><body><p>Hell\u00F6
W\u00F6rld!</p></body></html>";
     byte[] bytes = xml.getBytes(Charset.forName("ISO-8859-1"));
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("Hell\u00F6 W\u00F6rld!", doc.body().text());
     assertTrue(doc.charset().name().toUpperCase().contains("8859"));
 }

 @Test
 public void testMissingEncodingDeclaration() throws Exception {
     String xml = "<!DOCTYPE html><html><body><p>Hello World!</p></body></html>";
     byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("Hello World!", doc.body().text());
     assertNotNull(doc.charset());
 }

 @Test
 public void testInvalidEncodingNameFallsBackToUtf8() throws Exception {
     String xml = "<?xml version=\"1.0\" encoding=\"no-such-charset-xyz\"?><html><body><p>Hello
World!</p></body></html>";
     byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("Hello World!", doc.body().text());
     assertNotNull(doc.charset());
 }

 @Test
 public void testBomOverridesXmlDeclaration() throws Exception {
     String xml = "<?xml version=\"1.0\"
encoding=\"ISO-8859-1\"?><html><body><p>test</p></body></html>";
     byte[] xmlBytes = xml.getBytes(Charset.forName("ISO-8859-1"));
     byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
     byte[] combined = new byte[bom.length + xmlBytes.length];
     System.arraycopy(bom, 0, combined, 0, bom.length);
     System.arraycopy(xmlBytes, 0, combined, bom.length, xmlBytes.length);
     InputStream in = new ByteArrayInputStream(combined);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals("test", doc.body().text());
     assertEquals(StandardCharsets.UTF_8, doc.charset());
 }

 @Test
 public void testNullInputStreamReturnsEmptyDocument() throws Exception {
     Document doc = DataUtil.load((InputStream) null, null, BASE_URI);
     assertNotNull(doc);
     assertEquals(BASE_URI, doc.baseUri());
     assertEquals("", doc.text());
 }

 @Test
 public void testEmptyByteArrayStream() throws Exception {
     InputStream in = new ByteArrayInputStream(new byte[0]);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertNotNull(doc);
     assertEquals("", doc.text());
 }

 @Test
 public void testCharsetPersistedInOutputSettings() throws Exception {
     String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><html><body><p>Hello
World!</p></body></html>";
     byte[] bytes = xml.getBytes(Charset.forName("ISO-8859-1"));
     InputStream in = new ByteArrayInputStream(bytes);
     Document doc = DataUtil.load(in, null, BASE_URI, Parser.xmlParser());
     assertEquals(Charset.forName("ISO-8859-1"), doc.outputSettings().charset());
     assertEquals(Charset.forName("ISO-8859-1"), doc.charset());
 }

}
