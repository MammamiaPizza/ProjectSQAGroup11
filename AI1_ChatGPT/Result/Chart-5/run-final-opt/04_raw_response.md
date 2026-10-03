@Test
public void testPrimitiveAddOverloadsMaintainSortedOrder() {
    XYSeries series = new XYSeries("S");
    series.add(2.0, 20.0);
    series.add(4.0, new Integer(40));
    series.add(1.0, 10.0, false);
    series.add(3.0, new Integer(30), false);

    assertEquals(4, series.getItemCount());
    assertEquals(new Double(1.0), series.getX(0));
    assertEquals(new Double(2.0), series.getX(1));
    assertEquals(new Double(3.0), series.getX(2));
    assertEquals(new Double(4.0), series.getX(3));
}

@Test
public void testAddPlacesDuplicateXValuesAfterExistingDuplicates() {
    XYSeries series = new XYSeries("S", true, true);
    series.add(3.0, 30.0);
    series.add(2.0, 20.0);
    series.add(2.0, 21.0);
    series.add(3.0, 31.0);

    assertEquals(4, series.getItemCount());
    assertEquals(new Double(2.0), series.getX(0));
    assertEquals(new Double(20.0), series.getY(0));
    assertEquals(new Double(2.0), series.getX(1));
    assertEquals(new Double(21.0), series.getY(1));
    assertEquals(new Double(3.0), series.getX(2));
    assertEquals(new Double(30.0), series.getY(2));
    assertEquals(new Double(3.0), series.getX(3));
    assertEquals(new Double(31.0), series.getY(3));
}

@Test
public void testAddRejectsNullDataItem() {
    XYSeries series = new XYSeries("S");
    try {
        series.add((org.jfree.data.xy.XYDataItem) null);
        org.junit.Assert.fail("Expected IllegalArgumentException.");
    }
    catch (IllegalArgumentException e) {
        // expected
    }
}