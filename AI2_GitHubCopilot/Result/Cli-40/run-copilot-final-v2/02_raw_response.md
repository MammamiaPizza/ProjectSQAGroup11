package org.apache.commons.cli;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link TypeHandler} focused on bug CLI-282:
  * {@code createValue(String, Integer.class)} must throw {@link ParseException}
  * for invalid integer strings, but the buggy version returns null instead.
  */
 public class TypeHandlerTest {

     @Test(expected = ParseException.class)
     public void testCreateValueInteger_failure() throws ParseException {
         TypeHandler.createValue("not-a-number", Integer.class);
     }

     @Test(expected = ParseException.class)
     public void testCreateValueInteger_nullInput() throws ParseException {
         TypeHandler.createValue(null, Integer.class);
     }

     @Test(expected = ParseException.class)
     public void testCreateValueInteger_emptyString() throws ParseException {
         TypeHandler.createValue("", Integer.class);
     }

     @Test(expected = ParseException.class)
     public void testCreateValueInteger_decimalInput() throws ParseException {
         TypeHandler.createValue("1.5", Integer.class);
     }

     @Test(expected = ParseException.class)
     public void testCreateValueInteger_overflow() throws ParseException {
         TypeHandler.createValue("2147483648", Integer.class);
     }

     @Test(expected = ParseException.class)
     public void testCreateValueInteger_whitespaceInput() throws ParseException {
         TypeHandler.createValue(" 42 ", Integer.class);
     }

     @Test
     public void testCreateValueInteger_validPositive() throws ParseException {
         assertEquals(Integer.valueOf(100), TypeHandler.createValue("100", Integer.class));
     }

     @Test
     public void testCreateValueInteger_validNegative() throws ParseException {
         assertEquals(Integer.valueOf(-100), TypeHandler.createValue("-100", Integer.class));
     }

     @Test
     public void testCreateValueInteger_maxValue() throws ParseException {
         assertEquals(Integer.valueOf(Integer.MAX_VALUE),
                 TypeHandler.createValue("2147483647", Integer.class));
     }

     @Test
     public void testCreateValueInteger_minValue() throws ParseException {
         assertEquals(Integer.valueOf(Integer.MIN_VALUE),
                 TypeHandler.createValue("-2147483648", Integer.class));
     }

     @Test(expected = ParseException.class)
     public void testCreateValueObject_failure() throws ParseException {
         TypeHandler.createValue("not-a-number", (Object) Integer.class);
     }

     @Test
     public void testCreateValueObject_valid() throws ParseException {
         assertEquals(Integer.valueOf(42),
                 TypeHandler.createValue("42", (Object) Integer.class));
     }
 }