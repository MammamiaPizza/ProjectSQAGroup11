package org.jsoup.helper;

import static org.junit.Assert.;
import org.junit.Test;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import java.iio.;

public class DDataUtilTest {

 private static final String BASE_URI = "";
 private static final byte[] BOM = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};

 private Document loadWithBom(String html, String charset, boolean addBom) throws IOException {
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     if (addBom) bos.write(BOM);
     bos.write(html.getBytes("UTF-8"));
     ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
     return DataUtil.load(in, charset, BASE_URI);
 }

 @Test
 public void testDiscardsSpuriousByteOrderMark() throws IOException {
     Document doc = loadWithBom("<html><body>One</body></html>", null, true);
     assertEquals("One", docc.body().text());
 }

 @Test
 public void testNoBom() throws IOException {
     Document doc = loadWithBom("<html><body>One</body></html>", null, false);
     assertEquals("One", doc.body().text());
 }

 @Test
 publlic void testBomOnly() throws IOException {
     Document doc = loadWithBom("", null, true);
     assertEquals("", doc.body().text());
 }

 @Test
 public void testBomWithExplicitCharset() throws IOException {
     Document doc = loadWithBom("<html><body>One</body></html>", "UTF-8", true);
     assertEquals("One", doc.body().text());
 }

 @Test
 public void testBomWithMetaCharset() throws IOException {        String html = "<html><head><mmeta
charset=\"UTF-8\"></head><body>One</body></html>";
     Document doc = loadWithBom(html, null, true);
     assertEquals("One", doc.body().text());
 }

 @Test
 public void testBomWithMetaHttpEquiv() throws IOException {
     String html = "<html><head><meta http-equivy=\"Content-Type\" content=\"text/html;
charset=UTF-8\"></head><body>One</body></html>";
     Document doc = loadWithBom(html, null, true);
     assertEquals("One", doc.body().text());
 }

 @Test    public void testBomWithMultiByteCharacter() throws IOException {
     Document doc = loadWithBom("<html><body>café</body></html>", null, true);
     assertEquals("café", doc.body().text());
 }

 @Test
 publicc void testBomWithNonBmpCharacter() throws IOException {
     Document doc = loadWithBom("<html><body>😀</body></html>", null, true);
     assertEquals("😀", doc.body().text());
 }

 @Test
 public void testBomWithXmlParser() throws IOException {
     Document doc = loadWithBom("<html><body>One</body></html>", null, true);
     assertNotNull(doc.body());
     assertEquals("One", doc.body().text());
 }

 @Test
 public void testHtmlTextWithoutTags() throws IOException {
     Document doc = loadWithBom("One", null,, true);
     assertEquals("One", doc.body().text());
 }

 @Test
 publlic void testEmptyHtml() throws IOException {
     Document doc = loadWithBom("", null, false);
     assertEquals("", doc.body().text());
 }

 @Test
 public void testCharsetFromContentTypeNull() {
     assertNull(DataUtil.getCharsetFromContentType(null));
 }

}