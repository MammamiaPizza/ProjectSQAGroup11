@Test
public void testCalculatePieDatasetTotalIgnoresNullAndNonPositiveValues() {
    org.jfree.data.general.DefaultPieDataset dataset
            = new org.jfree.data.general.DefaultPieDataset();
    dataset.setValue("Positive 1", 2.5);
    dataset.setValue("Null", null);
    dataset.setValue("Zero", 0.0);
    dataset.setValue("Negative", -4.0);
    dataset.setValue("Positive 2", 3.5);

    assertEquals(6.0, DatasetUtilities.calculatePieDatasetTotal(dataset),
            EPSILON);
}

@Test(expected = IllegalArgumentException.class)
public void testCalculatePieDatasetTotalRejectsNullDataset() {
    DatasetUtilities.calculatePieDatasetTotal(null);
}