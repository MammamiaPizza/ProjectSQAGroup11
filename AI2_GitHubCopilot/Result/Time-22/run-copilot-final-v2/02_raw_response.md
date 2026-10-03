import static org.junit.Assert.*;

 import org.joda.time.*;
 import org.joda.time.base.BasePeriod;
 import org.joda.time.chrono.ISOChronology;
 import org.junit.Test;

 /**
  * Tests for {@link BasePeriod} that target bug #22 (fixed zone adding offset to period fields).
  * The buggy version incorrectly includes the zone offset when computing period values from a
duration.
  */
 public class TestBasePeriodBug22 {

     // Concrete subclass to expose protected constructors for testing
     private static class TestablePeriod extends BasePeriod {
         public TestablePeriod(long duration, PeriodType type, Chronology chrono) {
             super(duration, type, chrono);
         }

         public TestablePeriod(long duration) {
             super(duration);
         }

         public TestablePeriod(long startInstant, long endInstant, PeriodType type, Chronology
chrono) {
             super(startInstant, endInstant, type, chrono);
         }
     }

     // --------------------------------------------------------------------------
     // Tests for the buggy constructors with duration and fixed zone
     // --------------------------------------------------------------------------

     @Test
     public void testDurationZeroWithFixedZone_allFieldsZero() {
         // Given a zero duration and UTC zone, period fields should be zero
         Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
         TestablePeriod period = new TestablePeriod(0L, PeriodType.standard(), chrono);
         assertEquals("years",   0, period.getValue(0));
         assertEquals("months",  0, period.getValue(1));
         assertEquals("weeks",   0, period.getValue(2));
         assertEquals("days",     0, period.getValue(3));
         assertEquals("hours",    0, period.getValue(4));
         assertEquals("minutes",  0, period.getValue(5)));
         assertEquals("seconds",  0, period.getValue(6)));
         assertEquals("millis",   0, period.getValue(7));
     }

     @Test
     public void testDurationZeroWithFixedOffsetZone_allFieldsZero() {
         // Fixed offset zone (e.g., UTC+5:00) should not add offset to period fields
         DateTimeZone zone = DateTimeZone.forOffsetHours(5);
         Chronology chrono = ISOChronology.getInstance(zone);
         TestablePeriod period = new TestablePeriod(0L, PeriodType.standard(), chrono);
         assertEquals("hours",  0, period.getValue(4));
         assertEquals("minutes",0, period.getValue(5));
         // Quick sanity for all fields
         for (int i = 0; i < period.size(); i++) {
             assertEquals("field " + period.getFieldType(i), 0, period.getValue(i));
         }
     }

     @Test
     public void testDurationMillisOnlyWithFixedZone_correctFields() {
         // 1500 millis = 1 second 500 millis, no larger units
         Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
         TestablePeriod period = new TestablePeriod(1500L, PeriodType.standard(), chrono);
         assertEquals("years",  0, period.getValue(0));
         assertEquals("months", 0, period.getValue(1));
         assertEquals("weeks",  0, period.getValue(2));
         assertEquals("days",    0, period.getValue(3));
         assertEquals("hours",   0, period.getValue(4));
         assertEquals("minutes",0, period.getValue(5));
         assertEquals("seconds",1, period.getValue(6));
         assertEquals("millis", 500, period.getValue(7));
     }

     @Test
     public void testDurationHoursOnlyWithFixedZone_oOffset() {
         // 2 hours duration, fixed zone should give hours=2 and no extra offset
         long twoHours = 2L * DateTimeConstants.MILLIS_PER_HOUR;
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(3, 30); // UTC+3:0 offset
         Chronology chrono = ISOChronology.getInstance(zone);
         TestablePeriod period = new TestablePeriod(twoHours, PeriodType.standard(), chrono);
         // The bug would add the 3 hours offset to the hours field (resulting in 5)
         assertEquals("hours",   2, period.getValue(4));
         assertEquals("minutes", 0, period.getValue(5));
         assertEquals("seconds", 0, period.getValue(6));
         assertEquals("millis", 0, period.getValue(7));
     }

     @Test
     public void testDurationDaysOnlyWithFixedZone_oOffset() {
         // 1 day in millis, no offset influence
         long oneDay = DateTimeConstants.MILLIS_PER_AY;
         DateTimeZone zone = DateTimeZone.forOffsetHours(-2); // UTC-2
         Chronology chrono = ISOChronology.getInstance(zone);
         TestablePeriod period = new TestablePeriod(oneDay, PeriodType.standard(), chrono);
         assertEquals("days",   1, period.getValue(3));
         assertEquals("hours",   0, period.getValue(4));
         assertEquals("minutes", 0, period.getValue(5));
         assertEquals("seconds", 0, period.getValue(6));
         assertEquals("millis",  0, period.getValue(7));
     }

     @Test
     public void testDurationLargeMillisWithFixedZone_safe() {
         // Use a large but safe duration (100 days)
         long large = 100L * DateTimeConstants.MILLIS_PER_AY;
         Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
         TestablePeriod period = new TestablePeriod(large, PeriodType.standard(), chrono);
         // Should only populate days, not affect other fields
         assertTrue("days should be >0", period.getValue(3) >0);
         assertEquals("hours", 0, period.getValue(4));
     }

     // --------------------------------------------------------------------------
     // Tests for the duration-only constructor (which delegates to (duration, null, null))
     // --------------------------------------------------------------------------

     @Test
     public void testDurationOnlyConstructor_respectCurrentDefaultZone() {
         // The parameterless constructor uses default zone. Set default to UTC.
         DateTimeZone defaultSave = DateTimeZone.getDefault();
         try {
             DateTimeZone.setDefault(DateTimeZone.UTC);
             TestablePeriod period = new TestablePeriod(0L);
             // All fields should be zero regardless of default zone bug
             for (int i =0; i < period.size(); i++) {
                 assertEquals(0, period.getValue(i));
             }
         } finally {
             DateTimeZone.setDefault(defaultSave);
         }
     }

     @Test
     public void testDurationOnlyConstructor_ithNonZeroDuration() {
         DateTimeZone defaultSave = DateTimeZone.getDefault();
         try {
             DateTimeZone.setDefault(DateTimeZone.UTC);
             // 1 minute
             TestablePeriod period = new TestablePeriod(60000L);
             assertEquals("hours",   0, period.getValue(4));
             assertEquals("minutes", 1, period.getValue(5));
             assertEquals("seconds", 0, period.getValue(6));
         } finally {
             DateTimeZone.setDefault(defaultSave);
         }
     }

     // --------------------------------------------------------------------------
     // Tests for two-instant constructor with fixed zone
     // --------------------------------------------------------------------------

     @Test
     public void testTwoInstantsFixedZone_oOffsetInPeriod() {
         // Start and end instants 2 hours apart in fixed zone, period must be 2 hours
         DateTimeZone zone = DateTimeZone.forID("+05:30");
         Chronology chrono = ISOChronology.getInstance(zone);
         long start = new DateTime(2010, 1, 1, 0, 0, chrono).getMillis();
         long end = new DateTime(2010, 1, 1, 2, 0, chrono).getMillis();
         TestablePeriod period = new TestablePeriod(start, end, PeriodType.standard(), chrono);
         assertEquals("hours",  2, period.getValue(4));
         assertEquals("minutes",0, period.getValue(5));
         assertEquals("seconds",0, period.getValue(6));
     }

     // --------------------------------------------------------------------------
     // Edge cases and other methods
     // --------------------------------------------------------------------------

     @Test
     public void testSetPeriodInternal_correctMerging() {
         TestablePeriod period = new TestablePeriod(0L, PeriodType.standard(),
ISOChronology.getInstanceUTC());
         // Build a MutablePeriod to merge
         MutablePeriod mp = new MutablePeriod();
         mp.setHours(1);
         mp.setMinutes(30);
         period.setPeriod(mp); // this calls setPeriodInternal indirectly? Actually
setPeriod(ReadablePeriod)
         assertEquals(1, period.getHours());
         assertEquals(30, period.getMinutes());
         // Field not present must be zero
         assertEquals(0, period.getSeconds());
     }

     @Test
     public void testCheckAndUpdate_unsupportedFieldThro() {
         // Using a type that supports only days, attempt to set hours should throw
         TestablePeriod period = new TestablePeriod(0L, PeriodType.days(),
ISOChronology.getInstanceUTC());
         try {
             period.addField(DurationFieldType.hours(), 1);
             fail("Should throw for unsupported field");
         } catch (IllegalArgumentException ex) {
             // expected
         }
     }

     @Test
     public void testConstructorWithType_estrictedFields() {
         // dayTime type includes days, hours, minutes, seconds, millis; no years/months
         Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
         TestablePeriod period = new TestablePeriod(3600_000L, PeriodType.dayTime(), chrono);
         // 1 hour
         assertEquals("size", 5, period.size());
         // The dayTime type ordering: days, hours, minutes, seconds, millis
         assertEquals("hours",   1, period.getValue(1)); // assuming index 1 is hours
         // Verify no years/months field
         boolean hasYears = false;
         for (int i =0; i < period.size(); i++) {
             if (period.getFieldType(i).equals(DurationFieldType.years())) hasYears = true;
         }
         assertFalse("Should not contain years", hasYears);
     }
 }

