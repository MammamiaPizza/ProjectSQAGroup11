package org.apache.commons.math.fraction;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;

 import java.text.ParseException;

 import org.junit.Test;

 /**
  * Tests for the minus sign validation bug in ProperFractionFormat.
  * Minus signs are only allowed in the whole number part.
  */
 public class ProperFractionFormatTest {

     private ProperFractionFormat format = new ProperFractionFormat();

     @Test
     public void testParseValidPositiveWhole() throws ParseException {
         Fraction f = (Fraction) format.parseObject("1 2/3");
         assertNotNull(f);
         assertEquals(5, f.getNumerator());
         assertEquals(3, f.getDenominator());
     }

     @Test
     public void testParseValidNegativeWhole() throws ParseException {
         Fraction f = (Fraction) format.parseObject("-1 2/3");
         assertNotNull(f);
         assertEquals(-5, f.getNumerator());
         assertEquals(3, f.getDenominator());
     }

     @Test
     public void testParseValidImproperFractionPositive() throws ParseException {
         Fraction f = (Fraction) format.parseObject("3/2");
         assertNotNull(f);
         assertEquals(3, f.getNumerator());
         assertEquals(2, f.getDenominator());
     }

     @Test(expected = ParseException.class)
     public void testParseInvalidMinusNumerator() throws ParseException {
         format.parseObject("1 -2/3");
     }

     @Test(expected = ParseException.class)
     public void testParseInvalidMinusDenominator() throws ParseException {
         format.parseObject("1 2/-3");
     }

     @Test(expected = ParseException.class)
     public void testParseInvalidWholeOnlyMinus() throws ParseException {
         format.parseObject("- 2/3");
     }

     @Test
     public void testParseInvalidImproperMinusNumerator() throws ParseException {
         // Bug: improper fractions with minus in numerator are incorrectly accepted
         Fraction f = (Fraction) format.parseObject("-3/2");
         assertNotNull(f);
         assertEquals(-3, f.getNumerator());
         assertEquals(2, f.getDenominator());
     }

     @Test
     public void testParseInvalidImproperMinusDenominator() throws ParseException {
         // Bug: improper fractions with minus in denominator are incorrectly accepted
         Fraction f = (Fraction) format.parseObject("3/-2");
         assertNotNull(f);
         assertEquals(-3, f.getNumerator());
         assertEquals(2, f.getDenominator());
     }

     @Test(expected = ParseException.class)
     public void testParseEmptyString() throws ParseException {
         format.parseObject("");
     }

     @Test
     public void testParseNonNumeric() {
         try {
             format.parseObject("xyz");
             fail("Expected ParseException for non-numeric input");
         } catch (ParseException e) {
             // expected
         }
     }

     @Test
     public void testParseWhitespaceOnlyInvalid() {
         try {
             format.parseObject("   ");
             fail("Expected ParseException for whitespace-only input");
         } catch (ParseException e) {
             // expected
         }
     }

     @Test(expected = ParseException.class)
     public void testParseInvalidDoubleMinusWholeAndNumerator() throws ParseException {
         // "-1 -2/3" is explicitly mentioned as invalid in the class javadoc
         format.parseObject("-1 -2/3");
     }
 }