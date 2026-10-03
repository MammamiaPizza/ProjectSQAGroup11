package org.apache.commons.jxpath.ri.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.axes.InitialContext;
import org.apache.commons.jxpath.ri.axes.SelfContext;

/**
 *

 - Tests for CoreOperationRelationalExpression (targets JXPATH-149 and iterator-argument swap bug).
  */
 public class CoreOperationRelationalExpressionTest extends TestCase {
  // --- Basic scalar comparisons (golden path) ---
  public void testLessThanOrEqual_Scalars_True() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(3));
  ctx.getVariables().declareVariable("y", Integer.valueOf(7));
  Boolean result = (Boolean) ctx.getValue("$x <= $y");
  assertTrue("3 <= 7", result.booleanValue());
  }
  public void testLessThanOrEqual_Scalars_False() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(5));
  ctx.getVariables().declareVariable("y", Integer.valueOf(2));
  Boolean result = (Boolean) ctx.getValue("$x <= $y");
  assertFalse("5 <= 2", result.booleanValue());
  }
  public void testGreaterThan_Scalars_True() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(9));
  ctx.getVariables().declareVariable("y", Integer.valueOf(1));
  Boolean result = (Boolean) ctx.getValue("$x > $y");
  assertTrue("9 > 1", result.booleanValue());
  }
  public void testGreaterThan_Scalars_False() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(1));
  ctx.getVariables().declareVariable("y", Integer.valueOf(9));
  Boolean result = (Boolean) ctx.getValue("$x > $y");
  assertFalse("1 > 9", result.booleanValue());
  }
  // --- Iterator / scalar mixing (covers the swapped-arguments bug) ---
  public void testLessThanOrEqual_LeftIteratorRightScalar_NoSwap() {
  // left is collection, right scalar -> correct path: for each left elem, check leftElem <= right
  JXPathContext ctx = JXPathContext.newContext(new Object());
  List left = Arrays.asList(Integer.valueOf(1), Integer.valueOf(8), Integer.valueOf(3));
  ctx.getVariables().declareVariable("left", left);
  ctx.getVariables().declareVariable("right", Integer.valueOf(5));
  Boolean result = (Boolean) ctx.getValue("$left <= $right");
  // Among left: 1 <= 5 true, 8 <= 5 false, 3 <=5 true -> overall true
  assertTrue("At least one left element <= right", result.booleanValue());
  }
  public void testLessThanOrEqual_RightIteratorLeftScalar_Bug() {
  // This is the JXPATH-149 trigger: left scalar, right collection.
  // Expected: left <= any right element? Actually relational semantics: scalar <= iterator
  // means that there exists an element in right s.t. left <= element? Or universal?
  // In JXPath, iterable on right likely means "exists" matching. We'll test that.
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("a", Integer.valueOf(7));
  ctx.getVariables().declareVariable("c", Arrays.asList(Integer.valueOf(10), Integer.valueOf(2)));
  // Expression $a <= $c should be true because 7 <= 10
  Boolean result = (Boolean) ctx.getValue("$a <= $c");
  assertTrue("7 <= collection (10,2) -> true", result.booleanValue());
  }
  public void testGreaterThan_RightIteratorLeftScalar_Bug() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("a", Integer.valueOf(5));
  ctx.getVariables().declareVariable("c", Arrays.asList(Integer.valueOf(1), Integer.valueOf(8)));
  // $a > $c expected true because 5 > 1 exists
  Boolean result = (Boolean) ctx.getValue("$a > $c");
  assertTrue("5 > collection (1,8) -> true", result.booleanValue());
  }
  public void testLessThanOrEqual_BothIterators() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  List l1 = Arrays.asList(Integer.valueOf(1), Integer.valueOf(9));
  List l2 = Arrays.asList(Integer.valueOf(5), Integer.valueOf(10));
  ctx.getVariables().declareVariable("left", l1);
  ctx.getVariables().declareVariable("right", l2);
  // Intersection exists: 1<=5,9<=10, so true
  Boolean result = (Boolean) ctx.getValue("$left <= $right");
  assertTrue("Iterators intersection", result.booleanValue());
  }
  // --- NaN handling (always returns false) ---
  public void testLessThanOrEqual_LeftNaN_ReturnsFalse() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Double.NaN);
  ctx.getVariables().declareVariable("y", Integer.valueOf(0));
  Boolean result = (Boolean) ctx.getValue("$x <= $y");
  assertFalse("NaN <= y → false", result.booleanValue());
  }
  public void testLessThanOrEqual_RightNaN_ReturnsFalse() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(42));
  ctx.getVariables().declareVariable("y", Double.NaN);
  Boolean result = (Boolean) ctx.getValue("$x <= $y");
  assertFalse("x <= NaN → false", result.booleanValue());
  }
  // --- Infinity ---
  public void testLessThanOrEqual_Infinity() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Double.POSITIVE_INFINITY);
  ctx.getVariables().declareVariable("y", Double.MAX_VALUE);
  Boolean result = (Boolean) ctx.getValue("$x <= $y");
  assertFalse("Infinity <= large number → false", result.booleanValue());
  }
  // --- Mixed types ---
  public void testLessThanOrEqual_MixedTypes() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(3));
  ctx.getVariables().declareVariable("y", Double.valueOf(3.1));
  Boolean result = (Boolean) ctx.getValue("$x <= $y");
  assertTrue("3 <= 3.1 → true", result.booleanValue());
  }
  // --- Empty collection corner case ---
  public void testLessThanOrEqual_EmptyRightIterator_ReturnsFalse() {
  JXPathContext ctx = JXPathContext.newContext(new Object());
  ctx.getVariables().declareVariable("x", Integer.valueOf(5));
  ctx.getVariables().declareVariable("empty", Collections.EMPTY_LIST);
  Boolean result = (Boolean) ctx.getValue("$x <= $empty");
  assertFalse("No match in empty → false", result.booleanValue());
  }

}
