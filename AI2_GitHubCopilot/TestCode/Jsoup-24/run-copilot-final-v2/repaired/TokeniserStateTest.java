package org.jsoup.parser;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;

 /**
  * Tests targeting {@link TokeniserState} constant behaviors,
  * specifically the script-data-escaped states when encountering
  * quotes inside script comments.
  * The bug (issue #115) would cause quotes to prematurely exit
  * the escaped script-data state, mangling the comment content.
  */
 public class TokeniserStateTest {

     // ---- direct reproduction of the bug-triggering test ----

     @Test
     public void handlesQuotesInCommentsInScripts() {
         String fragment = "<script><!-- document.write(\'</scr[\'] + \'ipt\'>); //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue("Expected the quoted portion to survive parsing",
                 bodyHtml.contains("</scr['] + 'ipt>"));
     }

     // ---- normal cases : single and double quotes in // and <!-- ----

     @Test
     public void singleQuoteInsideScriptLineComment() {
         String fragment = "<script>// document.write('</script>');</script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue(scriptContent.contains("'</script>'"));
     }

     @Test
     public void doubleQuoteInsideScriptHtmlComment() {
         String fragment = "<script><!-- document.write(\"</script>\"); //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue(scriptContent.contains("\"</script>\""));
     }

     @Test
     public void mixedQuotesInsideEscapedComment() {
         String fragment = "<script><!-- var s = 'single\"mixed\\'quote'; //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue(scriptContent.contains("'single\"mixed\\'quote'"));
     }

     // ---- boundary cases ----

     @Test
     public void quoteAtStartOfHtmlComment() {
         String fragment = "<script><!-- 'leading //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue("Quote at start of comment should be retained",
                 scriptContent.contains("'leading"));
     }

     @Test
     public void quoteAtEndOfHtmlCommentBeforeClose() {
         String fragment = "<script><!-- ending' //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue("Quote at end of comment should be retained",
                 scriptContent.contains("ending'"));
     }

     @Test
     public void quoteImmediatelyBeforeScriptEndTag() {
         String fragment = "<script><!-- '</script> should not close //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue("Quote before </script> must stay part of comment",
                 scriptContent.contains("</script>"));
     }

     @Test
     public void emptyHtmlCommentFollowedByQuotedCode() {
         String fragment = "<script><!-- //-->\n'\n</script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         Elements scripts = doc.select("script");
         String content = scripts.first().html();
         assertTrue(content.contains("'"));
     }

     @Test
     public void consecutiveQuoteCharacters() {
         String fragment = "<script><!-- ' '' '''' //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue(scriptContent.contains("' '' ''''"));
     }

     // ---- error / invalid but well-defined branches ----

     @Test
     public void unclosedHtmlCommentContainingQuote() {
         String fragment = "<script><!-- document.write(\"unclosed_ipt>\")</script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue(bodyHtml.contains("unclosed_ipt>"));
     }

     @Test
     public void nestedCommentLikeTokensAfterQuote() {
         String fragment = "<script>// '<!-- not a real comment //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue(bodyHtml.contains("<!--"));
     }

     @Test
     public void multipleEscapedDashesAroundQuotes() {
         String fragment = "<script><!-- -- '--dash' -- ' //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue(bodyHtml.contains("--dash"));
     }
 }
