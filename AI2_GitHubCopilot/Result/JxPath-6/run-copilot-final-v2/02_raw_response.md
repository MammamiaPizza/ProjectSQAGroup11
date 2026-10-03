package org.apache.commons.jxpath.ri.compiler;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.JXPathContext;
 import org.apache.commons.jxpath.Pointer;
 import org.apache.commons.jxpath.ri.EvalContext;
 import org.apache.commons.jxpath.ri.InfoSetUtil;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.axes.InitialContext;
 import org.apache.commons.jxpath.ri.axes.RootContext;
 import org.apache.commons.jxpath.ri.model.NodePointer;
 import org.apache.commons.jxpath.ri.model.beans.PropertyPointer;
 import org.apache.commons.jxpath.ri.model.beans.NullPropertyPointer;

 /**
  * Tests for CoreOperationCompare focusing on the findMatch/contains/equal
  * logic triggered by bug JXPATH-94: "$d = 'a'" returns false when it should
  * return true when the variable $d iterates over a collection containing "a".
  */
 public class CoreOperationCompareTest extends TestCase {

     // Concrete subclass for testing the abstract class
     private static class TestableCompare extends CoreOperationCompare {
         public TestableCompare(Expression arg1, Expression arg2) {
             super(arg1, arg2);
         }

         public Object computeValue(EvalContext context) {
             return equal(context, args[0], args[1]) ? Boolean.TRUE : Boolean.FALSE;
         }

         public int getPrecedence() {
             return 0;
         }

         public boolean isSymmetric() {
             return true;
         }
     }

     private JXPathContext context;
     private TestBean testBean;

     public static class TestBean {
         private List items = new ArrayList();

         public TestBean() {
             items.add("a");
             items.add("b");
             items.add("c");
         }

         public List getItems() {
             return items;
         }

         public void setItems(List items) {
             this.items = items;
         }
     }

     private static final String EQUALS_A_EXPR = "$d = 'a'";

     protected void setUp() {
         testBean = new TestBean();
         context = JXPathContext.newContext(testBean);
     }

     /**
      * JXPATH-94: Single string value "a" in collection $d compared with string "a" should return
true.
      */
     public void testIterateVariableSingleMatch() {
         List singleList = new ArrayList();
         singleList.add("a");
         context.getVariables().declareVariable("d", singleList);
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertTrue("Evaluating <$d = 'a'> expected:<true> but was:<false>", result.booleanValue());
     }

     /**
      * JXPATH-94: $d contains "a" among other values; match-on-any semantics should find it.
      */
     public void testIterateVariableMatchInMultiValueCollection() {
         List multiList = new ArrayList();
         multiList.add("x");
         multiList.add("a");
         multiList.add("y");
         context.getVariables().declareVariable("d", multiList);
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertTrue("Evaluating <$d = 'a'> with multi-value collection expected:<true>",
result.booleanValue());
     }

     /**
      * JXPATH-94: The original trigger - variable holds the testBean.items list ["a","b","c"].
      * "$d = 'a'" should return true.
      */
     public void testIterateVariableOriginalTrigger() {
         context.getVariables().declareVariable("d", testBean.getItems());
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertTrue("Evaluating <$d = 'a'> expected:<true> for items [a,b,c]",
result.booleanValue());
     }

     /**
      * Normal equality: two scalar strings.
      */
     public void testEqualScalarStrings() {
         Expression left = new Constant("hello");
         Expression right = new Constant("hello");
         CoreOperationCompare op = new TestableCompare(left, right);
         Boolean result = (Boolean) op.computeValue(null);
         assertTrue("Equal strings should match", result.booleanValue());
     }

     /**
      * Normal inequality: two scalar strings differ.
      */
     public void testEqualScalarStringsDifferent() {
         Expression left = new Constant("hello");
         Expression right = new Constant("world");
         CoreOperationCompare op = new TestableCompare(left, right);
         Boolean result = (Boolean) op.computeValue(null);
         assertFalse("Different strings should not match", result.booleanValue());
     }

     /**
      * Pointer equality: same NodePointer values.
      */
     public void testEqualPointerSameValues() {
         TestBean bean = new TestBean();
         bean.setItems(Arrays.asList(new String[]{"x"}));
         JXPathContext ctx = JXPathContext.newContext(bean);

         Object ptr1 = ctx.getPointer("items[1]").getValue();
         Object ptr2 = ctx.getPointer("items[1]").getValue();

         Expression left = new Constant(ptr1);
         Expression right = new Constant(ptr2);
         CoreOperationCompare op = new TestableCompare(left, right);
         Boolean result = (Boolean) op.computeValue(null);
         assertTrue("Same pointer values should match", result.booleanValue());
     }

     /**
      * Boundary: null variable vs string literal - should not match.
      */
     public void testNullVariableVsString() {
         context.getVariables().declareVariable("d", null);
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertFalse("Null variable vs 'a' should be false", result.booleanValue());
     }

     /**
      * Boundary: empty collection variable vs string literal - no match.
      */
     public void testEmptyCollectionVariableVsString() {
         context.getVariables().declareVariable("d", Collections.EMPTY_LIST);
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertFalse("Empty collection vs 'a' should be false", result.booleanValue());
     }

     /**
      * Mixed types: collection contains non-String types, string value "a" is present.
      */
     public void testMixedTypesCollectionWithStringMatch() {
         List mixed = new ArrayList();
         mixed.add(new Integer(42));
         mixed.add(Boolean.TRUE);
         mixed.add("a");
         mixed.add(new Double(3.14));
         context.getVariables().declareVariable("d", mixed);
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertTrue("Mixed collection containing 'a' should match", result.booleanValue());
     }

     /**
      * Mixed types: collection does not contain the target string.
      */
     public void testMixedTypesCollectionWithoutMatch() {
         List mixed = new ArrayList();
         mixed.add(new Integer(42));
         mixed.add(Boolean.TRUE);
         mixed.add(new Double(3.14));
         context.getVariables().declareVariable("d", mixed);
         Boolean result = (Boolean) context.getValue(EQUALS_A_EXPR);
         assertFalse("Mixed collection without 'a' should not match", result.booleanValue());
     }

     /**
      * Iterator vs Iterator: findMatch with overlapping elements.
      */
     public void testFindMatchOverlappingIterators() {
         Iterator left = Arrays.asList(new String[]{"x", "a", "y"}).iterator();
         Iterator right = Arrays.asList(new String[]{"z", "a", "w"}).iterator();
         Expression leftExpr = new Constant(left);
         Expression rightExpr = new Constant(right);
         CoreOperationCompare op = new TestableCompare(leftExpr, rightExpr);
         Boolean result = (Boolean) op.computeValue(null);
         assertTrue("findMatch should detect overlapping 'a'", result.booleanValue());
     }

     /**
      * Iterator vs Iterator: no overlap.
      */
     public void testFindMatchDisjointIterators() {
         Iterator left = Arrays.asList(new String[]{"x", "y"}).iterator();
         Iterator right = Arrays.asList(new String[]{"z", "w"}).iterator();
         Expression leftExpr = new Constant(left);
         Expression rightExpr = new Constant(right);
         CoreOperationCompare op = new TestableCompare(leftExpr, rightExpr);
         Boolean result = (Boolean) op.computeValue(null);
         assertFalse("findMatch should return false for disjoint iterators", result.booleanValue());
     }
 }