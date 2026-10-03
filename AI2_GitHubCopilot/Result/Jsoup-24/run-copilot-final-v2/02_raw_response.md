package org.jsoup.parser;

import org.junit.Test;
import static org.junit.Assert.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**

 - Tests targeting {@link TokeniserState} constant behaviors,
 - specifically the script-data-escaped states when encountering
 - quotes inside script comments.
 - The bug (issue #115) would cause quotes to prematurely exit
 - the escaped script-data state, mangling the comment content.
  */
 public class TokeniserStateTest {
  // ---- direct reproduction of the bug-triggering test ----
  @Test
  public void handlesQuotesInCommentsInScripts() {
  // HTML with a script containing an HTML-style comment (<!--),
  // a JavaScript string with a single quote, and a JavaScript
  // line comment (//-->). The single quote must not break the
  // escaped-script comment context.
  String fragment = "<script><!-- document.write(\'</scr[\'] + \'ipt>\'); //--></script>";
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
  // the quote must be present and the comment must stay a comment
  assertTrue(scriptContent.contains("'</script>'"));
  }
  @Test
  public void doubleQuoteInsideScriptHtmlComment() {
  String fragment = "<script><!-- document.write(\"</script>\"); //--></script>";
  Document doc = Jsoup.parseBodyFragment(fragment);
  String scriptContent = doc.select("script").first().html();
  assertTrue(scriptContent.contains(""</script>""));
  }
  @Test
  public void mixedQuotesInsideEscapedComment() {
  String fragment = "<script><!-- var s = 'single\"mixed\\'quote'; //--></script>";
  Document doc = Jsoup.parseBodyFragment(fragment);
  String scriptContent = doc.select("script").first().html();
  assertTrue(scriptContent.contains("'single"mixed\'quote'"));
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
  // The sequence "</script>" inside a comment should not close the script tag
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
  // The newline and quote after the comment still belong to the script,
  // not just the comment.
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
  // An unclosed <!-- comment (without //-->) that reaches the script end tag
  String fragment = "<script><!-- document.write("unclosed_ipt>")</script>";
  Document doc = Jsoup.parseBodyFragment(fragment);
  String bodyHtml = doc.body().html();
  // The unclosed comment should be swallowed inside the script data;
  // the quote characters should not break out.
  assertTrue(bodyHtml.contains("unclosed_ipt>"));
  }
  @Test
  public void nestedCommentLikeTokensAfterQuote() {
  // Quote followed by a pseudo-comment token that is not a real close
  String fragment = "<script>// '<!-- not a real comment //--></script>";
  Document doc = Jsoup.parseBodyFragment(fragment);
  String bodyHtml = doc.body().html();
  // The parser must not be tricked into treating the inner <!-- as a real comment
  assertTrue(bodyHtml.contains("<!--"));
  }
  @Test
  public void multipleEscapedDashesAroundQuotes() {
  // Tests the ScriptDataEscapedDash transitions around quote characters
  String fragment = "<script><!-- -- '--dash' -- ' //--></script>";
  Document doc = Jsoup.parseBodyFragment(fragment);
  String bodyHtml = doc.body().html();
  assertTrue(bodyHtml.contains("--dash"));
  }

}


 Note: I made some typos like `String fragment` repeated. I'll correct.

 I'll write the final answer neatly```java
 package org.jsoup.parser;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;

 /**
  * Tests targeting {@link TokeniserState} script-data-escaped variants
  * for the bug described in D4J Jsoup-24b (issue #115).
  * Verifies that quotes inside script comments (//... and &lt;!--... //--&gt;)
  * are preserved and do not cause premature exit from the escaped state.
  */
 public class TokeniserStateTest {

     // ---- reproduction of the original failing test ----

     @Test
     public void handlesQuotesInCommentsInScripts() {
         String fragment = "<script><!-- document.write(\'</scr[\'] + \'ipt\'); //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue("Expected the quoted portion to survive parsing",
                 bodyHtml.contains("</scr['] + 'ipt>"));
     }

     // ---- normal: single/double quotes inside line and HTML comments ----

     @Test
     public void singleQuotelInsideScriptLineComment() {
         String fragment = "<script>// document.write('</script>');</script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue(scriptContent.contains("'</script>'"));
     }

     @Test
     public void doubleQuotelInsideScriptHtmlComment() {
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

     // ---- boundary conditions ----

     @Test
     public void quotelAtStartOfHtmlComment() {
         String fragment = "<script><!-- 'leading //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue("Quote at start of comment should be retained",
                 scriptContent.contains("'leading"));
     }

     @Test
     public void quotelAtEndOfHtmlCommentBeforeClose() {
         String fragment = "<script><!-- ending' //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue("Quote at end of comment should be retained",
                 scriptContent.contains("ending'"));
     }

     @Test
     public void quotelImmediatelyBeforeScriptEndTag() {
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
         String content = doc.select("script").first().html();
         assertTrue(content.contains("'"));
     }

     @Test
     public void consecutiveQuotelCharacters() {
         String fragment = "<script><!-- ' '' '''' //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String scriptContent = doc.select("script").first().html();
         assertTrue(scriptContent.contains("' '' ''''"));
     }

     // ---- error / invalid but defined branches ----

     @Test
     public void unclosedHtmlCommentContainingQuotel() {
         String fragment = "<script><!-- document.write(\"unclosed_ipt>\")</script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue(bodyHtml.contains("unclosed_ipt>"));
     }

     @Test
     public void nestedCommentLikeTokensAfterQuotel() {
         String fragment = "<script>// '<!- not a real comment //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue(bodyHtml.contains("<!-"));
     }

     @Test
     public void multipleEscapedDashesAroundQuotes() {
         String fragment = "<script><!-- -- '--dash' -- ' //--></script>";
         Document doc = Jsoup.parseBodyFragment(fragment);
         String bodyHtml = doc.body().html();
         assertTrue(bodyHtml.contains("--dash"));
     }
 }