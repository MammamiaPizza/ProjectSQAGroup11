package org.jsoup.parser;

 import org.junit.Test;
 import org.junit.Assert;

 import java.util.List;

 public class SelfClosingTagErrorTest {

     /**
      * Helper to parse HTML with error tracking and return the error list.
      */
     private List<ParseError> parseWithErrors(String html, int maxErrors) {
         Parser parser = new Parser();
         parser.setTrackErrors(maxErrors);
         parser.parseInput(html, "");
         return parser.getErrors();
     }

     private List<ParseError> parseWithErrors(String html) {
         // default large enough for all tests
         return parseWithErrors(html, 100);
     }

     /** Helper to check an error message is present */
     private void assertErrorContains(List<ParseError> errors, String expected, int position) {
         boolean found = false;
         for (ParseError error : errors) {
             if (error.getErrorMessage().contains(expected) && error.getPosition() == position) {
                 found = true;
                 break;
             }
         }
         Assert.assertTrue("Expected error '" + expected + "' at position " + position + " but
wasn't there", found);
     }

     @Test
     public void testSelfClosingNonVoidTagProducesError() {
         List<ParseError> errors = parseWithErrors("<div/>");
         Assert.assertEquals("Should have exactly 1 error", 1, errors.size());
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 18);
     }

     @Test
     public void testSelfClosingVoidTagProducesNoError() {
         List<ParseError> errors = parseWithErrors("<br/>");
         Assert.assertEquals("Void tag should produce no errors", 0, errors.size());
     }

     @Test
     public void testSelfClosingNonVoidTagWithAttributesProducesError() {
         List<ParseError> errors = parseWithErrors("<div class=\"foo\"/>");
         Assert.assertEquals(1, errors.size());
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 18);
     }

     @Test
     public void testSelfClosingVoidTagWithAttributesNoError() {
         List<ParseError> errors = parseWithErrors("<br class=\"bar\"/>");
         Assert.assertEquals(0, errors.size());
     }

     @Test
     public void testNestedSelfClosingNonVoidTag() {
         List<ParseError> errors = parseWithErrors("<div><p/></div>");
         Assert.assertEquals("Only the inner <p/> should error", 1, errors.size());
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 21);
     }

     @Test
     public void testMultipleNonVoidSelfClosingTags() {
         List<ParseError> errors = parseWithErrors("<div/><span/>");
         Assert.assertEquals(2, errors.size());
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 18);
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 24);
     }

     @Test
     public void testMixedTagsCorrectErrorCount() {
         List<ParseError> errors = parseWithErrors("<div/><br/><span/>");
         Assert.assertEquals(2, errors.size());
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 18);
         assertErrorContains(errors, "Tag cannot be self closing; not a void tag", 29);
     }

     @Test
     public void testVoidTagListNoError() {
         List<ParseError> errors = parseWithErrors("<br/><hr/><img/><input/>");
         Assert.assertEquals(0, errors.size());
     }

     @Test
     public void testParserTrackingErrorsWhenRequested() {
         // Simulates tracksErrorsWhenRequested: parse a larger document with error tracking.
         // The bug originally caused self-closing flag to generate the wrong error message.
         String html = "<html><head><title>test</title></head><body><div/><p/></body></html>";
         List<ParseError> errors = parseWithErrors(html);
         // Expect exactly 2 errors (for <div/> and <p/>)
         Assert.assertEquals(2, errors.size());
         // Positions might be different due to prefix, just check message content.
         boolean correctMessages = true;
         for (ParseError error : errors) {
             if (!error.getErrorMessage().contains("Tag cannot be self closing; not a void tag")) {
                 correctMessages = false;
                 break;
             }
         }
         Assert.assertTrue("All errors should be about non-void self-closing tags",
correctMessages);
     }

     @Test
     public void testFragmentParsingSelfClosingNonVoidError() {
         // Parse a fragment containing a self-closing non-void tag.
         Parser parser = new Parser();
         parser.setTrackErrors(100);
         parser.parseFragment("<div/>", null, "");
         List<ParseError> errors = parser.getErrors();
         Assert.assertEquals(1, errors.size());
         Assert.assertTrue(errors.get(0).getErrorMessage().contains("Tag cannot be self closing; not
a void tag"));
     }

     @Test
     public void testFragmentParsingSelfClosingVoidNoError() {
         Parser parser = new Parser();
         parser.setTrackErrors(100);
         parser.parseFragment("<br/>", null, "");
         List<ParseError> errors = parser.getErrors();
         Assert.assertEquals(0, errors.size());
     }
 }