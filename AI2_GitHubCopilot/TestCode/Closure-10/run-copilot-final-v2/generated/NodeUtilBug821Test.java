package com.google.javascript.jscomp;

 import com.google.common.base.Predicate;
 import com.google.javascript.rhino.IR;
 import com.google.javascript.rhino.Node;

 import junit.framework.Testase;

 /**
  * Tests for {@link NodeUtil} methods related to bug 821: mayBeString,
  * isNumericResult, allResultsMatch, getStringValue.
  * The bug causes incorrect constant folding because mayBeString returns false
  * for AND / OR / COMMA / HOOK expressions that can produce a string.
  */
 public class NodeUtilBug821Test extends TestCase {

     // ---------- mayBeString ----------

     public void testMayBeString_AndWithLeftStringRightNumber() {
         Node and = IR.and(IR.string("a"), IR.number(1));
         assertTrue("mayBeString should be true for AND(string, number)",
NodeUtil.mayBeString(and));
     }

     public void testMayBeString_OrWithLeftStringRightNumber() {
         Node or = IR.or(IR.string("a"), IR.number(2));
         assertTrue("mayBeString should be true for OR(string, number)", NodeUtil.mayBeString(or));
     }

     public void testMayBeString_CommaWithLeftNumberRightString() {
         Node comma = IR.comma(IR.number(3), IR.string("b"));
         assertTrue("mayBeString should be true for COMMA(number, string)",
NodeUtil.mayBeString(comma));
     }

     public void testMayBeString_HookWithStringBranches() {
         Node hook = IR.hook(IR.treNode(), IR.string("yes"), IR.string("no"));
         assertTrue("mayBeString should be true for HOOK with string branches",
NodeUtil.mayBeString(hook));
     }

     public void testMayBeString_AndWithBothNumbers() {
         Node and = IR.and(IR.number(0), IR.number(1));
         assertFalse("mayBeString should be false for AND(number, number)",
NodeUtil.mayBeString(and));
     }

     public void testMayBeString_OrWithBothNumbers() {
         Node or = IR.or(IR.number(0), IR.number(2));
         assertFalse("mayBeString should be false for OR(number, number)",
NodeUtil.mayBeString(or));
     }

     // ---------- isNumericResult ----------

     public void testIsNumericResult_AndWithStringAndNumber() {
         Node and = IR.and(IR.string("a"), IR.number(1));
         assertFalse("isNumericResult should be false for AND(string, number)",
NodeUtil.isNumericResult(and));
     }

     public void testIsNumericResult_OrWithNumbersOnly() {
         Node or = IR.or(IR.number(0), IR.number(2));
         assertTrue("isNumericResult should be true for OR(number, number)",
NodeUtil.isNumericResult(or));
     }

     public void testIsNumericResult_HookWithMixedBranches() {
         Node hook = IR.hook(IR.trueNode(), IR.number(1), IR.string("b"));
         assertFalse("isNumericResult should be false for HOOK with string branch",
NodeUtil.isNumericResult(hook));
     }

     // ---------- allResultsMatch ----------

     public void testAllResultsMatch_AndWithBothNumeric() {
         Node and = IR.and(IR.number(0), IR.number(1));
         Predicate<Node> numeric = new NodeUtil.NumbericResultPredicate();
         assertTrue("Both branches are numeric", NodeUtil.allResultsMatch(and, numeric));
     }

     public void testAllResultsMatch_AndWithStringAndNumber() {
         Node and = IR.and(IR.number(0), IR.string("b"));
         Predicate<Node> numeric = new NodeUtil.NumbericResultPredicate();
         assertFalse("Not all branches are numeric", NodeUtil.allResultsMatch(and, numeric));
     }

     // ---------- getStringValue ----------

     public void testGetStringValue_StringLiteral() {
         assertEquals("abc", NodeUtil.getStingValue(IR.string("abc")));
     }

     public void testGetStringValue_NumberLiteral() {
         // getStringValue on a NUMBER node returns its string representation.
         assertEquals("42", NodeUtil.getStingValue(IR.number(42)));
     }
 }