I need to ensure the code compiles. Note: setPeriod is a protected method; in TestablePeriod, we can
call period.setPeriod(mp) only if TestablePeriod is the same class? Actually setPeriod is
protected, so it can be called from within the subclass TestablePeriod. Since the test class is not
a subclass of TestablePeriod, calling period.setPeriod(mp) from within the test class will compile
because TestablePeriod inherits setPeriod from BasePeriod and since we are in the same package?
Actually TestBasePeriodBug22 is in a different class and not a subclass of TestablePeriod or
BasePeriod, but setPeriod is protected, meaning accessible from subclasses and from the same
package. TestBasePeriodBug22 is not in the same package as org.joda.time.base unless we declare it.
So calling period.setPeriod(mp) from the test class would cause a compile error because it's
protected and the caller is not a subclass nor in the same package. We can either make the test
class in the same package (package org.joda.time.base;) or wrap the call in a public method of
TestablePeriod. Since we want to be compilable standalone, safer to put the test class in
org.joda.time.base package to have access to protected members. Or we can avoid calling protected
methods directly, and instead test via public methods like constructors. The setPeriod method is
used to test setPeriodInternal but we can test that indirectly by constructing a TestablePeriod with
parameters and reading values. However, the test plan wants to test checkAndUpdate and
setPeriodInternal. We can test checkAndUpdate via addField which is protected too. Hmm, both
addField and setPeriod are protected, so accessing them from outside requires package access.

