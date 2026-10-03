@org.junit.Test
public void getLegendItemsReturnsEmptyCollectionWhenRendererHasNoPlot() {
    org.jfree.chart.renderer.category.DefaultCategoryItemRenderer renderer
            = new org.jfree.chart.renderer.category.DefaultCategoryItemRenderer();

    org.junit.Assert.assertEquals(0, renderer.getLegendItems().getItemCount());
}

@org.junit.Test
public void addAnnotationsInBothLayersAllowsEachToBeRemoved() {
    org.jfree.chart.renderer.category.DefaultCategoryItemRenderer renderer
            = new org.jfree.chart.renderer.category.DefaultCategoryItemRenderer();
    org.jfree.chart.annotations.CategoryTextAnnotation foreground
            = new org.jfree.chart.annotations.CategoryTextAnnotation("F", "C", 1.0);
    org.jfree.chart.annotations.CategoryTextAnnotation background
            = new org.jfree.chart.annotations.CategoryTextAnnotation("B", "C", 2.0);

    renderer.addAnnotation(foreground);
    renderer.addAnnotation(background, org.jfree.ui.Layer.BACKGROUND);

    org.junit.Assert.assertTrue(renderer.removeAnnotation(foreground));
    org.junit.Assert.assertTrue(renderer.removeAnnotation(background));
    org.junit.Assert.assertFalse(renderer.removeAnnotation(foreground));
}

@org.junit.Test
public void removeAnnotationsClearsAnnotationsFromBothLayers() {
    org.jfree.chart.renderer.category.DefaultCategoryItemRenderer renderer
            = new org.jfree.chart.renderer.category.DefaultCategoryItemRenderer();
    org.jfree.chart.annotations.CategoryTextAnnotation foreground
            = new org.jfree.chart.annotations.CategoryTextAnnotation("F", "C", 1.0);
    org.jfree.chart.annotations.CategoryTextAnnotation background
            = new org.jfree.chart.annotations.CategoryTextAnnotation("B", "C", 2.0);

    renderer.addAnnotation(foreground);
    renderer.addAnnotation(background, org.jfree.ui.Layer.BACKGROUND);
    renderer.removeAnnotations();

    org.junit.Assert.assertFalse(renderer.removeAnnotation(foreground));
    org.junit.Assert.assertFalse(renderer.removeAnnotation(background));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void addAnnotationRejectsNullAnnotation() {
    new org.jfree.chart.renderer.category.DefaultCategoryItemRenderer()
            .addAnnotation(null);
}