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
  assertEquals(0, period.getSeconds());
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