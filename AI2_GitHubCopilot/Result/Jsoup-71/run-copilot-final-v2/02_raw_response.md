package org.jsoup.select;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.select.Elements;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class QueryParserMatchTextTest {

     @Test
     public void testParseMatchTextSimple() {
         Evaluator eval = QueryParser.parse("p:matchText");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextWithFirstChild() {
         Evaluator eval = QueryParser.parse("p:matchText:first-child");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextUniversal() {
         Evaluator eval = QueryParser.parse(":matchText");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextWildcard() {
         Evaluator eval = QueryParser.parse("*:matchText");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextWithCombinator() {
         Evaluator eval = QueryParser.parse("div > p:matchText");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextWithClass() {
         Evaluator eval = QueryParser.parse("p.myclass:matchText");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextWithContains() {
         Evaluator eval = QueryParser.parse("p:matchText:contains(hello)");
         assertNotNull(eval);
     }

     @Test
     public void testParseMatchTextWithNot() {
         Evaluator eval = QueryParser.parse("p:not(:matchText)");
         assertNotNull(eval);
     }

     @Test
     public void testSelectMatchTextDoesNotThrow() {
         Document doc = Jsoup.parse("<p>Hello</p><div>World</div>");
         Elements result = doc.select("p:matchText");
         assertNotNull(result);
         // Verify no exception is thrown; existence of result is sufficient
     }

     @Test(expected = Selector.SelectorParseException.class)
     public void testEmptyQueryThrows() {
         QueryParser.parse("");
     }

     @Test(expected = Selector.SelectorParseException.class)
     public void testUnknownPseudoThrows() {
         QueryParser.parse("p:unknown");
     }

     @Test(expected = Selector.SelectorParseException.class)
     public void testInvalidPseudoSyntaxThrows() {
         QueryParser.parse(":matchText(");
     }
 }