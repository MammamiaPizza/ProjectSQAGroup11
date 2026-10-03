package org.mockito.internal.verification.argumentmatching;

 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;

 import org.hamcrest.Description;
 import org.hamcrest.Matcher;
 import org.hamcrest.StringDescription;
 import org.junit.Test;

 import org.mockito.internal.matchers.ContainsExtraTypeInformation;

 import static org.junit.Assert.*;

 public class ArgumentMatchingToolTest {

     private final ArgumentMatchingTool tool = new ArgumentMatchingTool();

     /* Custom ContainsExtraTypeInformation implementation for testing */
     static class TestExtraTypeMatcher implements Matcher<Object>, ContainsExtraTypeInformation {
         private final boolean matches;
         private final String toString;
         private final boolean typeMatches;

         TestExtraTypeMatcher(boolean matches, String toString, boolean typeMatches) {
             this.matches = matches;
             this.toString = toString;
             this.typeMatches = typeMatches;
         }

         public boolean matches(Object arg) {
             return matches;
         }

         public void describeTo(Description description) {
             description.appendText(toString);
         }

         @Override
         public String toString() {
             return toString;
         }

         public boolean typeMatches(Object object) {
             return typeMatches;
         }
     }

     /* Custom simple Matcher that is NOT ContainsExtraTypeInformation */
     static class TestSimpleMatcher implements Matcher<Object> {
         private final boolean matches;

         TestSimpleMatcher(boolean matches) {
             this.matches = matches;
         }

         public boolean matches(Object arg) {
             return matches;
         }

         public void describeTo(Description description) {
             description.appendText("simple");
         }
     }

     @Test
     public void emptyListsShouldReturnEmptyArray() {
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Collections.<Matcher>emptyList(), new Object[0]);
         assertNotNull(result);
         assertEquals(0, result.length);
     }

     @Test
     public void differentSizesShouldReturnEmptyArray() {
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(new TestSimpleMatcher(true)), new Object[] {"a", "b"});
         assertNotNull(result);
         assertEquals(0, result.length);
     }

     @Test
     public void nullArgumentWithNullMatchingMatcherShouldProduceNoSuspicious() {
         // matcher that matches null -> safelyMatches returns true -> skipped
         Matcher m = new TestExtraTypeMatcher(true, "null-equivalent", false);
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(m), new Object[] {null});
         assertEquals(0, result.length);
     }

     @Test
     public void nullArgumentWithNonMatchingNonExtraTypeMatcherShouldBeIgnored() {
         // matcher is not ContainsExtraTypeInformation, so even if matches returns false it is
ignored
         Matcher m = new TestSimpleMatcher(false);
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(m), new Object[] {null});
         assertEquals(0, result.length);
     }

     @Test
     public void nullArgumentWithNonMatchingExtraTypeAndTypeMismatchShouldResturnIndex() {
         // m.matches(null) returns false, m instanceof ContainsExtraTypeInformation == true,
         // toStringEquals(m, null) should not throw NPE (bug fix),
         // typeMatches returns false -> suspicious index 0
         Matcher m = new TestExtraTypeMatcher(false, "null-description", false);
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(m), new Object[] {null});
         // If the bug is fixed, no NPE and result contains index 0 because toStringEquals
         // will compare description with null.toString()? The fixed version should return false
         // when arg is null, so suspicious index 0.
         assertArrayEquals(new Integer[]{0}, result);
     }

     @Test
     public void nullArgumentWithNonMatchingExtraTypeButTypeMatchesShouldNotBeSuspicious() {
         Matcher m = new TestExtraTypeMatcher(false, "desc", true);
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(m), new Object[] {null});
         assertEquals(0, result.length);
     }

     @Test
     public void matchingExtraTypeArgumentShouldNeverBeSuspicious() {
         Matcher m = new TestExtraTypeMatcher(true, "anything", false);
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(m), new Object[] {"test"});
         assertEquals(0, result.length);
     }

     @Test
     public void nonMatchingExtraTypeWithDifferentToStringShouldNotBeSuspicious() {
         Matcher m = new TestExtraTypeMatcher(false, "desc", false) {
             @Override
             public String toString() {
                 return "desc-different";
             }
         };
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(m), new Object[] {"value"});
         // arg.toString() = "value", matcher description = "desc-different" => not equal
         assertEquals(0, result.length);
     }

     @Test
     public void mixtureOfNullAndNonNullArgsShouldReturnCorrectIndices() {
         Matcher nullM = new TestExtraTypeMatcher(false, "null-desc", false);
         Matcher matchM = new TestExtraTypeMatcher(true, "match", false);
         Matcher mismatchM = new TestExtraTypeMatcher(false, "mismatch", false) {
             @Override
             public String toString() {
                 return "mismatch";
             }
         };
         // arguments: null (suspicious index 0 if no NPE), "hello" (matches), "mismatch"
(suspicious? toStringEquals true, typeMatches false -> index 2)
         // note: we rely on arg.toString() for "hello" and "mismatch"
         List<Matcher> matchers = Arrays.asList(nullM, matchM, mismatchM);
         Object[] args = new Object[]{null, "hello", "mismatch"};
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(matchers, args);
         // Expected: indices 0 and 2
         assertArrayEquals(new Integer[]{0, 2}, result);
     }

     @Test
     public void safelyMatchesShouldSuppressException() {
         Matcher explodingMatcher = new TestExtraTypeMatcher(false, "desc", false) {
             @Override
             public boolean matches(Object arg) {
                 throw new RuntimeException("boom");
             }
         };
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(
                 Arrays.asList(explodingMatcher), new Object[] {"arg"});
         // safelyMatches returns false, then toStringEquals compares "desc" with "arg" -> false, no
suspicious
         assertEquals(0, result.length);
     }

     @Test
     public void allArgumentsNullWithVariousMatchersShouldNotThrowNPE() {
         Matcher nullMatchingMatcher = new TestExtraTypeMatcher(true, "null-matcher", false);
         Matcher nonMatchingMatcher = new TestExtraTypeMatcher(false, "desc", false);
         Object[] args = new Object[]{null, null};
         List<Matcher> matchers = Arrays.asList(nullMatchingMatcher, nonMatchingMatcher);
         // The second arg should trigger TypeError only if toStringEquals NPE. Fix will prevent.
         Integer[] result = tool.getSuspiciouslyNotMatchingArgsIndexes(matchers, args);
         assertArrayEquals(new Integer[]{1}, result);
     }
 }
