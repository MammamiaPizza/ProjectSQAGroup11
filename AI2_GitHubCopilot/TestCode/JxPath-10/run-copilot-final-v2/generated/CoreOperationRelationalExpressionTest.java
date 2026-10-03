package org.apache.commons.jxpath.ri.compiler;

 import java.util.Arrays;
 import java.util.List;

 import junit.framework.TestCase;
 import junit.framework.Assert;

 import org.apache.commons.jxpath.JXPathContext;

 /**
  * Tests for CoreOperationRelationalExpression, focusing on empty node‑set
  * behaviour per JXPATH-93.  When one operand is an empty node‑set the
  * comparison must always return false, regardless of the operator or the
  * value of the other operand (XPath 1.0 existential semantics).
  */
 public class CoreOperationRelationalExpressionTest extends TestCase {

     private JXPathContext context;

     /**
      * Simple bean used for tests that require an existing property.
      */
     public static final class Bean {
         private int value;
         private List numbers;

         public Bean() {
         }

         public Bean(int value) {
             this.value = value;
         }

         public int getValue() {
             return value;
         }

         public void setValue(int value) {
             this.value = value;
         }

         public List getNumbers() {
             return numbers;
         }

         public void setNumbers(List numbers) {
             this.numbers = numbers;
         }
     }

     protected void setUp() {
         // empty context – any property is non‑existent, producing empty node‑sets
         context = JXPathContext.newContext(new Object());
     }

     // ---------------------------------------------------------------
     // Empty left operand
     // ---------------------------------------------------------------

     public void testEmptyLeftGreaterThanZero() {
         Boolean result = (Boolean) context.getValue("/idonotexist > 0");
         assertFalse("Empty node‑set > 0 must be false", result.booleanValue());
     }

     public void testEmptyLeftGreaterThanOrEqualZero() {
         Boolean result = (Boolean) context.getValue("/idonotexist >= 0");
         assertFalse("Empty node‑set >= 0 must be false", result.booleanValue());
     }

     public void testEmptyLeftLessThanZero() {
         Boolean result = (Boolean) context.getValue("/idonotexist < 0");
         assertFalse("Empty node‑set < 0 must be false", result.booleanValue());
     }

     public void testEmptyLeftLessThanOrEqualZero() {
         Boolean result = (Boolean) context.getValue("/idonotexist <= 0");
         assertFalse("Empty node‑set <= 0 must be false", result.booleanValue());
     }

     public void testEmptyLeftEqualsZero() {
         Boolean result = (Boolean) context.getValue("/idonotexist = 0");
         assertFalse("Empty node‑set = 0 must be false", result.booleanValue());
     }

     public void testEmptyLeftNotEqualsZero() {
         Boolean result = (Boolean) context.getValue("/idonotexist != 0");
         assertFalse("Empty node‑set != 0 must be false", result.booleanValue());
     }

     // ---------------------------------------------------------------
     // Empty right operand
     // ---------------------------------------------------------------

     public void testEmptyRightGreaterThanZero() {
         Boolean result = (Boolean) context.getValue("0 < /idonotexist");
         assertFalse("0 < empty node‑set must be false", result.booleanValue());
     }

     // ---------------------------------------------------------------
     // Both operands empty
     // ---------------------------------------------------------------

     public void testBothEmpty() {
         Boolean result = (Boolean) context.getValue("/idonotexist = /idonotexist");
         assertFalse("Empty node‑set = empty node‑set must be false", result.booleanValue());
     }

     // ---------------------------------------------------------------
     // Empty left vs string
     // ---------------------------------------------------------------

     public void testEmptyLeftVsString() {
         Boolean result = (Boolean) context.getValue("/idonotexist >= 'abc'");
         assertFalse("Empty node‑set >= 'abc' must be false", result.booleanValue());
     }

     // ---------------------------------------------------------------
     // Non‑empty, existential semantics (sanity checks)
     // ---------------------------------------------------------------

     public void testNonEmptySingleNodeTrue() {
         JXPathContext ctx = JXPathContext.newContext(new Bean(10));
         Boolean result = (Boolean) ctx.getValue("/value >= 5");
         assertTrue("Existing node with value 10 >= 5 must be true", result.booleanValue());
     }

     public void testNonEmptySingleNodeFalse() {
         JXPathContext ctx = JXPathContext.newContext(new Bean(3));
         Boolean result = (Boolean) ctx.getValue("/value >= 5");
         assertFalse("Existing node with value 3 >= 5 must be false", result.booleanValue());
     }

     public void testNonEmptyMultiNodeExistentialTrue() {
         Bean b = new Bean();
         b.setNumbers(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(10),
Integer.valueOf(2) }));
         JXPathContext ctx = JXPathContext.newContext(b);
         Boolean result = (Boolean) ctx.getValue("numbers >= 5");
         assertTrue("Multi‑node set with at least one matching value must be true",
result.booleanValue());
     }
 }