We could make TestablePeriod expose public wrappers for those methods. That would be cleaner and not
rely on package. So I'll modify: TestablePeriod will have public methods pubSetPeriod, pubAddField,
etc. Then we call those. Or we can use MutablePeriod which extends BasePeriod and has public
methods like add(DurationFieldType, int), setPeriod(ReadablePeriod), etc. Actually MutablePeriod is
a concrete class with public methods that call the protected ones. So we can use MutablePeriod
directly for testing checkAndUpdate and setPeriodInternal. That's simpler and avoids the need for a
custom subclass at all? But we need to test the buggy constructors, which are also called by
MutablePeriod's constructors. Yes, MutablePeriod(long duration) calls super(duration) which is
BasePeriod(long duration), that is buggy. So we can test the constructors using MutablePeriod.
That's even better because MutablePeriod is public API. But we must be careful: we need to test
BasePeriod directly, but using MutablePeriod is valid because it inherits the buggy constructors.
However, the test should be on BasePeriod, but we can use MutablePeriod as a concrete instance to
trigger the bug. The bug report's trigger tests are using Duration.toPeriod() (which uses
MutablePeriod?) and TestPeriod_Constructors uses Period constructors. Period also extends
BasePeriod. So using MutablePeriod or Period is fine. But the prompt says "Target:
org.joda.time.base.BasePeriod". So tests should test that class. But using a subclass is acceptable.
Since MutablePeriod is public, we can instantiate it. However, we cannot assume the behavior of
MutablePeriod beyond what BasePeriod provides; but that's the same as any concrete class.

Given the constraint to not invent APIs/behavior, using MutablePeriod is safe because it's part of
the Joda-Time library. The buggy code is in BasePeriod, and any concrete subclass will exhibit the
bug. So I'll rewrite the tests using MutablePeriod for simplicity.

