package org.mockito.internal.matchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hamcrest.StringDescription;
import org.junit.Test;

public class SameTest {

 @Test
 public void matchesNullWantedAndNullActual() {
     assertTrue(new Same(null).matches(null));
 }

 @Test
 public void matchesNullWantedAndNonNullActualIsFalseWithoutThrowing() {
     assertFalse(new Same(null).matches("some actual value"));
 }

 @Test
 public void matchesIdenticalReference() {
     Object wanted = new Object();
     assertTrue(new Same(wanted).matches(wanted));
 }

 @Test
 public void matchesEqualButDistinctStringsAsFalse() {
     String wanted = new String("foo");
     String actual = new String("foo");
     assertFalse(new Same(wanted).matches(actual));
 }

 @Test
 public void matchesDistinctNonNullValuesAsFalse() {
     assertFalse(new Same(Integer.valueOf(1)).matches(Integer.valueOf(2)));
 }

 @Test
 public void describeToWithNullWantedDoesNotThrow() {
     StringDescription description = new StringDescription();

     new Same(null).describeTo(description);

     assertEquals("same(null)", description.toString());
 }

 @Test
 public void describeToQuotesStringWanted() {
     StringDescription description = new StringDescription();

     new Same("foo").describeTo(description);

     assertEquals("same(\"foo\")", description.toString());
 }

 @Test
 public void describeToQuotesCharacterWanted() {
     StringDescription description = new StringDescription();

     new Same(Character.valueOf('c')).describeTo(description);

     assertEquals("same('c')", description.toString());
 }

 @Test
 public void describeToRendersNonStringWantedUnquoted() {
     StringDescription description = new StringDescription();

     new Same(Integer.valueOf(42)).describeTo(description);

     assertEquals("same(42)", description.toString());
 }

}
