package org.apache.commons.collections.functors;

 import org.apache.commons.collections.Predicate;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class EqualPredicateTest {

     @Test
     public void testSingleArgFactoryUsesEquals() {
         // Bug COLLECTIONS-239: DefaultEquator may use reference equality (==)
         // instead of Object.equals. This test fails on the buggy version.
         String s1 = new String("abc");
         String s2 = new String("abc");
         Predicate<String> pred = EqualPredicate.equalPredicate(s1);
         assertTrue("Evaluation must rely on Object.equals", pred.evaluate(s2));
     }

     @Test
     public void testSingleArgFactoryNotEquals() {
         Predicate<String> pred = EqualPredicate.equalPredicate("abc");
         assertFalse(pred.evaluate("xyz"));
     }

     @Test
     public void testSingleArgFactorySameReference() {
         String ref = "hello";
         Predicate<String> pred = EqualPredicate.equalPredicate(ref);
         assertTrue(pred.evaluate(ref));
     }

     @Test
     public void testEquatorDelegatesTrue() {
         Equator<String> alwaysTrue = new Equator<String>() {
             public boolean equate(String a, String b) { return true; }
         };
         Predicate<String> pred = EqualPredicate.equalPredicate("hello", alwaysTrue);
         assertTrue(pred.evaluate("world"));
     }

     @Test
     public void testEquatorDelegatesFalse() {
         Equator<String> alwaysFalse = new Equator<String>() {
             public boolean equate(String a, String b) { return false; }
         };
         Predicate<String> pred = EqualPredicate.equalPredicate("hello", alwaysFalse);
         assertFalse(pred.evaluate("hello"));
     }

     @Test
     public void testGetValueReturnsStoredObject() {
         String stored = "stored";
         Predicate<String> pred = EqualPredicate.equalPredicate(stored);
         EqualPredicate<String> eqPred = (EqualPredicate<String>) pred;
         assertSame(stored, eqPred.getValue());
     }

     @Test
     public void testNullObjectFactoryReturnsNullPredicate() {
         Predicate<Object> pred = EqualPredicate.equalPredicate(null);
         // NullPredicate returns true only for null input
         assertTrue(pred.evaluate(null));
         assertFalse(pred.evaluate("something"));
     }

     @Test
     public void testEvaluateNullInputWithNonNullStoredValue() {
         Predicate<String> pred = EqualPredicate.equalPredicate("hello");
         assertFalse(pred.evaluate(null));
     }

     @Test
     public void testEquatorHandlesNull() {
         Equator<Object> nullSafe = new Equator<Object>() {
             public boolean equate(Object a, Object b) {
                 if (a == null) return b == null;
                 return a.equals(b);
             }
         };
         // factory skips the equator when object is null, so use the constructor directly
         EqualPredicate<Object> pred = new EqualPredicate<Object>(null, nullSafe);
         assertTrue(pred.evaluate(null));
         assertNull(pred.getValue());
     }

     @Test
     public void testTwoArgFactoryReturnsPredicate() {
         Predicate<String> pred = EqualPredicate.equalPredicate("x", new Equator<String>() {
             public boolean equate(String a, String b) { return true; }
         });
         assertNotNull(pred);
     }

     @Test
     public void testEquatorOverridesEquals() {
         // Even when objects are not equal according to Object.equals,
         // the equator controls the result.
         final Equator<String> custom = new Equator<String>() {
             public boolean equate(String a, String b) {
                 return a != null && a.length() == b.length();
             }
         };
         Predicate<String> pred = EqualPredicate.equalPredicate("hi", custom);
         assertTrue(pred.evaluate("ok"));     // both length 2, equals false
         assertFalse(pred.evaluate("long"));  // length 4 vs 2
     }
 }