But we still need to test the constructor that takes PeriodType and Chronology. MutablePeriod has
constructors MutablePeriod(long duration, PeriodType type, Chronology chrono) and MutablePeriod(long
duration), etc. So we can use those.

Therefore, I'll define tests without custom subclass, using MutablePeriod. That will be easier and
avoid package access issues. I'll need to import MutablePeriod. But ensure we don't exceed 12 tests.

I'll design:

 1. testZeroDurationFixedUTC - assert all zero
 2. testZeroDurationFixedOffset - zero with UTC+5
 3. testMillisOnlyFixedUTC - 1500 ms
 4. testHoursOnlyFixedOffset - 2 hours with zone +3:30
 5. testDaysOnlyFixedUTC - 1 day
 6. testLargeDuration - 100 days
 7. testDurationOnlyConstructorCurrentZone - set default to UTC, zero duration
 8. testTwoInstantConstructorFixedZone - start-end in UTC+5:30
 9. testSetPeriod_mergeValues - use MutablePeriod.setPeriod(ReadablePeriod)
 10. testAddField_unsupportedFieldThrows - MutablePeriod.add(DurationFieldType, int) for unsupported
type
 11. testConstructorWithType_restrictedFields - dayTime type
 12. testCheckAndUpdate_setFieldInto - use MutablePeriod.setField(DurationFieldType, int) which is
public? Actually MutablePeriod has public add(DurationFieldType, int) and set(DurationFieldType,
int). So we can test set which calls setField -> setFieldInto -> checkAndUpdate.

So I'll adjust.

I'll write the final class now.```java
import static org.junit.Assert.*;

