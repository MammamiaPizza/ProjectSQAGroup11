import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;
 import org.apache.commons.lang3.StringUtils;

 /**
  * Tests for {@link StringUtils#equals(CharSequence, CharSequence)} and
  * {@link StringUtils#equalsIgnoreCase(CharSequence, CharSequence)}.
  * Targets the defect described in LANG-786 where non-String
  * {@link CharSequence} implementations were not compared by content.
  */
 public class StringUtilsEqualsTest {

     // ---------- equals(CharSequence, CharSequence) ----------

     @Test
     public void testEquals_BothNull_ReturnsTrue() {
         assertTrue(StringUtils.equals(null, null));
     }

     @Test
     public void testEquals_OneNullOneNonNull_ReturnsFalse() {
         assertFalse(StringUtils.equals(null, "abc"));
         assertFalse(StringUtils.equals("abc", null));
     }

     @Test
     public void testEquals_SameStringContent_ReturnsTrue() {
         assertTrue(StringUtils.equals("abc", "abc"));
     }

     @Test
     public void testEquals_DifferentContent_ReturnsFalse() {
         assertFalse(StringUtils.equals("abc", "abcd"));
         assertFalse(StringUtils.equals("abc", "ABC"));
     }

     @Test
     public void testEquals_DifferentTypesSameContent_ReturnsTrue() {
         String s = "hello";
         StringBuilder sb = new StringBuilder("hello");
         assertTrue(StringUtils.equals(s, sb));
         assertTrue(StringUtils.equals(sb, s));
     }

     @Test
     public void testEquals_DifferentTypesDifferentContent_ReturnsFalse() {
         assertFalse(StringUtils.equals("hello", new StringBuilder("world")));
     }

     @Test
     public void testEquals_BothEmptyDifferentTypes_ReturnsTrue() {
         assertTrue(StringUtils.equals("", new StringBuilder()));
         assertTrue(StringUtils.equals(new StringBuilder(), ""));
     }

     @Test
     public void testEquals_SupplementaryCharacters_ReturnsTrue() {
         // two code points: U+10400 DESERET CAPITAL LETTER LONG I
         String supplementary = new String(Character.toChars(0x10400)) + "a";
         StringBuilder sb = new StringBuilder(supplementary);
         assertTrue(StringUtils.equals(supplementary, sb));
     }

     @Test
     public void testEquals_VeryLongEqualSequences_ReturnsTrue() {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 10_000; i++) {
             sb.append('x');
         }
         String longStr = sb.toString();
         assertTrue(StringUtils.equals(longStr, sb));
     }

     // ---------- equalsIgnoreCase(CharSequence, CharSequence) ----------

     @Test
     public void testEqualsIgnoreCase_BothNull_ReturnsTrue() {
         assertTrue(StringUtils.equalsIgnoreCase(null, null));
     }

     @Test
     public void testEqualsIgnoreCase_OneNullOneNonNull_ReturnsFalse() {
         assertFalse(StringUtils.equalsIgnoreCase(null, "abc"));
         assertFalse(StringUtils.equalsIgnoreCase("abc", null));
     }

     @Test
     public void testEqualsIgnoreCase_MixedCaseSameContent_ReturnsTrue() {
         assertTrue(StringUtils.equalsIgnoreCase("Hello World", "hELlO wOrLd"));
         // cross-type with case difference
         assertTrue(StringUtils.equalsIgnoreCase("MixedCase", new StringBuilder("MIXEDcASE")));
     }

     @Test
     public void testEqualsIgnoreCase_DifferentContent_ReturnsFalse() {
         assertFalse(StringUtils.equalsIgnoreCase("abc", "abcd"));
         assertFalse(StringUtils.equalsIgnoreCase("abc", new StringBuilder("xyz")));
     }
 }
