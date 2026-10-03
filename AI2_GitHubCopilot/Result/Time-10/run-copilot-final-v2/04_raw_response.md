@Test
 public void testCompareTo_differentClass() {
     org.joda.time.Days days = org.joda.time.Days.days(1);
     org.joda.time.Months months = org.joda.time.Months.months(1);
     try {
         days.compareTo(months);
         fail("Expected ClassCastException");
     } catch (ClassCastException e) {
         // expected
     }
 }

 @Test
 public void testEquals_differentPeriodType() {
     org.joda.time.Days days = org.joda.time.Days.days(5);
     org.joda.time.Months months = org.joda.time.Months.months(5);
     assertFalse(days.equals(months));
 }

 @Test
 public void testEquals_nonReadablePeriod() {
     org.joda.time.Days days = org.joda.time.Days.days(1);
     assertFalse(days.equals("not a period"));
 }

 @Test
 public void testGet_nonMatchingFieldType() {
     org.joda.time.Days days = org.joda.time.Days.days(3);
     assertEquals(0, days.get(org.joda.time.DurationFieldType.months()));
 }