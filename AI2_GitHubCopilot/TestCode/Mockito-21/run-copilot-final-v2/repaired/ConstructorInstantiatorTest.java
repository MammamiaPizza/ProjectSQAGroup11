package org.mockito.internal.creation.instance;

import org.junit.Test;
import static org.junit.Assert.;
import static org.hamcrest.CoreMatchers.;

import java.lang.reflect.Constructor;

/**

 - Tests for {@link ConstructorInstantiator} focusing on non-static inner class instantiation bug.
  */
 public class ConstructorInstantiatorTest {
  // ---- test fixtures (outer / inner classes) ------------------------------------------------
  public static class Outer {
  public class Inner {
  }
  public static class StaticInner {
  }
  }
  public static class OtherOuter {
  public class Inner {
  }
  }
  public static class PlainClass {
  public PlainClass() {
  }
  }
  // ---- inner class with matching outer instance (the reported bug)
  // ----------------------------
  @Test
  public void shouldInstantiateInnerClassWithMatchingOuter() {
  Outer outer = new Outer();
  ConstructorInstantiator instantiator = new ConstructorInstantiator(outer);
  Outer.Inner inner = instantiator.newInstance(Outer.Inner.class);
  assertNotNull(inner);
  }
  // ---- null outer instance -------------------------------------------------------------------
  @Test(expected = InstantationException.class)
  public void shouldFailForInnerClassWithNullOuter() {
  ConstructorInstantiator instantiator = new ConstructorInstantiator(null);
  instantiator.newInstance(Outer.Inner.class);
  }
  // ---- mismatched outer instance ---------------------------------------------------------------
  @Test(expected = InstantationException.class)
  public void shouldFailForInnerClassWithMismatchedOuter() {
  OtherOuter other = new OtherOuter();
  ConstructorInstantiator instantiator = new ConstructorInstantiator(other);
  instantiator.newInstance(Outer.Inner.class);
  }
  // ---- static inner class --------------------------------------------------------------------
  @Test
  public void shouldInstantiateStaticInnerClassWithNullOuter() {
  ConstructorInstantiator instantiator = new ConstructorInstantiator(null);
  Outer.StaticInner instance = instantiator.newInstance(Outer.StaticInner.class);
  assertNotNull(instance);
  }
  @Test(expected = InstantationException.class)
  public void shouldFailForStaticInnerClassWithMatchingOuter() {
  Outer outer = new Outer();
  ConstructorInstantiator instantiator = new ConstructorInstantiator(outer);
  instantiator.newInstance(Outer.StaticInner.class);
  }
  // ---- non-inner (plain) class ----------------------------------------------------------------
  @Test
  public void shouldInstantiatePlainClassWithNullOuter() {
  ConstructorInstantiator instantiator = new ConstructorInstantiator(null);
  PlainClass instance = instantiator.newInstance(PlainClass.class);
  assertNotNull(instance);
  }
  @Test(expected = InstantationException.class)
  public void shouldFailForPlainClassWithNonnullOuter() {
  Outer outer = new Outer();
  ConstructorInstantiator instantiator = new ConstructorInstantiator(outer);
  instantiator.newInstance(PlainClass.class);
  }
  // ---- exception message content -------------------------------------------------------------
  @Test
  public void shouldReportParameterlessConstructorAdvice() {
  ConstructorInstantiator instantiator = new ConstructorInstantiator(null);
  try {
      instantiator.newInstance(Outer.Inner.class);
      fail("Expected InstantationException");
  } catch (InstantationException e) {
      assertThat(e.getMessage(), containsString("parameter-less constructor"));
  }
  }
  @Test
  public void shouldReportCorrectOuterTypeAdvice() {
  OtherOuter other = new OtherOuter();
  ConstructorInstantiator instantiator = new ConstructorInstantiator(other);
  try {
      instantiator.newInstance(Outer.Inner.class);
      fail("Expected InstantationException");
  } catch (InstantationException e) {
      assertThat(e.getMessage(), containsString("outer instance has correct type"));
  }
  }
  // ---- edge: instantiation path selection ----------------------------------------------------
  @Test
  public void shouldNotAttemptOuterResolutionWhenOuterIsNull() {
  // Verify that when outer is null we go straight to no-arg constructor.
  // The attempt to instantiate a plain class must succeed.
  ConstructorInstantiator instantiator = new ConstructorInstantiator(null);
  PlainClass instance = instantiator.newInstance(PlainClass.class);
  assertNotNull(instance);
  }
  @Test
  public void shouldPreserveOriginalExceptionAsCause() {
  ConstructorInstantiator instantiator = new ConstructorInstantiator(null);
  try {
      instantiator.newInstance(Outer.Inner.class);
      fail("Expected InstantationException");
  } catch (InstantationException e) {
      assertNotNull("Cause should not be null", e.getCause());
  }
  }

}
