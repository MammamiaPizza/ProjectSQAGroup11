@Test(expected = IllegalArgumentException.class)
public void testConstructorRejectsDifferentSeriesCounts() {
    new org.jfree.data.category.DefaultIntervalCategoryDataset(
            new Number[][] {{1.0}},
            new Number[][] {{1.0}, {2.0}});
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorRejectsDifferentCategoryCounts() {
    new org.jfree.data.category.DefaultIntervalCategoryDataset(
            new Number[][] {{1.0, 2.0}},
            new Number[][] {{1.0}});
}

@Test(expected = IllegalArgumentException.class)
public void testSetSeriesKeysRejectsWrongNumberOfKeys() {
    org.jfree.data.category.DefaultIntervalCategoryDataset dataset
            = new org.jfree.data.category.DefaultIntervalCategoryDataset(
                    new Number[][] {{1.0}, {2.0}},
                    new Number[][] {{3.0}, {4.0}});
    dataset.setSeriesKeys(new Comparable[] {"S1"});
}

@Test
public void testEmptyDatasetReturnsEmptyRowAndColumnKeyLists() {
    org.jfree.data.category.DefaultIntervalCategoryDataset dataset
            = new org.jfree.data.category.DefaultIntervalCategoryDataset(
                    new Number[0][0], new Number[0][0]);

    assertEquals(0, dataset.getRowKeys().size());
    assertEquals(0, dataset.getColumnKeys().size());
}