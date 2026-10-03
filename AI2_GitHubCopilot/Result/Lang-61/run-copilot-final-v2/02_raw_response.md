package org.apache.commons.lang.text;

 import static org.junit.Assert.*;
 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests targeting LANG-294: deleteAll with empty-string matcher must not corrupt state,
  * and indexOf/lastIndexOf with empty string must follow String-like contract.
  */
 public class StrBuilderLang294Test {

     private StrBuilder builder;

     @Before
     public void setUp() {
         builder = new StrBuilder("abc");
     }

     // ---------- deleteAll with empty-string matcher ----------

     @Test
     public void testDeleteAllEmptyStringMatcherDoesNothing() {
         String original = builder.toString();
         builder.deleteAll(StrMatcher.stringMatcher(""));
         assertEquals("deleteAll with empty-string matcher must leave content unchanged",
                      original, builder.toString());
         assertEquals("size must remain unchanged", original.length(), builder.size());
     }

     @Test
     public void testDeleteAllEmptyStringMatcherOnEmptyBuilder() {
         StrBuilder empty = new StrBuilder();
         empty.deleteAll(StrMatcher.stringMatcher(""));
         assertEquals("", empty.toString());
         assertEquals(0, empty.size());
     }

     // ---------- indexOf with empty string ----------

     @Test
     public void testIndexOfEmptyStringAtZero() {
         assertEquals("indexOf(\"\", 0) should return 0", 0, builder.indexOf("", 0));
     }

     @Test
     public void testIndexOfEmptyStringAtPositive() {
         assertEquals(1, builder.indexOf("", 1));
     }

     @Test
     public void testIndexOfEmptyStringAtSize() {
         assertEquals(builder.size(), builder.indexOf("", builder.size()));
     }

     @Test
     public void testIndexOfEmptyStringPastEnd() {
         assertEquals(-1, builder.indexOf("", builder.size() + 1));
     }

     @Test
     public void testIndexOfEmptyStringOnEmptyBuilder() {
         StrBuilder empty = new StrBuilder();
         assertEquals(0, empty.indexOf("", 0));
     }

     // ---------- lastIndexOf with empty string ----------

     @Test
     public void testLastIndexOfEmptyStringAtEnd() {
         assertEquals(builder.size(), builder.lastIndexOf("", builder.size()));
     }

     @Test
     public void testLastIndexOfEmptyStringAtMid() {
         assertEquals(1, builder.lastIndexOf("", 1));
     }

     @Test
     public void testLastIndexOfEmptyStringAtZero() {
         assertEquals(0, builder.lastIndexOf("", 0));
     }

     @Test
     public void testLastIndexOfEmptyStringPastEnd() {
         // When startIndex > length, String returns -1; the builder should do the same.
         assertEquals(-1, builder.lastIndexOf("", builder.size() + 1));
     }

     @Test
     public void testLastIndexOfEmptyStringOnEmptyBuilder() {
         StrBuilder empty = new StrBuilder();
         assertEquals(0, empty.lastIndexOf("", 0));
         assertEquals(-1, empty.lastIndexOf("", 1));
     }
 }