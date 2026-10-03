@org.junit.Test
public void testAllTimeBoundsWithNonChronologicalInsertion() {
    TimePeriodValues values = new TimePeriodValues("S");
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(100L), new java.util.Date(200L)),
            java.lang.Integer.valueOf(1));
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(0L), new java.util.Date(400L)),
            java.lang.Integer.valueOf(2));
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(50L), new java.util.Date(60L)),
            java.lang.Integer.valueOf(3));

    org.junit.Assert.assertEquals(1, values.getMinStartIndex());
    org.junit.Assert.assertEquals(0, values.getMaxStartIndex());
    org.junit.Assert.assertEquals(2, values.getMinMiddleIndex());
    org.junit.Assert.assertEquals(1, values.getMaxMiddleIndex());
    org.junit.Assert.assertEquals(2, values.getMinEndIndex());
    org.junit.Assert.assertEquals(1, values.getMaxEndIndex());
}

@org.junit.Test
public void testEqualsDistinguishesDescriptionsCountsAndItems() {
    org.jfree.data.time.SimpleTimePeriod period
            = new org.jfree.data.time.SimpleTimePeriod(
                    new java.util.Date(10L), new java.util.Date(20L));
    TimePeriodValues first = new TimePeriodValues("S");
    TimePeriodValues second = new TimePeriodValues("S");
    first.add(period, java.lang.Integer.valueOf(1));
    second.add(period, java.lang.Integer.valueOf(1));

    org.junit.Assert.assertTrue(first.equals(first));
    org.junit.Assert.assertFalse(first.equals("S"));
    org.junit.Assert.assertTrue(first.equals(second));

    second.setDomainDescription("Other Domain");
    org.junit.Assert.assertFalse(first.equals(second));
    second.setDomainDescription(first.getDomainDescription());

    second.setRangeDescription("Other Range");
    org.junit.Assert.assertFalse(first.equals(second));
    second.setRangeDescription(first.getRangeDescription());

    second.add(period, java.lang.Integer.valueOf(1));
    org.junit.Assert.assertFalse(first.equals(second));
    first.add(period, java.lang.Integer.valueOf(1));
    org.junit.Assert.assertTrue(first.equals(second));

    second.update(1, java.lang.Integer.valueOf(2));
    org.junit.Assert.assertFalse(first.equals(second));
}

@org.junit.Test
public void testCloneCreatesIndependentEquivalentSeries()
        throws CloneNotSupportedException {
    TimePeriodValues values = new TimePeriodValues("S");
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(10L), new java.util.Date(20L)),
            java.lang.Integer.valueOf(3));

    TimePeriodValues clone = (TimePeriodValues) values.clone();

    org.junit.Assert.assertNotSame(values, clone);
    org.junit.Assert.assertEquals(values, clone);
    clone.update(0, java.lang.Integer.valueOf(9));

    org.junit.Assert.assertEquals(3, values.getValue(0).intValue());
    org.junit.Assert.assertEquals(9, clone.getValue(0).intValue());
    org.junit.Assert.assertFalse(values.equals(clone));
}