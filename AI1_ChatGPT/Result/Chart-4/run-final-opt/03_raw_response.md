--- XYPlotInitializationRegressionTest.java
+++ XYPlotInitializationRegressionTest.java
@@ -116,7 +116,7 @@
         renderer.setSeriesToolTipGenerator(0, generator);
 
         assertSame(generator, renderer.getSeriesToolTipGenerator(0));
-        assertNotNull(renderer.getToolTipGenerator(0, 0));
+        assertNotNull(renderer.getToolTipGenerator(0, 0, false));
         assertSame(renderer, plot.getRenderer());
     }
