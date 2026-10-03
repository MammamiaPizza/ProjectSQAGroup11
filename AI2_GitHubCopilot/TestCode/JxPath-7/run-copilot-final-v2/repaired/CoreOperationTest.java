package org.apache.commons.jxpath.ri.compiler;

 import java.util.Arrays;

 import junit.framework.TestCase;
 import org.apache.commons.jxpath.ri.EvalContext;

 /**
  * Tests for relational comparison operations (&gt;, &gt;=, &lt;, &lt;=).
  * The buggy implementation does not handle node-set operands per XPath semantics:
  * when either operand is a node-set, the comparison should return true if
  * <em>any</em> node in the set satisfies the relation with the other operand.
  */
 public class CoreOperationTest extends TestCase {

     /**
      * A simple Expression stub that returns a constant value,
      * ignoring the EvalContext.
      */
     private static final class ConstantExpression extends Expression {
         private final Object value;

         ConstantExpression(Object value) {
             this.value = value;
         }

         public Object compute(EvalContext context) {
             return value;
         }

         public Object computeValue(EvalContext context) {
             return value;
         }
     }

     // Dummy context – unused by our stubs
     private static final EvalContext CTX = null;

     private static ConstantExpression num(double v) {
         return new ConstantExpression(Double.valueOf(v));
     }

     private static ConstantExpression nodeSet(Double... values) {
         return new ConstantExpression(Arrays.asList(values));
     }

     // ---------- GreaterThan ----------

     public void testGreaterThanScalars() {
         CoreOperationGreaterThan op = new CoreOperationGreaterThan(num(5), num(3));
         assertTrue("5 > 3", Boolean.TRUE.equals(op.computeValue(CTX)));

         op = new CoreOperationGreaterThan(num(3), num(5));
         assertFalse("3 > 5", Boolean.TRUE.equals(op.computeValue(CTX)));

         op = new CoreOperationGreaterThan(num(5), num(5));
         assertFalse("5 > 5", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testGreaterThanNodeSetWithMatch() {
         // BUG: [5,3,1] > 0  → any-node-satisfies: 5 > 0 → true
         CoreOperationGreaterThan op = new CoreOperationGreaterThan(
                 nodeSet(5.0, 3.0, 1.0), num(0));
         assertTrue("[5,3,1] > 0", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testGreaterThanNodeSetNoMatch() {
         CoreOperationGreaterThan op = new CoreOperationGreaterThan(
                 nodeSet(1.0, 2.0, 3.0), num(5));
         assertFalse("[1,2,3] > 5", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testGreaterThanScalarVsNodeSet() {
         // 10 > [5,15,3] → 10 > 5 is true
         CoreOperationGreaterThan op = new CoreOperationGreaterThan(
                 num(10), nodeSet(5.0, 15.0, 3.0));
         assertTrue("10 > [5,15,3]", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     // ---------- GreaterThanOrEqual ----------

     public void testGreaterThanOrEqualScalars() {
         CoreOperationGreaterThanOrEqual op =
                 new CoreOperationGreaterThanOrEqual(num(5), num(5));
         assertTrue("5 >= 5", Boolean.TRUE.equals(op.computeValue(CTX)));

         op = new CoreOperationGreaterThanOrEqual(num(3), num(5));
         assertFalse("3 >= 5", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testGreaterThanOrEqualNodeSetWithMatch() {
         // BUG: [3,5,1] >= 5 → 5 >= 5 is true
         CoreOperationGreaterThanOrEqual op = new CoreOperationGreaterThanOrEqual(
                 nodeSet(3.0, 5.0, 1.0), num(5));
         assertTrue("[3,5,1] >= 5", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testGreaterThanOrEqualNodeSetNoMatch() {
         CoreOperationGreaterThanOrEqual op = new CoreOperationGreaterThanOrEqual(
                 nodeSet(1.0, 2.0, 3.0), num(5));
         assertFalse("[1,2,3] >= 5", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     // ---------- LessThan ----------

     public void testLessThanScalars() {
         CoreOperationLessThan op = new CoreOperationLessThan(num(3), num(5));
         assertTrue("3 < 5", Boolean.TRUE.equals(op.computeValue(CTX)));

         op = new CoreOperationLessThan(num(5), num(3));
         assertFalse("5 < 3", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testLessThanNodeSetWithMatch() {
         // BUG: [1,2,3] < 5 → 1 < 5, 2 < 5, 3 < 5 → true
         CoreOperationLessThan op = new CoreOperationLessThan(
                 nodeSet(1.0, 2.0, 3.0), num(5));
         assertTrue("[1,2,3] < 5", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testLessThanNodeSetNoMatch() {
         CoreOperationLessThan op = new CoreOperationLessThan(
                 nodeSet(5.0, 6.0, 7.0), num(3));
         assertFalse("[5,6,7] < 3", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     // ---------- LessThanOrEqual ----------

     public void testLessThanOrEqualScalars() {
         CoreOperationLessThanOrEqual op =
                 new CoreOperationLessThanOrEqual(num(5), num(5));
         assertTrue("5 <= 5", Boolean.TRUE.equals(op.computeValue(CTX)));

         op = new CoreOperationLessThanOrEqual(num(5), num(3));
         assertFalse("5 <= 3", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     public void testLessThanOrEqualNodeSetWithMatch() {
         // BUG: [3,5,7] <= 3 → 3 <= 3 is true
         CoreOperationLessThanOrEqual op = new CoreOperationLessThanOrEqual(
                 nodeSet(3.0, 5.0, 7.0), num(3));
         assertTrue("[3,5,7] <= 3", Boolean.TRUE.equals(op.computeValue(CTX)));
     }

     // ---------- getSymbol ----------

     public void testGetSymbols() {
         assertEquals(">",
                 new CoreOperationGreaterThan(num(1)), num(2)).getSymbol());
         assertEquals("=>",
                 new CoreOperationGreaterThanOrEqual(num(1)), num(2)).getSymbol());
         assertEquals("<",
                 new CoreOperationLessThan(num(1)), num(2)).getSymbol());
         assertEquals("<=",
                 new CoreOperationLessThanOrEqual(num(1)), num(2)).getSymbol());
     }
 }
