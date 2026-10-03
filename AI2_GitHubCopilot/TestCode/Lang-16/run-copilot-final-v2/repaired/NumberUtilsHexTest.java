import static org.hamcrest.CoreMatchers.*;
 import static org.junit.Assert.*;
 import org.junit.Test;

 public class NumberUtilsHexTest {

     @Test
     public void testCreateNumberHexLowercase() {
         Number n = org.apache.commons.lang3.math.NumberUtils.createNumber("0x1a");
         assertEquals(Long.decode("0x1a").longValue(), n.longValue());
     }

     @Test
     public void testCreateNumberHexUppercaseX() {
         Number n = org.apache.commons.lang3.math.NumberUtils.createNumber("0Xfade");
         assertEquals(Long.decode("0Xfade").longValue(), n.longValue());
     }

     @Test
     public void testCreateNumberHexZero() {
         assertEquals(0L,
 org.apache.commons.lang3.math.NumberUtils.createNumber("0x0").longValue());
         assertEquals(0L,
 org.apache.commons.lang3.math.NumberUtils.createNumber("0X0").longValue());
     }

     @Test
     public void testCreateNumberNegativeHex() {
         Number n = org.apache.commons.lang3.math.NumberUtils.createNumber("-0xF");
         assertEquals(-15L, n.longValue());
     }

     @Test
     public void testCreateNumberHexLongMax() {
         Number n = org.apache.commons.lang3.math.NumberUtils.createNumber("0x7fffffffffffffff");
         assertEquals(Long.MAX_VALUE, n.longValue());
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexBeyondLongMax() {
         org.apache.commons.lang3.math.NumberUtils.createNumber("0X8000000000000000");
     }

     @Test
     public void testCreateNumberHexLargerThanInt() {
         Number n = org.apache.commons.lang3.math.NumberUtils.createNumber("0xffffffff");
         assertThat(n, instanceOf(Number.class));
         assertEquals(4294967295L, n.longValue());
     }

     @Test
     public void testCreateNumberHexOnlyPrefixes() {
         try {
             org.apache.commons.lang3.math.NumberUtils.createNumber("0x");
             fail("Expected NumberFormatException for '0x'");
         } catch (NumberFormatException e) { /* expected */ }
         try {
             org.apache.commons.lang3.math.NumberUtils.createNumber("0X");
             fail("Expected NumberFormatException for '0X'");
         } catch (NumberFormatException e) { /* expected */ }
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexMalformed() {
         org.apache.commons.lang3.math.NumberUtils.createNumber("0xGHI");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexWithTrailingSpace() {
         org.apache.commons.lang3.math.NumberUtils.createNumber("0x ");
     }

     @Test
     public void testCreateNumberInvalidInputs() {
         try {
             org.apache.commons.lang3.math.NumberUtils.createNumber(null);
             fail("Expected IllegalArgumentException for null");
         } catch (IllegalArgumentException e) { /* expected */ }
         try {
             org.apache.commons.lang3.math.NumberUtils.createNumber("");
             fail("Expected NumberFormatException for empty string");
         } catch (NumberFormatException e) { /* expected */ }
     }

     @Test
     public void testCreateNumberDecimalWorks() {
         Number n = org.apache.commons.lang3.math.NumberUtils.createNumber("123");
         assertEquals(123, n.intValue());
     }
 }
