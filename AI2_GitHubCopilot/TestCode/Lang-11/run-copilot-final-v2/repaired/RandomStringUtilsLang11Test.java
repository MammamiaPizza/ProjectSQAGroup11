package org.apache.commons.lang3;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class RandomStringUtilsLang11Test {

     @Test
     public void testRandomCountNegativeThrowsIllegalArgumentException() {
         try {
             RandomStringUtils.random(-1, 0, 100, false, false);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue("Message must mention negative length", e.getMessage().contains("less than
0"));
         }
     }

     @Test
     public void testRandomCountZeroReturnsEmptyString() {
         assertEquals("", RandomStringUtils.random(0, 0, 100, false, false));
     }

     @Test
     public void testRandomStartEqualsEndThrowsIllegalArgumentExceptionWithStartInMessage() {
         try {
             RandomStringUtils.random(5, 10, 10, false, false);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue("Message must contain 'start'", e.getMessage().contains("start"));
         }
     }

     @Test
     public void testRandomStartGreaterThanEndThrowsIllegalArgumentExceptionWithStartInMessage() {
         try {
             RandomStringUtils.random(5, 20, 10, false, false);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue("Message must contain 'start'", e.getMessage().contains("start"));
         }
     }

     @Test
     public void
testRandomStartGreaterThanEndWithCharsThrowsIllegalArgumentExceptionWithStartInMessage() {
         try {
             RandomStringUtils.random(5, 20, 10, false, false, new char[] {'a','b'});
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue("Message must contain 'start'", e.getMessage().contains("start"));
         }
     }

     @Test
     public void testRandomEmptyCharsArrayThrowsIllegalArgumentException() {
         try {
             RandomStringUtils.random(3, 0, 0, false, false, new char[0]);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue("Message must mention empty chars array", e.getMessage().contains("chars
array must not be empty"));
         }
     }

     @Test
     public void testRandomValidProducesNonEmptyString() {
         String result = RandomStringUtils.random(10, 32, 127, false, false);
         assertNotNull(result);
         assertEquals(10, result.length());
     }

     @Test
     public void testRandomLettersOnlyProducesOnlyLetters() {
         String result = RandomStringUtils.random(20, 65, 91, true, false);
         for (char c : result.toCharArray()) {
             assertTrue("Character must be a letter: " + c, Character.isLetter(c));
         }
     }

     @Test
     public void testRandomWithCharsArrayProducesMatchingCharacters() {
         char[] allowed = {'X', 'Y', 'Z'};
         String result = RandomStringUtils.random(8, allowed);
         for (char c : result.toCharArray()) {
             boolean valid = false;
             for (char a : allowed) {
                 if (a == c) valid = true;
             }
             assertTrue("Character not in allowed set: " + c, valid);
         }
     }

     @Test
     public void testRandomNullCharsArrayWorksLikeNoStartEnd() {
         // This exercises the branch where start==0 && end==0 && chars==null
         String result = RandomStringUtils.random(5, 0, 0, false, false, (char[]) null);
         assertNotNull(result);
         assertEquals(5, result.length());
     }

 }
