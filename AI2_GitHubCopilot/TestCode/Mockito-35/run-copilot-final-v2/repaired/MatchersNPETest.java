package org.mockito;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.util.List;

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
         List mock = Mockito.mock(List.class);
         try {
             Mockito.when(mock.add(Matchers.eq(5))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("eq(int) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add(5));
     }

     // eq(Integer) should not throw NPE when auto-boxing int
     @Test
     public void testEqIntegerDoesNotThrowNPE() {
         List mock = Mockito.mock(List.class);
         try {
             Mockito.when(mock.add(Matchers.eq(Integer.valueOf(5)))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("eq(Integer) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add(5));
     }

     // same(Integer) should not throw NPE
     @Test
     public void testSameIntegerDoesNotThrowNPE() {
         List mock = Mockito.mock(List.class);
         Integer input = Integer.valueOf(42);
         try {
             Mockito.when(mock.add(Matchers.same(input))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("same(Integer) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add(input));
     }

     // eq(boolean) does not throw NPE and returns false
     @Test
     public void testEqBooleanReturnsFalseAndNoNPE() {
         List mock = Mockito.mock(List.class);
         try {
             Mockito.when(mock.add(Matchers.eq(true))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("eq(boolean) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add(true));
     }

     // eq(byte) does not throw NPE and returns zero
     @Test
     public void testEqByteReturnsZeroAndNoNPE() {
         List mock = Mockito.mock(List.class);
         try {
             Mockito.when(mock.add(Matchers.eq((byte) 1))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("eq(byte) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add((byte) 1));
     }

     // eq(char) does not throw NPE and returns a char
     @Test
     public void testEqCharReturnsZeroAndNoNPE() {
         List mock = Mockito.mock(List.class);
         try {
             Mockito.when(mock.add(Matchers.eq('a'))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("eq(char) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add('a'));
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
         List mock = Mockito.mock(List.class);
         try {
             Mockito.when(mock.add(Matchers.eq(1.0f))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("eq(float) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add(1.0f));
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
         List mock = Mockito.mock(List.class);
         String input = "world";
         try {
             Mockito.when(mock.add(Matchers.same(input))).thenReturn(true);
         } catch (NullPointerException e) {
             fail("same(String) threw NullPointerException: " + e.getMessage());
             return;
         }
         assertTrue(mock.add(input));
     }
 }
