package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.EvalContext;

public class CoreOperationRelationalExpressionNaNTest extends TestCase {

 private static class Relational extends CoreOperationRelationalExpression {
     private final String op;

     Relational(Expression[] args, String op) {
         super(args);
         this.op = op;
     }

     protected boolean evaluateCompare(int compare) {
         if (">".equals(op)) {
             return compare > 0;
         }
         if (">=".equals(op)) {
             return compare >= 0;
         }
         if ("<".equals(op)) {
             return compare < 0;
         }
         if ("<=".equals(op)) {
             return compare <= 0;
         }
         throw new IllegalArgumentException("Unknown operator: " + op);
     }

     public String getSymbol() {
         return op;
     }

     boolean evaluate(EvalContext context) {
         return ((Boolean) computeValue(context)).booleanValue();
     }
 }

 private boolean compare(String op, double left, double right) {
     Expression[] args = new Expression[] {
         new Constant(new Double(left)),
         new Constant(new Double(right))
     };
     return new Relational(args, op).evaluate(null);
 }

 public void testNanGreaterThanNan() {
     assertFalse(compare(">", Double.NaN, Double.NaN));
 }

 public void testNanGreaterOrEqualNan() {
     assertFalse(compare(">=", Double.NaN, Double.NaN));
 }

 public void testNanLessThanNan() {
     assertFalse(compare("<", Double.NaN, Double.NaN));
 }

 public void testNanLessOrEqualNan() {
     assertFalse(compare("<=", Double.NaN, Double.NaN));
 }

 public void testNanGreaterThanZero() {
     assertFalse(compare(">", Double.NaN, 0.0));
 }

 public void testZeroGreaterThanNan() {
     assertFalse(compare(">", 0.0, Double.NaN));
 }

 public void testNanGreaterThanInfinity() {
     assertFalse(compare(">", Double.NaN, Double.POSITIVE_INFINITY));
 }

 public void testInfinityGreaterThanNan() {
     assertFalse(compare(">", Double.POSITIVE_INFINITY, Double.NaN));
 }

 public void testBaselineGreaterThan() {
     assertTrue(compare(">", 1.0, 0.0));
     assertFalse(compare(">", 0.0, 1.0));
     assertFalse(compare(">", 1.0, 1.0));
 }

 public void testBaselineGreaterOrEqual() {
     assertTrue(compare(">=", 1.0, 0.0));
     assertTrue(compare(">=", 1.0, 1.0));
     assertFalse(compare(">=", 0.0, 1.0));
 }

 public void testBaselineLessThan() {
     assertFalse(compare("<", 1.0, 0.0));
     assertTrue(compare("<", 0.0, 1.0));
     assertFalse(compare("<", 1.0, 1.0));
 }

 public void testBaselineLessOrEqual() {
     assertFalse(compare("<=", 1.0, 0.0));
     assertTrue(compare("<=", 1.0, 1.0));
     assertTrue(compare("<=", 0.0, 1.0));
 }

}
