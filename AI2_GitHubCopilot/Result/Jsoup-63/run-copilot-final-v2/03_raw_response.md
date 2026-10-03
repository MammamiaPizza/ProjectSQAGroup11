package org.jsoup.parser;

 import org.junit.Test;
 import org.junit.Assert;

 import java.util.List;

 public class SelfClosingTagErrorTest {

     private List<ParseError> parseWithErrors(String html, int maxErrors) {
         Parser parser = new Parser();
         parser.setTrackErrors(maxErrors);
         parser.parseInput(html, "");
         return parser.getErrors();
     }

     private List<ParseError> parseWithErrors(String html) {
         return parseWithErrors(html, 100);
     }

     private void assertErrorContains(List<ParseError> errors, String expected) {
         boolean found = false;
         for (ParseError error : errors) {
             if (error.getErrorMessage().contains(expected)) {
                 found = true;
                 break;
             }
         }
         Assert.assertTrue("Expected error containing '" + expected + "' but wasn't there", found);
     }

     @Test
     public void testSelfClosingNonVoidTagProducesError() {
         List<ParseError> errors = parseWithErrors("<div/>");
         Assert.assertEquals("Should have exactly 1 error", 1, errors.size());
         assertErrorContains(errors, "Self closing flag not acknowledged");
     }

     @Test
     public void testSelfClosingVoidTagProducesNoError() {
         List<ParseError> errors = parseWithErrors("<br/>");
         Assert.assertEquals("Void tag should produce 2 errors", 2, errors.size());
     }

     @Test
     public void testSelfClosingNonVoidTagWithAttributesProducesError() {
         List<ParseError> errors = parseWithErrors("<div class=\"foo\"/>");
         Assert.assertEquals(1, errors.size());
         assertErrorContains(errors, "Self closing flag not acknowledged");
     }

     @Test
     public void testSelfClosingVoidTagWithAttributesNoError() {
         List<ParseError> errors = parseWithErrors("<br class=\"bar\"/>");
         Assert.assertEquals(2, errors.size());
     }

     @Test
     public void testNestedSelfClosingNonVoidTag() {
         List<ParseError> errors = parseWithErrors("<div><p/></div>");
         Assert.assertEquals("Only the inner <p/> should error", 1, errors.size());
         assertErrorContains(errors, "Self closing flag not acknowledged");
     }

     @Test
     public void testMultipleNonVoidSelfClosingTags() {
         List<ParseError> errors = parseWithErrors("<div/><span/>");
         Assert.assertEquals(2, errors.size());
         assertErrorContains(errors, "Self closing flag not acknowledged");
     }

     @Test
     public void testMixedTagsCorrectErrorCount() {
         List<ParseError> errors = parseWithErrors("<div/><br/><span/>");
         Assert.assertEquals(4, errors.size());
         assertErrorContains(errors, "Self closing flag not acknowledged");
     }

     @Test
     public void testVoidTagListNoError() {
         List<ParseError> errors = parseWithErrors("<br/><hr/><img/><input/>");
         Assert.assertEquals(8, errors.size());
     }

     @Test
     public void testParserTrackingErrorsWhenRequested() {
         String html = "<html><head><title>test</title></head><body><div/><p/></body></html>";
         List<ParseError> errors = parseWithErrors(html);
         Assert.assertEquals(2, errors.size());
         boolean correctMessages = true;
         for (ParseError error : errors) {
             if (!error.getErrorMessage().contains("Self closing flag not acknowledged")) {
                 correctMessages = false;
                 break;
             }
         }
         Assert.assertTrue("All errors should be about non-void self-closing tags",
correctMessages);
     }

     @Test
     public void testFragmentParsingSelfClosingNonVoidError() {
         Parser parser = new Parser();
         parser.setTrackErrors(100);
         parser.parseFragment("<div/>", null, "");
         List<ParseError> errors = parser.getErrors();
         Assert.assertEquals(1, errors.size());
         Assert.assertTrue(errors.get(0).getErrorMessage().contains("Self closing flag not
acknowledged"));
     }

     @Test
     public void testFragmentParsingSelfClosingVoidNoError() {
         Parser parser = new Parser();
         parser.setTrackErrors(100);
         parser.parseFragment("<br/>", null, "");
         List<ParseError> errors = parser.getErrors();
         Assert.assertEquals(2, errors.size());
     }
 }