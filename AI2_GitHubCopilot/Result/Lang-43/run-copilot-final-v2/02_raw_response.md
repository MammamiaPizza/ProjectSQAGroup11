package org.apache.commons.lang.text;

 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Assert;
 import org.junit.Test;

 public class ExtendedMessageFormatTest {

     private Map<String, FormatFactory> createRegistry() {
         return new HashMap<String, FormatFactory>();
     }

     @Test
     public void testSimplePatternNoCustomRegistry() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("Hello {0}");
         String pattern = emf.toPattern();
         Assert.assertNotNull("toPattern should not be null", pattern);
         Assert.assertTrue("Pattern should contain {0}", pattern.contains("{0}"));
     }

     @Test
     public void testEscapedQuoteOutsideFormatNoRegistry() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("''{0}");
         String pattern = emf.toPattern();
         Assert.assertNotNull(pattern);
         Assert.assertTrue("Pattern should contain escaped quote", pattern.contains("'"));
     }

     @Test(timeout = 2000)
     public void testEscapedQuoteInsideFormatWithCustomRegistry() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("{0,date,''}", createRegistry());
         String pattern = emf.toPattern();
         Assert.assertNotNull("toPattern should not be null", pattern);
     }

     @Test(timeout = 2000)
     public void testMultipleFormatElementsWithEscapedQuote() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("{0,number,''}{1,date,short}",
createRegistry());
         Assert.assertNotNull(emf.toPattern());
     }

     @Test(timeout = 2000)
     public void testFormatDescriptionWithNestedQuotes() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("{0,choice,0#''none''|1#''one''}",
createRegistry());
         Assert.assertNotNull(emf.toPattern());
     }

     @Test
     public void testQuotesAtStartAndEndNoRegistry() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("''{0}''");
         Assert.assertNotNull(emf.toPattern());
     }

     @Test
     public void testApplyPatternChangesOutput() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("Hello {0}", createRegistry());
         String original = emf.toPattern();
         emf.applyPattern("World {1}");
         String updated = emf.toPattern();
         Assert.assertNotNull(updated);
         Assert.assertFalse(updated.contains("Hello"));
         Assert.assertTrue(updated.contains("World"));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testUnterminatedQuotedStringThrowsException() {
         new ExtendedMessageFormat("'{0}", createRegistry());
     }

     @Test(timeout = 2000)
     public void testDeeplyNestedFormatElements() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("{0,choice,0#{1,number,integer}|1#}",
createRegistry());
         Assert.assertNotNull(emf.toPattern());
     }

     @Test
     public void testPlainTextWithRegistry() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("plain text", createRegistry());
         Assert.assertNotNull(emf.toPattern());
     }

     @Test
     public void testMultipleEscapedQuotes() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("''''{0}''''", createRegistry());
         Assert.assertNotNull(emf.toPattern());
     }

     @Test(timeout = 2000)
     public void testEscapedQuoteAtEndOfFormatDescription() {
         ExtendedMessageFormat emf = new ExtendedMessageFormat("{0,date,'''}", createRegistry());
         Assert.assertNotNull(emf.toPattern());
     }
 }