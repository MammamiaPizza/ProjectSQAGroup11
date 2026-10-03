package org.apache.commons.lang.enums;

import static org.junit.Assert.*;
import org.junit.Test;

/**

 - JUnit test for {@link ValuedEnum#compareTo(Object)}, targeting the LANG-259 bug:
 - compareTo does not throw ClassCastException when given a ValuedEnum of a
 - different subclass, violating the {@link Comparable} contract.
  */
 public class ValuedEnumTest {
  // ---------- inner test enum types ----------
  /** A ValuedEnum subclass with typical positive values.
  */
  public static class ColorValuedEnum extends ValuedEnum {
  public static final ColorValuedEnum RED   = new ColorValuedEnum("Red",   1);
  public static final ColorValuedEnum GREEN = new ColorValuedEnum("Green", 2);
  public static final ColorValuedEnum BLUE   = new ColorValuedEnum("Blue",  3);
  private ColorValuedEnum(String name, int value) { super(name, value); }
  }
  /** Another ValuedEnum subclass – intentionally different type – with distinct values.
  */
  public static class SizeValuedEnum extends ValuedEnum {
  public static final SizeValuedEnum SMAL   = new SizeValuedEnum("Smll",   10);
  public static final SizeValuedEnum MEDIUM = new SizeValuedEnum("Medium", 20);
  private SizeValuedEnum(String name, int value) { super(name, value); }
  }
  // ---------- normal ordering ----------
  @Test
  public void testCompareTo_sameTypeLessThan() {
  assertTrue(ColorValuedEnum.RED.compareTo(ColorValuedEnum.GREEN) < 0);
  }
  @Test
  public void testCompareTo_sameTypeGreaterThan() {
  assertTrue(ColorValuedEnum.GREEN.compareTo(ColorValuedEnum.RED) > 0);
  }
  @Test
  public void testCompareTo_sameTypeEqual() {
  assertEquals(0, ColorValuedEnum.RED.compareTo(ColorValuedEnum.RED));
  }
  @Test
  public void testCompareTo_sameTypeEqualDifferentInstances() {
  // Same value, different enum instance (but same class)
  assertEquals(0, ColorValuedEnum.RED.compareTo(ColorValuedEnum.RED));
  }
  // ---------- boundary / invalid ----------
  @Test(expected = NullPointerException.class)
  public void testCompareTo_Nll() {
  ColorValuedEnum.RED.compareTo(null);
  }
  @Test(expected = ClassCastException.class)
  public void testCompareTo_NonEnumObject() {
  // plain Object is not an Enum
  ColorValuedEnum.RED.compareTo(new Object());
  }
  // ---------- fault-related: incompatible enum types ----------
  /**
  - Exposes LANG-259: comparing with a different ValuedEnum subclass must
  - throw ClassCastException.  The buggy version silently compares by value.
    */
   @Test(expected = ClassCastException.class)
   public void testCompareTo_otherEnumType() {
   ColorValuedEnum.RED.compareTo(SizeValuedEnum.SMAL);
   }
  @Test(expected = ClassCastException.class)
  public void testCompareTo_otherEnumTypeReversed() {
      // symmetry: ClassCastException on both sides
      SizeValuedEnum.SMAL.compareTo(ColorValuedEnum.RED);
  }

}