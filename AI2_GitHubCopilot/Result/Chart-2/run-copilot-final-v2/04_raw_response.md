@org.junit.Test
    public void calculatePieDatasetTotalNullDatasetThrows() {
        try {
            org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(null);
            org.junit.Assert.fail("Expected IllegalArgumentException for a null dataset.");
        } catch (java.lang.IllegalArgumentException expected) {
            // expected outcome
        }
    }

 @org.junit.Test
 public void calculatePieDatasetTotalOnlyPositiveValuesSummed() {
     org.jfree.data.general.DefaultPieDataset dataset = new
org.jfree.data.general.DefaultPieDataset();
     dataset.setValue("A", 5.0);
     dataset.setValue("B", -3.0);
     dataset.setValue("C", 0.0);
     double total = org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(dataset);
     org.junit.Assert.assertEquals(5.0, total, 0.0);
 }

 @org.junit.Test
 public void calculatePieDatasetTotalNullValueIgnored() {
     org.jfree.data.general.DefaultPieDataset dataset = new
org.jfree.data.general.DefaultPieDataset();
     dataset.setValue("A", 5.0);
     dataset.setValue("B", (java.lang.Number) null);
     double total = org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(dataset);
     org.junit.Assert.assertEquals(5.0, total, 0.0);
 }

 @org.junit.Test
 public void isEmptyOrNullCategoryDataset() {
     org.junit.Assert.assertTrue(
             org.jfree.data.general.DatasetUtilities.isEmptyOrNull(
                     (org.jfree.data.category.CategoryDataset) null));
     org.jfree.data.category.DefaultCategoryDataset empty = new
org.jfree.data.category.DefaultCategoryDataset();
     org.junit.Assert.assertTrue(org.jfree.data.general.DatasetUtilities.isEmptyOrNull(empty));
     org.jfree.data.category.DefaultCategoryDataset nonEmpty = new
org.jfree.data.category.DefaultCategoryDataset();
     nonEmpty.addValue(1.0, "Row", "Column");
     org.junit.Assert.assertFalse(org.jfree.data.general.DatasetUtilities.isEmptyOrNull(nonEmpty));
 }