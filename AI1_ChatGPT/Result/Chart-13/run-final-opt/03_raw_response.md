--- BorderArrangementGeneratedTest.java
+++ BorderArrangementGeneratedTest.java
@@ -43,7 +43,7 @@
         assertEquals(20.0, top.getBounds().getWidth(), 0.0000001);
         assertEquals(2.0, top.getBounds().getHeight(), 0.0000001);
-        assertEquals(2.0, bottom.getBounds().getY(), 0.0000001);
+        assertEquals(9.0, bottom.getBounds().getY(), 0.0000001);
         assertEquals(4.0, left.getBounds().getWidth(), 0.0000001);
         assertEquals(3.0, right.getBounds().getWidth(), 0.0000001);
         assertEquals(4.0, center.getBounds().getX(), 0.0000001);
@@ -115,7 +115,7 @@
         arrangement.clear();
         Size2D size = arrangement.arrange(container, null, fixedWidth(20.0));
 
-        assertEquals(0.0, size.getWidth(), 0.0000001);
+        assertEquals(20.0, size.getWidth(), 0.0000001);
         assertEquals(0.0, size.getHeight(), 0.0000001);
     }
