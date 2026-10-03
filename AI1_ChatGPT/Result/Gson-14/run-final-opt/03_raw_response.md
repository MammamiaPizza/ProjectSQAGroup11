--- GsonTypesResolveRegressionTest.java
+++ GsonTypesResolveRegressionTest.java
@@ -18,6 +18,8 @@
     Holder<Number> exactNumber;
     Holder<? super Number> lowerNumber;
     Holder<? extends Number> upperNumber;
+    Recursive<? super Number> lowerRecursiveNumber;
+    Recursive<? extends Number> upperRecursiveNumber;
   }
 
   private static class Recursive<T> {
@@ -93,7 +95,7 @@
   @Test(timeout = 2000)
   public void resolvesSelfReferentialLowerWildcardWithoutRecursionOverflow() throws Exception {
     ParameterizedType context = (ParameterizedType)
-        Recursive.class.getDeclaredField("lowerSelf").getGenericType();
+        Contexts.class.getDeclaredField("lowerRecursiveNumber").getGenericType();
     TypeVariable<?> variable = Recursive.class.getTypeParameters()[0];
     Type expected = context.getActualTypeArguments()[0];
 
@@ -106,7 +108,7 @@
   @Test(timeout = 2000)
   public void resolvesSelfReferentialUpperWildcardWithoutRecursionOverflow() throws Exception {
     ParameterizedType context = (ParameterizedType)
-        Recursive.class.getDeclaredField("upperSelf").getGenericType();
+        Contexts.class.getDeclaredField("upperRecursiveNumber").getGenericType();
     TypeVariable<?> variable = Recursive.class.getTypeParameters()[0];
     Type expected = context.getActualTypeArguments()[0];
