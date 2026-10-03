package org.apache.commons.cli;

 import junit.framework.TestCase;

 public class UtilTest extends TestCase {

     public void testStripLeadingHyphensNull() {
         try {
             assertNull(Util.stripLeadingHyphens(null));
         } catch (NullPointerException e) {
             fail("stripLeadingHyphens(null) should not throw NullPointerException");
         }
     }

     public void testStripLeadingHyphensEmpty() {
         assertEquals("", Util.stripLeadingHyphens(""));
     }

     public void testStripLeadingHyphensNoHyphen() {
         assertEquals("word", Util.stripLeadingHyphens("word"));
     }

     public void testStripLeadingHyphensSingleHyphen() {
         assertEquals("s", Util.stripLeadingHyphens("-s"));
     }

     public void testStripLeadingHyphensDoubleHyphen() {
         assertEquals("long", Util.stripLeadingHyphens("--long"));
     }

     public void testStripLeadingHyphensTripleHyphen() {
         assertEquals("-", Util.stripLeadingHyphens("---"));
     }

     public void testStripLeadingHyphensOnlyOneHyphen() {
         assertEquals("", Util.stripLeadingHyphens("-"));
     }

     public void testStripLeadingHyphensOnlyTwoHyphens() {
         assertEquals("", Util.stripLeadingHyphens("--"));
     }

     public void testStripLeadingHyphensHyphenEmbedded() {
         assertEquals("a-b", Util.stripLeadingHyphens("a-b"));
     }

     public void testStripLeadingHyphensLeadingSpaceNoStrip() {
         assertEquals(" -x", Util.stripLeadingHyphens(" -x"));
     }

     public void testStripLeadingHyphensDoubleHyphenLong() {
         assertEquals("very-long", Util.stripLeadingHyphens("--very-long"));
     }

     public void testStripLeadingHyphensSingleHyphenPreservesInner() {
         assertEquals("a-b", Util.stripLeadingHyphens("-a-b"));
     }
 }