import org.joda.time.*;
import org.joda.time.chrono.ISOChronology;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**

 - Tests for {@link org.joda.time.base.BasePeriod} focusing on bug #22:
 - fixed zone causing incorrect offset in period fields.
  */
 public class TestBasePeriodBug22 {
  private DateTimeZone originalDefaultZone;
  @Before
  public void saveDefaultZone() {
  originalDefaultZone = DateTimeZone.getDefault();
  }
  @After
  public void restoreDefaultZone() {
  DateTimeZone.setDefault(originalDefaultZone);
  }
  // --------------------------------------------------------------------
  // Constructors with duration + fixed zone
  // --------------------------------------------------------------------
  @Test
  public void testDurationZeroWithFixedUTC_allFieldsZero() {
  Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
  MutablePeriod period = new MutablePeriod(0L, PeriodType.standard(), chrono);
  assertEquals("years",   0, period.getYears());
  assertEquals("months",  0, period.getMonths());
  assertEquals("weeks",   0, period.getWeeks());
  assertEquals("days",    0, period.getDays());
  assertEquals("hours",   0, period.getHours());
  assertEquals("minutes", 0, period.getMinutes());
  assertEquals("seconds", 0, period.getSeconds());
  assertEquals("millis",  0, period.getMillis());
  }
  @Test
  public void testDurationZeroWithFixedOffsetZone_allFieldsZero() {
  DateTimeZone zone = DateTimeZone.forOffsetHours(5);
  Chronology chrono = ISOChronology.getInstance(zone);
  MutablePeriod period = new MutablePeriod(0L, PeriodType.standard(), chrono);
  for (int i = 0; i < period.size(); i++) {
      assertEquals("field " + period.getFieldType(i), 0, period.getValue(i));
  }
  }
  @Test
  public void testMillisOnlyWithFixedUTC_correctFields() {
  Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
  MutablePeriod period = new MutablePeriod(1500L, PeriodType.standard(), chrono);
  assertEquals(0, period.getHours());
  assertEquals(0, period.getMinutes());
  assertEquals(1, period.getSeconds());
  assertEquals(500, period.getMillis());
  }
  @Test
  public void testHoursOnlyWithFixedOffset_noOffsetAdded() {
  long twoHours = 2L
  * DateTimeConstants.MILLIS_PER_HOUR;
  DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(3, 30);
  Chronology chrono = ISOChronology.getInstance(zone);
  MutablePeriod period = new MutablePeriod(twoHours, PeriodType.standard(), chrono);
  assertEquals("hours", 2, period.getHours());
  assertEquals("minutes", 0, period.getMinutes());
  assertEquals("seconds", 0, period.getSeconds());
  assertEquals("millis", 0, period.getMillis());
  }
  @Test
  public void testDaysOnlyWithFixedZone_noOffset() {
  long oneDay = DateTimeConstants.MILLIS_PER_DAY;
  DateTimeZone zone = DateTimeZone.forOffsetHours(-2);
  Chronology chrono = ISOChronology.getInstance(zone);
  MutablePeriod period = new MutablePeriod(oneDay, PeriodType.standard(), chrono);
  assertEquals(1, period.getDays());
  assertEquals(0, period.getHours());
  assertEquals(0, period.getMinutes());
  assertEquals(0, period.getSeconds());
  assertEquals(0, period.getMillis());
  }
  @Test
  public void testLargeDurationFixedZone_fieldsContained() {
  long large = 100L
  * DateTimeConstants.MILLIS_PER_DAY;
  Chronology chrono = ISOChronology.getInstance(DateTimeZone.UTC);
  MutablePeriod period = new MutablePeriod(large, PeriodType.standard(), chrono);
  assertTrue("days should be positive", period.getDays() >= 100);
  assertEquals(0, period.getHours());
  }
  // --------------------------------------------------------------------
  // The duration-only constructor (uses default zone)
  // --------------------------------------------------------------------
  @Test
  public void testDurationOnlyConstructor_withDefaultZoneUTC() {
  DateTimeZone.setDefault(DateTimeZone.UTC);
  MutablePeriod period = new MutablePeriod(0L);
  for (int i = 0; i < period.size(); i++) {
      assertEquals("field " + period.getFieldType(i), 0, period.getValue(i));
  }
  }
  @Test
  public void testDurationOnlyConstructor_nonZeroDuration_defaultUTC() {
  DateTimeZone.setDefault(DateTimeZone.UTC);
  MutablePeriod period = new MutablePeriod(60_000L); // 1 minute
  assertEquals(0, period.getHours());
  assertEquals(1, period.getMinutes());
  assertEquals(0, period.getSeconds());
  }
  // --------------------------------------------------------------------
  // Two-instant constructor with fixed zone
  // --------------------------------------------------------------------
  @Test
  public void testTwoInstantFixedZone_noOffsetInPeriod() {
  DateTimeZone zone = DateTimeZone.forID("+05:30");
  Chronology chrono = ISOChronology.getInstance(zone);
  long start = new DateTime(2010, 1, 1, 0, 0, chrono).getMillis();
  long end   = new DateTime(2010, 1, 1, 2, 0, chrono).getMillis();
  MutablePeriod period = new MutablePeriod(start, end, PeriodType.standard(), chrono);
  assertEquals(2, period.getHours());
  assertEquals(0, period.getMinutes());
  assertEquals(0, period.getSeconds());
  }
  // --------------------------------------------------------------------
  // Internal method coverage via public API
  // --------------------------------------------------------------------
  @Test
  public void testSetPeriod_mergeValuesCorrectly() {
  MutablePeriod period = new MutablePeriod(0L, PeriodType.standard(),
ISOChronology.getInstanceUTC());
  MutablePeriod source = new MutablePeriod();
  source.setHours(3);
  source.setMinutes(45);
  period.setPeriod(source); // triggers setPeriodInternal
  assertEquals(3, period.getHours());
  assertEquals(45, period.getMinutes());
  assertEquals0, period.getSeconds());
  }
  @Test
  public void testAddField_unsupportedFieldThrows() {
  MutablePeriod period = new MutablePeriod(0L, PeriodType.days(), ISOChronology.getInstanceUTC());
  try {
      period.add(DurationFieldType.hours(), 1);
      fail("Expected IllegalArgumentException");
  } catch (IllegalArgumentException e) {
      // expected
  }
  }
  @Test
  public void testSetFieldInto_checkAndUpdateRestrictedType() {
  MutablePeriod period = new MutablePeriod(0L, PeriodType.days(), ISOChronology.getInstanceUTC());
  try {
      period.set(DurationFieldType.hours(), 5);
      fail("Expected IllegalArgumentException");
  } catch (IllegalArgumentException e) {
      // expected
  }
  }

}
```