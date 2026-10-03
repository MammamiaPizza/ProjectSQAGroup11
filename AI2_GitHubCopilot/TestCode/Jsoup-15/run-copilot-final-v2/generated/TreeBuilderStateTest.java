package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

/**

 - Tests for data-only tag handling in TreeBuilderState (script, style, title, etc.).
 - Focuses on the bug where raw text/RCDATA tags inject extraneous characters.
  */
 public class TreeBuilderStateTest {
  @Test
  public void handlesDataOnlyTags() {
  Document doc = Jsoup.parse("<script>Hello There</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("Hello There", script.html());
  }
  @Test
  public void scriptTagWithNormalText() {
  Document doc = Jsoup.parse("<script>console.log('test');</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("console.log('test');", script.html());
  }
  @Test
  public void styleTagWithNormalText() {
  Document doc = Jsoup.parse("<style>body { color: red; }</style>");
  Element style = doc.select("style").first();
  assertNotNull(style);
  assertEquals("body { color: red; }", style.html());
  }
  @Test
  public void titleTagWithNormalText() {
  Document doc = Jsoup.parse("<title>Hello There</title>");
  Element title = doc.select("title").first();
  assertNotNull(title);
  assertEquals("Hello There", title.html());
  }
  @Test
  public void scriptTagWithAngleBrackets() {
  Document doc = Jsoup.parse("<script>if (a < b && c > d) {}</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("if (a < b && c > d) {}", script.html());
  }
  @Test
  public void scriptTagWithHtmlEntities() {
  Document doc = Jsoup.parse("<script>& < > "</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("& < > "", script.html());
  }
  @Test
  public void scriptTagWithNestedEndScript() {
  Document doc = Jsoup.parse("<script>var s = "</script>";</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("var s = "", script.html());
  }
  @Test
  public void emptyScriptTag() {
  Document doc = Jsoup.parse("<script></script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("", script.html());
  }
  @Test
  public void emptyStyleTag() {
  Document doc = Jsoup.parse("<style></style>");
  Element style = doc.select("style").first();
  assertNotNull(style);
  assertEquals("", style.html());
  }
  @Test
  public void multipleAdjacentDataTags() {
  Document doc = Jsoup.parse("<script>first</script><style>second</style>");
  Element script = doc.select("script").first();
  Element style = doc.select("style").first();
  assertNotNull(script);
  assertNotNull(style);
  assertEquals("first", script.html());
  assertEquals("second", style.html());
  }
  @Test
  public void scriptTagWithWhitespaceOnly() {
  Document doc = Jsoup.parse("<script>   </script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("   ", script.html());
  }
  @Test
  public void longScriptContent() {
  StringBuilder sb = new StringBuilder(1000);
  for (int i = 0; i < 1000; i++) sb.append('a');
  String content = sb.toString();
  Document doc = Jsoup.parse("<script>" + content + "</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals(content, script.html());
  }
  @Test
  public void scriptTagWithAttributes() {
  Document doc = Jsoup.parse("<script type="text/javascript">data</script>");
  Element script = doc.select("script").first();
  assertNotNull(script);
  assertEquals("data", script.html());
  assertEquals("text/javascript", script.attr("type"));
  }

}
