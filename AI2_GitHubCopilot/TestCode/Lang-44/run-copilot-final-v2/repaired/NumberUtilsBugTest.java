package org.apache.commons.lang;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import static org.hamcrest.CoreMatchers.*;

 /**
  * JUnit test class targeting the LANG-457 bug in NumberUtils.createNumber().
  * The bug causes StringIndexOutOfBoundsException when the mantissa becomes empty
  * after stripping a type suffix (L, l, F, f, D, d).
  */
 public class NumberUtilsBugTest {

     // ---------- normal valid numbers ----------

     @Test
     public void testNormalIntegers() {
         assertThat(NumberUtils.createNumber("123"), instanceOf(Integer.class));
         assertThat(NumberUtils.createNumber("-45"), instanceOf(Integer.class));
         assertThat(NumberUtils.createNumber("0"),   instanceOf(Integer.class));
         assertThat(NumberUtils.createNumber("-0"),  instanceOf(Integer.class));
     }

     @Test
     public void testLongWithSuffix() {
         assertThat(NumberUtils.createNumber("1L"),  instanceOf(Long.class));
         assertThat(NumberUtils.createNumber("0L"),  instanceOf(Long.class));
         assertThat(NumberUtils.createNumber("-0L"), instanceOf(Long.class));
     }

     @Test
     public void testFloatDoubleSuffix() {
         assertThat(NumberUtils.createNumber("1.0f"), instanceOf(Float.class));
         assertThat(NumberUtils.createNumber("3.14f"),instanceOf(Float.class));
         assertThat(NumberUtils.createNumber("0.0D"), instanceOf(Double.class));
         assertThat(NumberUtils.createNumber("1.0d"), instanceOf(Double.class));
         assertThat(NumberUtils.createNumber("1e3D"), instanceOf(Double.class));
     }

     @Test
     public void testHexNumbers() {
         assertThat(NumberUtils.createNumber("0x1A"), instanceOf(Integer.class));
         assertThat(NumberUtils.createNumber("0x0"),  instanceOf(Integer.class));
         assertThat(NumberUtils.createNumber("-0x0"), instanceOf(Integer.class));
     }

     @Test
     public void testScientificNotation() {
         assertThat(NumberUtils.createNumber("1e3"),  instanceOf(Double.class));
         assertThat(NumberUtils.createNumber("1e-3"), instanceOf(Double.class));
     }

     // ---------- LANG-457 triggers ----------

     @Test
     public void testEmptyAndSingleSuffixThrows() {
         String[] inputs = {"", "L", "l", "F", "f", "D", "d"};
         for (String in : inputs) {
             try {
                 NumberUtils.createNumber(in);
                 fail("Expected NumberFormatException for input: '" + in + "'");
             } catch (NumberFormatException expected) {
                 // expected
             } catch (StringIndexOutOfBoundsException e) {
                 fail("BUG LANG-457 triggered: StringIndexOutOfBoundsException for input: '" + in +
 "'");
             }
         }
     }

     @Test
     public void testHexPrefixWithSuffixThrows() {
         String[] inputs = {"0xL", "-0xL", "0xl", "-0xl"};
         for (String in : inputs) {
             try {
                 NumberUtils.createNumber(in);
                 fail("Expected NumberFormatException for input: '" + in + "'");
             } catch (NumberFormatException expected) {
                 // expected
             } catch (IndexOutOfBoundsException e) {
                 fail("IndexOutOfBoundsException for input: '" + in + "'");
             }
         }
     }

     @Test
     public void testSignedSuffixThrows() {
         String[] inputs = {"- L", "+ L"};
         for (String in : inputs) {
             try {
                 NumberUtils.createNumber(in);
                 fail("Expected NumberFormatException for input: '" + in + "'");
             } catch (NumberFormatException expected) {
                 // expected
             } catch (IndexOutOfBoundsException e) {
                 fail("IndexOutOfBoundsException for input: '" + in + "'");
             }
         }
     }

     @Test
     public void testInvalidHexThrows() {
         String[] inputs = {"0x G", "0x.0", "0x", "0x-1A"};
         for (String in : inputs) {
             try {
                 NumberUtils.createNumber(in);
                 fail("Expected NumberFormatException for input: '" + in + "'");
             } catch (NumberFormatException expected) {
                 // expected
             } catch (IndexOutOfBoundsException e) {
                 fail("IndexOutOfBoundsException for input: '" + in + "'");
             }
         }
     }

     @Test
     public void testDoubleDashThrows() {
         try {
             NumberUtils.createNumber("--1");
             fail("Expected NumberFormatException for '--1'");
         } catch (NumberFormatException expected) {
             // expected
         }
     }

     @Test
     public void testNullThrows() {
         try {
             NumberUtils.createNumber(null);
             fail("Expected NumberFormatException for null");
         } catch (NumberFormatException expected) {
             // expected
         }
     }

     @Test
     public void testDecimalWithSuffix() {
         assertThat(NumberUtils.createNumber("0.0d"), instanceOf(Double.class));
         assertThat(NumberUtils.createNumber("3.14D"), instanceOf(Double.class));
     }
 }
