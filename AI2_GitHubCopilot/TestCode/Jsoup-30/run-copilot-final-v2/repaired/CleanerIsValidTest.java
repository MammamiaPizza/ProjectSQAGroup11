package org.jsoup.safety;

import org.junit.Test;
import static org.junit.Assert.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CleanerIsValidTest {

 private final Cleaner cleaner = new Cleaner(Whitelist.basic());

 @Test
 public void allElementsAllowedReturnsTrue() {
     Document doc = Jsoup.parse("<html><body><p>text</p><b>bold</b><i>italic</i></body></html>");
     assertTrue(cleaner.isValid(doc));
 }

 @Test
 public void disallowedScriptTagReturnsFalse() {
     Document doc = Jsoup.parse("<html><body><p>ok</p><script>alert(1)</script></body></html>");
     assertFalse(cleaner.isValid(doc));
 }

 @Test
 public void emptyBodyReturnsTrue() {
     Document doc = Jsoup.parse("<html><body></body></html>");
     assertTrue(cleaner.isValid(doc));
 }

 @Test
 public void disallowedStyleAttributeReturnsFalse() {
     Document doc = Jsoup.parse("<html><body><p style=\"color:red\">text</p></body></html>");
     assertFalse(cleaner.isValid(doc));
 }

 @Test
 public void divWrappingAllowedElementReturnsFalse() {
     Document doc = Jsoup.parse("<html><body><div><p>text</p></div></body></html>");
     assertFalse(cleaner.isValid(doc));
 }

 @Test(expected = IllegalArgumentException.class)
 public void nullDocumentThrowsIllegalArgument() {
     cleaner.isValid(null);
 }

 @Test
 public void allowedTagWithDisallowedAttrReturnsFalse() {
     Document doc = Jsoup.parse("<html><body><p onclick=\"bad()\">text</p></body></html>");
     assertFalse(cleaner.isValid(doc));
 }

 @Test
 public void onlyTextNodesReturnsTrue() {
     Document doc = Jsoup.parse("<html><body>plain text only</body></html>");
     assertTrue(cleaner.isValid(doc));
 }

 @Test
 public void multipleDisallowedTagsReturnFalse() {
     Document doc = Jsoup.parse("<html><body><img src=\"x\"><embed y=\"z\"></body></html>");
     assertFalse(cleaner.isValid(doc));
 }

 @Test
 public void allowedAttributesWithCorrectTagsReturnsTrue() {
     Document doc = Jsoup.parse("<html><body><a
href=\"]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;\">link</a></body></html>");]8;;
     assertTrue(cleaner.isValid(doc));
 }

 @Test
 public void framesetDocumentNullBodyHandledGracefully() {
     Document doc = Jsoup.parse("<html><frameset><frame src=\"x\"></frameset></html>");
     assertTrue(cleaner.isValid(doc));
 }

}
EOF
</bash_script>
