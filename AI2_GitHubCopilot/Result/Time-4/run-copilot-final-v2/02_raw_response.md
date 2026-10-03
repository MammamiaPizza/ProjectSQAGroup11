package org.joda.time;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;

import java.util.Arrays;

/**

 - Tests for {@link Partial#with(DateTimeFieldType, int)} focusing on the bug where
 - replacing an existing field type could create duplicates and increase size
 - instead of updating the value.
  */
 public class TestPartialWithBug88 extends TestCase {
  public static TestSuite suite() {
  return new TestSuite(TestPartialWithBug88.class);
  }
  public TestPartialWithBug88(String name) {
  super(name);
  }
  // -----------------------------------------------------------------------
  /** New field type should increase size by 1 and append in correct order.
  */
  public void testWith_NewFieldType_AddsField() {
  Partial partial = new Partial(DateTimeFieldType.year(), 2010);
  Partial result = partial.with(DateTimeFieldType.monthOfYear(), 6);
  assertEquals(2, result.size());
  assertEquals(2010, result.getValue(0));
  assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
  assertEquals(6, result.getValue(1));
  assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(1));
  }
  /** Replacing an existing field type must not increase size.
  */
  public void testWith_ExistingFieldType_ReplacesValue() {
  Partial partial = new Partial(
      new DateTimeFieldType[]{DateTimeFieldType.year(), DateTimeFieldType.monthOfYear()},
      new int[]{2010, 6}
  );
  Partial result = partial.with(DateTimeFieldType.monthOfYear(), 12);
  assertEquals("size must stay 2", 2, result.size());
  assertEquals(2010, result.getValue(0));
  assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
  assertEquals(12, result.getValue(1));
  assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(1));
  }
  /** Using the same value for an existing field type should not change size nor values.
  */
  public void testWith_ExistingFieldTypeSameValue_SizeUnchanged() {
  Partial partial = new Partial(DateTimeFieldType.monthOfYear(), 5);
  Partial result = partial.with(DateTimeFieldType.monthOfYear(), 5);
  assertEquals(1, result.size());
  assertEquals(5, result.getValue(0));
  assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(0));
  }
  /** Null field type must throw IllegalArgumentException.
  */
  public void testWith_NullFieldType_ThrowsException() {
  Partial partial = new Partial(DateTimeFieldType.year(), 2000);
  try {
      partial.with(null, 10);
      fail("Expected IllegalArgumentException for null fieldType");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  /** Invalid value for the field in the given chronology must throw (validation).
  */
  public void testWith_InvalidValue_ThrowsException() {
  Partial partial = new Partial(DateTimeFieldType.monthOfYear(), 5);
  try {
      // monthOfYear only 1..12 in ISO
      partial.with(DateTimeFieldType.monthOfYear(), 13);
      fail("Expected exception for value 13 (monthOfYear max 12)");
  } catch (IllegalArgumentException expected) {
      // expected
  }
  }
  /** Chronology must be retained after replacement.
  */
  public void testWith_ChronologyRetained() {
  Chronology chrono = ISOChronology.getInstanceUTC();
  Partial partial = new Partial(DateTimeFieldType.year(), 2010, chrono);
  Partial result = partial.with(DateTimeFieldType.year(), 2011);
  assertEquals(chrono, result.getChronology());
  }
  /** After replacement the field type must not appear twice.
  */
  public void testWith_NoDuplicateFieldTypes() {
  Partial partial = new Partial(
      new DateTimeFieldType[]{DateTimeFieldType.year(), DateTimeFieldType.monthOfYear()},
      new int[]{2010, 6}
  );
  Partial result = partial.with(DateTimeFieldType.monthOfYear(), 8);
  DateTimeFieldType[] types = result.getFieldTypes();
  assertEquals(2, types.length);
  // Check that monthOfYear appears exactly once
  int count = 0;
  for (DateTimeFieldType t : types) {
      if (DateTimeFieldType.monthOfYear().equals(t)) {
          count++;
      }
  }
  assertEquals(1, count);
  }
  /** Replacement must preserve the ordering of field types.
  */
  public void testWith_PreservesOrderOnReplacement() {
  Partial partial = new Partial(
      new DateTimeFieldType[]{DateTimeFieldType.year(), DateTimeFieldType.monthOfYear()},
      new int[]{2010, 6}
  );
  Partial result = partial.with(DateTimeFieldType.year(), 2005);
  assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
  assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(1));
  }
  /** Border values (min and max) should be accepted and stored correctly.
  */
  public void testWith_BoundaryValues_MinMax() {
  Partial partial = new Partial(DateTimeFieldType.monthOfYear(), 6);
  Partial minResult = partial.with(DateTimeFieldType.monthOfYear(), 1);
  assertEquals(1, minResult.getValue(0));
  Partial maxResult = partial.with(DateTimeFieldType.monthOfYear(), 12);
  assertEquals(12, maxResult.getValue(0));
  }
  /** Adding a smaller field type appends it at the end.
  */
  public void testWith_AddFieldAtEnd() {
  Partial partial = new Partial(DateTimeFieldType.year(), 2020);
  Partial result = partial.with(DateTimeFieldType.dayOfMonth(), 15);
  assertEquals(2, result.size());
  assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
  assertEquals(2020, result.getValue(0));
  assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(1));
  assertEquals(15, result.getValue(1));
  }
  /** Operation on an empty partial simply adds one field.
  */
  public void testWith_EmptyPartial() {
  Partial empty = new Partial();
  Partial result = empty.with(DateTimeFieldType.year(), 2025);
  assertEquals(1, result.size());
  assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
  assertEquals(2025, result.getValue(0));
  }
  /** Cross-chronology: replacing a field keeps the original chronology, not the default one.
  */
  public void testWith_ChronologyRetained_NonISO() {
  Chronology buddhist = BuddhistChronology.getInstanceUTC();
  Partial partial = new Partial(DateTimeFieldType.monthOfYear(), 5, buddhist);
  Partial result = partial.with(DateTimeFieldType.monthOfYear(), 6);
  assertEquals(buddhist, result.getChronology());
  assertEquals(6, result.getValue(0));
  }

}