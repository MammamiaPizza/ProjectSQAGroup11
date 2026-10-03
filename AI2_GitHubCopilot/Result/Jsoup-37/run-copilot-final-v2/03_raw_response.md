package org.jsoup.nodes;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.TextNode;
 import org.jsoup.parser.Tag;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 public class ElementTest {

  @Test
  public void testPrettyPrintFalseProducesCompactHtml() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element div = new Element(Tag.valueOf("div"), "");
      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("Hello");
      div.appendChild(p);
      body.appendChild(div);

      String html = div.html();
      assertEquals("\n <p>Hello</p>\n", html);
  }

  @Test
  public void testPrettyPrintFalseNestedBlocks() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element outer = new Element(Tag.valueOf("div"), "");
      Element inner = new Element(Tag.valueOf("div"), "");
      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("Nested");
      inner.appendChild(p);
      outer.appendChild(inner);
      body.appendChild(outer);

      assertEquals("\n <div>\n  <p>Nested</p>\n </div>\n", outer.html());
  }

  @Test
  public void testPrettyPrintFalseInlineElements() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element p = new Element(Tag.valueOf("p"), "");
      Element span = new Element(Tag.valueOf("span"), "");
      span.appendText("inline");
      p.appendChild(span);
      body.appendChild(p);

      assertEquals("<span>inline</span>", p.html());
  }

  @Test
  public void testPrettyPrintFalseTextWithNewlines() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("Line1\nLine2");
      body.appendChild(p);

      assertEquals("Line1\nLine2", p.html());
  }

  @Test
  public void testPrettyPrintFalseAfterChangeSettings() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element div = new Element(Tag.valueOf("div"), "");
      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("Hello");
      div.appendChild(p);
      body.appendChild(div);

      String htmlFalse = div.html();
      assertTrue("Output should contain newline", htmlFalse.contains("\n"));

      doc.outputSettings().prettyPrint(true);
      String htmlTrue = div.html();

      assertEquals("Both outputs should be indented when prettyPrint(false) has no effect",
htmlFalse, htmlTrue);
      assertTrue("True output should contain newline", htmlTrue.contains("\n"));
  }

  @Test
  public void testToStringMatchesHtmlWhenPrettyPrintFalse() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element div = new Element(Tag.valueOf("div"), "");
      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("Test");
      div.appendChild(p);
      body.appendChild(div);

      assertTrue("toString should contain the inner HTML", div.toString().contains(div.html()));
      assertTrue("toString should start with opening tag", div.toString().startsWith("<div>"));
  }

  @Test
  public void testPrettyPrintTrueProducesIndentedOutput() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(true);
      Element body = doc.body();

      Element div = new Element(Tag.valueOf("div"), "");
      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("Hello");
      div.appendChild(p);
      body.appendChild(div);

      String html = div.html();
      assertTrue("Indented HTML should contain newline", html.contains("\n"));
      assertEquals("\n <p>Hello</p>\n", html);
  }

  @Test
  public void testPrettyPrintFalseEmptyElement() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element div = new Element(Tag.valueOf("div"), "");
      body.appendChild(div);

      assertEquals("", div.html());
  }

  @Test
  public void testPrettyPrintFalseSingleChild() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element parent = new Element(Tag.valueOf("div"), "";
      Element child = new Element(Tag.valueOf("p"), "");
      parent.appendChild(child);
      body.appendChild(parent);

      assertEquals("\n <p></p>\n", parent.html());
  }

  @Test
  public void testPrettyPrintFalseWithBlankText() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("   ");
      body.appendChild(p);

      assertEquals("   ", p.html());
  }

  @Test
  public void testPrettyPrintFalseMultipleChildren() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element div = new Element(Tag.valueOf("div"), "");
      Element first = new Element(Tag.valueOf("p"), "");
      first.appendText("First");
      Element second = new Element(Tag.valueOf("p"), "");
      second.appendText("Second");
      div.appendChild(first);
      div.appendChild(second);
      body.appendChild(div);

      assertEquals("\n <p>First</p>\n <p>Second</p>\n", div.html());
  }

  @Test
  public void testPrettyPrintFalseNewlineOnlyText() {
      Document doc = Jsoup.parse("<html><head></head><body></body></html>");
      doc.outputSettings().prettyPrint(false);
      Element body = doc.body();

      Element p = new Element(Tag.valueOf("p"), "");
      p.appendText("\n");
      body.appendChild(p);

      assertEquals("\n", p.html());
  }

 }