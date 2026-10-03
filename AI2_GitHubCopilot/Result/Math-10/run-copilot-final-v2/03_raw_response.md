package org.apache.commons.math3.analysis.differentiation;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for DSCompiler's atan2 method, focusing on the bug MATH-935:
  * atan2(0,0) derivatives produced NaN instead of 0.0.
  */
 public class DSCompilerTest {
     // --- Helper to allocate data array for a compiler ---
     private double[] allocateData(DSCompiler compiler) {
         return new double[compiler.getSize()];
     }

     // --- Helper to build constant-value array (derivatives zero) ---
     private double[] constantData(DSCompiler compiler, double value) {
         double[] data = allocateData(compiler);
         data[0] = value;
         return data;
     }

     // --- atan2(0,0) ---
     @Test
     public void testAtan2ZeroZeroOrder0() {
         DSCompiler compiler = DSCompiler.getCompiler(1, 0);
         double[] y = constantData(compiler, 0.0);
         double[] x = constantData(compiler, 0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value atan2(0,0) must be 0.0", 0.0, result[0], 1e-15);
     }

     @Test
     public void testAtan2ZeroZeroOrder1SingleFreeParam() {
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         double[] y = constantData(compiler, 0.0);
         double[] x = constantData(compiler, 0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value", 0.0, result[0], 1e-15);
         assertEquals("first derivative must be 0.0, not NaN", 0.0, result[1], 1e-15);
     }

     @Test
     public void testAtan2ZeroZeroOrder2SingleFreeParam() {
         DSCompiler compiler = DSCompiler.getCompiler(1, 2);
         double[] y = constantData(compiler, 0.0);
         double[] x = constantData(compiler, 0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value", 0.0, result[0], 1e-15);
         assertEquals("first derivative must be 0.0, not NaN", 0.0, result[1], 1e-15);
         assertEquals("second derivative must be 0.0, not NaN", 0.0, result[2], 1e-15);
     }

     @Test
     public void testAtan2ZeroZeroTwoFreeParamsFirstDerivsAreZero() {
         DSCompiler compiler = DSCompiler.getCompiler(2, 1);
         // y = variable 0 (d/dp0 = 1, d/dp1 = 0)
         double[] y = allocateData(compiler);
         y[0] = 0.0;
         y[1] = 1.0; // partial wrt param0
         y[2] = 0.0; // partial wrt param1
         // x = variable 1 (d/dp0 = 0, d/dp1 = 1)
         double[] x = allocateData(compiler);
         x[0] = 0.0;
         x[1] = 0.0;
         x[2] = 1.0;
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value", 0.0, result[0], 1e-15);
         assertEquals("d/dp0 must be 0.0, not NaN", 0.0, result[1], 1e-15);
         assertEquals("d/dp1 must be 0.0, not NaN", 0.0, result[2], 1e-15);
     }

     // --- Signed zeros ---
     @Test
     public void testAtan2NegativeZeroXZeroOrder1() {
         // atan2(-0.0, 0) = -0.0 per IEEE
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         double[] y = constantData(compiler, -0.0);
         double[] x = constantData(compiler, 0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value must be -0.0", Double.doubleToLongBits(-0.0),
                 Double.doubleToLongBits(result[0]));
         assertEquals("derivative must be 0.0 (not NaN)", 0.0, result[1], 1e-15);
     }

     @Test
     public void testAtan2ZeroXNegativeZeroOrder1() {
         // atan2(0, -0) = PI per IEEE
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         double[] y = constantData(compiler, 0.0);
         double[] x = constantData(compiler, -0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value must be PI", Math.PI, result[0], 1e-15);
         assertEquals("derivative must be 0.0 (not NaN)", 0.0, result[1], 1e-15);
     }

     @Test
     public void testAtan2NegativeZeroXNegativeZeroOrder1() {
         // atan2(-0.0, -0.0) = -PI per IEEE
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         double[] y = constantData(compiler, -0.0);
         double[] x = constantData(compiler, -0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value must be -PI", -Math.PI, result[0], 1e-15);
         assertEquals("derivative must be 0.0 (not NaN)", 0.0, result[1], 1e-15);
     }

     // --- One zero argument, non-zero other ---
     @Test
     public void testAtan2ZeroXPositive() {
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         // y = 0 (constant), x = variable with value 1.0
         double[] y = constantData(compiler, 0.0);
         double[] x = new double[compiler.getSize()];
         x[0] = 1.0;
         x[1] = 1.0; // df/dp = 1
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("atan2(0, positive) = 0.0", 0.0, result[0], 1e-15);
         // d/dp atan2(y(p),x(p)) = (xy' - yx')/(x^2+y^2)
         // y'=0, y=0, x'=1, x=1 => -0*1/(1)=0
         assertEquals("derivative must be 0.0", 0.0, result[1], 1e-15);
     }

     @Test
     public void testAtan2PositiveYZero() {
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         // y = variable with value 2.0, x = constant 0
         double[] y = new double[compiler.getSize()];
         y[0] = 2.0;
         y[1] = 1.0;
         double[] x = constantData(compiler, 0.0);
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("atan2(positive, 0) = PI/2", Math.PI / 2, result[0], 1e-15);
         // d/dp = (xy' - yx')/(x^2+y^2) = (0*1 - 2*0)/(0+4) = 0
         assertEquals("derivative must be 0.0", 0.0, result[1], 1e-15);
     }

     // --- Normal case with non-zero arguments, verifying correct derivatives ---
     @Test
     public void testAtan2NormalCaseBothNonZero() {
         DSCompiler compiler = DSCompiler.getCompiler(1, 1);
         // y = variable with value 1.0, derivative 1
         double[] y = new double[compiler.getSize()];
         y[0] = 1.0;
         y[1] = 1.0;
         // x = variable with value 2.0, derivative 1
         double[] x = new double[compiler.getSize()];
         x[0] = 2.0;
         x[1] = 1.0;
         double[] result = allocateData(compiler);
         compiler.atan2(y, 0, x, 0, result, 0);
         assertEquals("value", Math.atan2(1.0, 2.0), result[0], 1e-15);
         // d/dp atan2(y(p),x(p)) = (xy' - yx')/(x^2+y^2) = (2*1 - 1*1)/(4+1)= 1/5 = 0.2
         assertEquals("derivative", 0.2, result[1], 1e-15);
     }
 }