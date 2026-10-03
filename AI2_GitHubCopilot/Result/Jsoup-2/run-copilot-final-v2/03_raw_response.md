package org.jsoup.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

/**

 - Tests targeting the bug where text appearing after a data-tag end tag
 - (e.g. script, textarea) is incorrectly included inside the data element.
  */
 public class ParserTest {
  private static final String BASE_URI = "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;";]8;;
  // ---- Basic reproduction ----
  @Test
  public void handlesTextAfterData() {
  Document doc = Parser.parseBodyFragment("pre <script>inner</script> aft", BASE_URI);
  assertNotNull(doc);
  // "aft" must appear after the closing script tag
  assertEquals("<html><head></head><body>pre <script>inner</script> aft</body></html>",
          doc.html());
  }
  @Test
  public void scriptElementContainsOnlyItsOwnData() {
  Document doc = Parser.parseBodyFragment("<script>inner</script> aft", BASE_URI);
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("inner", script.text());
  }
  // ---- Other data tags ----
  @Test
  public void textAfterTextarea() {
  Document doc = Parser.parseBodyFragment("<textarea>inner</textarea> aft", BASE_URI);
  assertEquals("<html><head></head><body><textarea>inner</textarea> aft</body></html>",
          doc.html());
  }
  @Test
  public void textAfterTitle() {
  Document doc = Parser.parseBodyFragment("<title>inner</title> aft", BASE_URI);
  assertEquals("<html><head></head><body><title>inner</title> aft</body></html>",
          doc.html());
  }
  // ---- Variations with surrounding text ----
  @Test
  public void textBeforeAndAfterScript() {
  Document doc = Parser.parseBodyFragment("before<script>x</script>after", BASE_URI);
  assertEquals("<html><head></head><body>before<script>x</script>after</body></html>",
          doc.html());
  }
  @Test
  public void multipleScriptTagsWithTrailingText() {
  Document doc = Parser.parseBodyFragment("<script>a</script><script>b</script>c", BASE_URI);
  assertEquals("<html><head></head><body><script>a</script><script>b</script>c</body></html>",
          doc.html());
  }
  // ---- Nested content inside script ----
  @Test
  public void scriptWithInnerMarkupThenTextAfter() {
  Document doc = Parser.parseBodyFragment("<script>a <b> c</script> after", BASE_URI);
  // inner markup must be treated as raw data, then "after" follows outside
  assertEquals("<html><head></head><body><script>a <b> c</script> after</body></html>",
          doc.html());
  }
  // ---- Edge cases ----
  @Test
  public void emptyScriptTagWithTextAfter() {
  Document doc = Parser.parseBodyFragment("<script></script> after", BASE_URI);
  assertEquals("<html><head></head><body><script></script> after</body></html>",
          doc.html());
  }
  @Test
  public void textAfterCdata() {
  Document doc = Parser.parseBodyFragment("<![CDATA[raw]]> after", BASE_URI);
  assertEquals("<html><head></head><body>raw after</body></html>",
          doc.html());
  }
  @Test
  public void textAfterScriptInNestedDiv() {
  Document doc = Parser.parseBodyFragment("<div><script>s</script> out</div>", BASE_URI);
  assertEquals("<html><head></head><body><div><script>s</script> out</div></body></html>",
          doc.html());
  }
  // ---- Full document parse ----
  @Test
  public void fullDocumentParseWithTextAfterScript() {
  Document doc = Parser.parse("<html><head></head><body><script>inner</script>aft</body></html>",
BASE_URI);
  assertEquals("<html><head></head><body><script>inner</script> aft</body></html>",
          doc.html());
  }
  // ---- No trailing text ----
  @Test
  public void scriptWithoutTrailingText() {
  Document doc = Parser.parseBodyFragment("<script>inner</script>", BASE_URI);
  assertEquals("<html><head></head><body><script>inner</script></body></html>",
          doc.html());
  }

}