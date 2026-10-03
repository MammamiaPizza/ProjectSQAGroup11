package org.apache.commons.codec.language;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class DoubleMetaphone2Test {

     @Test
     public void testNullInputReturnsNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertNull(dm.doubleMetaphone(null));
     }

     @Test
     public void testEmptyInputReturnsNull() {
         DoubleMetaphone dm = new DoubleMetaphone();
         assertNull(dm.doubleMetaphone(""));
     }

     @Test
     public void testPrimaryAngier() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // Primary code for "Angier" is ANKR
         assertEquals("ANKR", dm.doubleMetaphone("Angier"));
     }

     @Test
     public void testAlternateAngier() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // Alternate code for "Angier" must be ANJR (not ANKR)
         assertEquals("ANJR", dm.doubleMetaphone("Angier", true));
     }

     @Test
     public void testGFrontVowelAlternate() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // G + I/E/Y front vowels: primary K, alternate J
         assertEquals("KN", dm.doubleMetaphone("Gin"));        // primary
         assertEquals("JN", dm.doubleMetaphone("Gin", true));   // alternate
         assertEquals("KL", dm.doubleMetaphone("Gel"));
         assertEquals("JL", dm.doubleMetaphone("Gel", true));
         assertEquals("KM", dm.doubleMetaphone("Gym"));
         assertEquals("JM", dm.doubleMetaphone("Gym", true));
     }

     @Test
     public void testGNonFrontVowelAlternate() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // G + A/O/U (non front) gives K in both primary and alternate
         assertEquals("KL", dm.doubleMetaphone("Gal"));
         assertEquals("KL", dm.doubleMetaphone("Gal", true));
         assertEquals("KL", dm.doubleMetaphone("Gol"));
         assertEquals("KL", dm.doubleMetaphone("Gol", true));
         assertEquals("KL", dm.doubleMetaphone("Gul"));
         assertEquals("KL", dm.doubleMetaphone("Gul", true));
     }

     @Test
     public void testGConsonantAlternate() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // G followed by a consonant – both primary and alternate are K
         assertEquals("KL", dm.doubleMetaphone("GL"));
         assertEquals("KL", dm.doubleMetaphone("GL", true));
     }

     @Test
     public void testGHSilentH() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // GH before I at word start yields J
         assertEquals("J", dm.doubleMetaphone("GHI"));
         assertEquals("J", dm.doubleMetaphone("GHI", true));
     }

     @Test
     public void testGNStart() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // Initial GN is silent G – primary and alternate start with N
         assertEquals("NM", dm.doubleMetaphone("GNOME"));
         assertEquals("NM", dm.doubleMetaphone("GNOME", true));
     }

     @Test
     public void testDGEBoundary() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // DGE sequences encode as J
         assertEquals("AJ", dm.doubleMetaphone("EDGE"));
         assertEquals("AJ", dm.doubleMetaphone("EDGE", true));
     }

     @Test
     public void testMaxCodeLenOverride() {
         DoubleMetaphone dm = new DoubleMetaphone();
         dm.setMaxCodeLen(3);
         assertEquals("ANK", dm.doubleMetaphone("Angier"));
         assertEquals("ANJ", dm.doubleMetaphone("Angier", true));
     }

     @Test
     public void testIsDoubleMetaphoneEqualAlternate() {
         DoubleMetaphone dm = new DoubleMetaphone();
         // "Gin" and "Jin" share the same alternate code (JN)
         assertTrue(dm.isDoubleMetaphoneEqual("Gin", "Jin", true));
         assertFalse(dm.isDoubleMetaphoneEqual("Gin", "Jin", false));
     }
 }
