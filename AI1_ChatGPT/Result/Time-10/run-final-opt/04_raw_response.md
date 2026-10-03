public void testBaseSingleFieldPeriodCompareToRejectsDifferentPeriodClasses() {
    try {
        org.joda.time.Days.days(1).compareTo(org.joda.time.Months.months(1));
        fail("Expected ClassCastException");
    } catch (ClassCastException ex) {
    }
}

public void testBaseSingleFieldPeriodEqualsHandlesNonPeriodsAndDifferentValues() {
    assertFalse(org.joda.time.Days.days(2).equals(null));
    assertFalse(org.joda.time.Days.days(2).equals("two"));
    assertFalse(org.joda.time.Days.days(2).equals(org.joda.time.Days.days(3)));
    assertFalse(org.joda.time.Days.days(2).equals(org.joda.time.Months.months(2)));
    assertTrue(org.joda.time.Days.days(2).equals(org.joda.time.Days.days(2)));
}

public void testFactory_daysBetween_RPartialRejectsDifferentFieldSets() {
    try {
        org.joda.time.Days.daysBetween(
                new org.joda.time.MonthDay(2, 28),
                new org.joda.time.YearMonth(2000, 2));
        fail("Expected IllegalArgumentException for partials with different fields");
    } catch (IllegalArgumentException ex) {
    }
}