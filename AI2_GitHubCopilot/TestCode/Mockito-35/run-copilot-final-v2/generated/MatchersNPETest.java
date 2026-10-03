package org.mockito;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;

 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 public class MatchersNPETest {

     @Before
     public void setUp() {
         MockitoAnnotations.initMocks(this);
         Mockito.validateMockitoUsage();
     }

     @After
     public void tearDown() {
         resetting();
     }

     private void resetting() {
         // no-op; matchers are stateless per call
     }

     // eq(int) should not throw NPE
     @Test
     public void testEqIntDoesNotThrowNPE() {
         int result;
         try {
             result = Matchers.eq(5);
         } catch (NullPointerException e) {
             fail("eq(int) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(0, result);
     }

     // eq(Integer) should not throw NPE when auto-boxing int
     @Test
     public void testEqIntegerDoesNotThrowNPE() {
         Integer result;
         try {
             result = Matchers.eq(Integer.valueOf(5));
         } catch (NullPointerException e) {
             fail("eq(Integer) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertNull("eq(Integer) should return null value", result);
     }

     // same(Integer) should not throw NPE
     @Test
     public void testSameIntegerDoesNotThrowNPE() {
         Integer input = Integer.valueOf(42);
         Integer result;
         try {
             result = Matchers.same(input);
         } catch (NullPointerException e) {
             fail("same(Integer) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertNull("same(Integer) should return null value", result);
     }

     // eq(boolean) does not throw NPE and returns false
     @Test
     public void testEqBooleanReturnsFalseAndNoNPE() {
         boolean result;
         try {
             result = Matchers.eq(true);
         } catch (NullPointerException e) {
             fail("eq(boolean) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(false, result);
     }

     // eq(byte) does not throw NPE and returns zero
     @Test
     public void testEqByteReturnsZeroAndNoNPE() {
         byte result;
         try {
             result = Matchers.eq((byte) 1);
         } catch (NullPointerException e) {
             fail("eq(byte) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(0, result);
     }

     // eq(char) does not throw NPE and returns a char
     @Test
     public void testEqCharReturnsZeroAndNoNPE() {
         char result;
         try {
             result = Matchers.eq('a');
         } catch (NullPointerException e) {
             fail("eq(char) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals('\u0000', result);
     }

     // eq(long) does not throw NPE and returns zero
     @Test
     public void testEqLongReturnsZeroAndNoNPE() {
         long result;
         try {
             result = Matchers.eq(1L);
         } catch (NullPointerException e) {
             fail("eq(long) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(0L, result);
     }

     // eq(short) does not throw NPE and returns zero
     @Test
     public void testEqShortReturnsZeroAndNoNPE() {
         short result;
         try {
             result = Matchers.eq((short) 1);
         } catch (NullPointerException e) {
             fail("eq(short) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(0, result);
     }

     // eq(Float) does not throw NPE
     @Test
     public void testEqFloatReturnsZeroAndNoNPE() {
         float result;
         try {
             result = Matchers.eq(1.0f);
         } catch (NullPointerException e) {
             fail("eq(float) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(0.0f, result, 0.0f);
     }

     // eq(Double) does not throw NPE
     @Test
     public void testEqDoubleReturnsZeroAndNoNPE() {
         double result;
         try {
             result = Matchers.eq(1.0d);
         } catch (NullPointerException e) {
             fail("eq(double) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertEquals(0.0d, result, 0.0d);
     }

     // eq with String object should not throw NPE
     @Test
     public void testEqStringDoesNotThrowNPE() {
         String result;
         try {
             result = Matchers.eq("hello");
         } catch (NullPointerException e) {
             fail("eq(String) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertNull("eq(String) should return null", result);
     }

     // same with String should not throw NPE
     @Test
     public void testSameStringDoesNotThrowNPE() {
         String input = "world";
         String result;
         try {
             result = Matchers.same(input);
         } catch (NullPointerException e) {
             fail("same(String) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertNull("same(String) should return null", result);
     }
 }
