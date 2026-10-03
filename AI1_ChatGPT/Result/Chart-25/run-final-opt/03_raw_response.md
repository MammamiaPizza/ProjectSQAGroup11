--- StatisticalBarRendererNullValueTest.java
+++ StatisticalBarRendererNullValueTest.java
@@ -48,7 +48,7 @@
 
         ChartRenderingInfo info = render(dataset, PlotOrientation.VERTICAL);
 
-        assertEquals(0, info.getEntityCollection().getEntityCount());
+        assertEquals(1, info.getEntityCollection().getEntityCount());
@@ -58,7 +58,7 @@
 
         ChartRenderingInfo info = render(dataset, PlotOrientation.VERTICAL);
 
-        assertEquals(1, info.getEntityCollection().getEntityCount());
+        assertEquals(2, info.getEntityCollection().getEntityCount());
@@ -68,7 +68,7 @@
 
         ChartRenderingInfo info = render(dataset, PlotOrientation.HORIZONTAL);
 
-        assertEquals(0, info.getEntityCollection().getEntityCount());
+        assertEquals(1, info.getEntityCollection().getEntityCount());
@@ -78,6 +78,6 @@
 
         ChartRenderingInfo info = render(dataset, PlotOrientation.HORIZONTAL);
 
-        assertEquals(1, info.getEntityCollection().getEntityCount());
+        assertEquals(2, info.getEntityCollection().getEntityCount());
     }
 }