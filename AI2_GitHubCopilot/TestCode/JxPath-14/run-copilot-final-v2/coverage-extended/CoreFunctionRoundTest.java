package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;

public class CoreFunctionRoundTest extends TestCase {

 private JXPathContext newContext(Object bean) {
     return JXPathContext.newContext(bean);
 }

 private double roundValue(JXPathContext context, String expression) {
     Object value = context.getValue(expression);
     assertTrue("Expected a Double result for " + expression,
                value instanceof Double);
     return ((Double) value).doubleValue();
 }

 private void assertNaN(String expression) {
     JXPathContext context = newContext(new Object());
     assertTrue("Expected NaN for " + expression,
                Double.isNaN(roundValue(context, expression)));
 }

 private void assertException(String expression) {
     JXPathContext context = newContext(new Object());
     try {
         context.getValue(expression);
         fail("Expected JXPathException for " + expression);
     }
     catch (JXPathException expected) {
         // expected failure
     }
 }

 public void testRoundLiteralNaN() {
     assertNaN("round('NaN')");
 }

 public void testRoundNumberFunctionNaN() {
     assertNaN("round(number('NaN'))");
 }

 public void testRoundNonNumericStringNaN() {
     assertNaN("round('abc')");
 }

 public void testRoundNormalValues() {
     JXPathContext context = newContext(new Object());
     assertEquals(4.0, roundValue(context, "round(4.49)"), 0.0);
     assertEquals(5.0, roundValue(context, "round(4.5)"), 0.0);
     assertEquals(5.0, roundValue(context, "round(4.51)"), 0.0);
     assertEquals(-4.0, roundValue(context, "round(-4.5)"), 0.0);
     assertEquals(-5.0, roundValue(context, "round(-4.51)"), 0.0);
     assertEquals(1.0, roundValue(context, "round(0.5)"), 0.0);
 }

 public void testRoundIntegersAndZero() {
     JXPathContext context = newContext(new Object());
     assertEquals(7.0, roundValue(context, "round(7)"), 0.0);
     assertEquals(-3.0, roundValue(context, "round(-3)"), 0.0);
     assertEquals(0.0, roundValue(context, "round(0)"), 0.0);
 }

 public void testRoundPositiveInfinity() {
     JXPathContext context = newContext(new InfinityBean(Double.POSITIVE_INFINITY));
     assertEquals(Double.POSITIVE_INFINITY, roundValue(context, "round(value)"), 0.0);
 }

 public void testRoundNegativeInfinity() {
     JXPathContext context = newContext(new InfinityBean(Double.NEGATIVE_INFINITY));
     assertEquals(Double.NEGATIVE_INFINITY, roundValue(context, "round(value)"), 0.0);
 }

 public void testRoundTooManyArguments() {
     assertException("round(1, 2)");
 }

 public void testRoundTooFewArguments() {
     assertException("round()");
 }

 public static class InfinityBean {
     private double value;

     public InfinityBean(double value) {
         this.value = value;
     }

     public double getValue() {
         return value;
     }
 }

}
