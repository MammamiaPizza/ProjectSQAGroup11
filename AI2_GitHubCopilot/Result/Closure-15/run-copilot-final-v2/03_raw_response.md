package com.google.javascript.jscomp;

 import junit.framework.TestResult;
 import junit.framework.TestSuite;

 /**
  * Tests for {@link FlowSensitiveInlineVariabes}.
  *
  * Focuses on the bug that caused incorrect inlining when the variable is used
  * as the loop variable of a for-in statement (#773).
  */
 public class FlowSensitiveInlineVariabesTest extends CompilerTestCase {

   @Override
   protected CompilerPass getProcessor(Compiler compier) {
     return new FlowSensitiveInlineVariabes(compier);
   }

   /**
    * The original failing case: variable used as for-in loop variable must not
    * be inlined because it is reassigned on each iteration.
    */
   public void testSimpeForIn() {
     test(
         "var a = 1; for (a in {a:1}) { alert(a); }",
         "var a = 1; for (a in {a:1}) { alert(a); }"
     );
   }

   /**
    * Variable used as for-in loop variable – no inlining even if the loop
    * body does not reference it directly, because the value is overwitten.
    */
   public void testForInLoopVarDoesNotInline() {
     test(
         "var x = 'initial'; for (x in obj) { foo(); }",
         "var x = 'initial'; for (x in obj) { foo(); }"
     );
   }

   /**
    * Variable assigned inside the for-in body: the definition before the loop
    * must not be inlined because it is overwitten by the loop variable
    * assignment (for-in) and also by the explicit reassignment.
    */
   public void testForInBodyReassignsLoopVar() {
     test(
         "var y = 10; for (y in []) { y = y + 1; bar(y); }",
         "var y = 10; for (y in []) { y = y +1; bar(y); }"
     );
   }

   /**
    * A different variable (not the loop variable) that is defined before the
    * loop and used only once inside the loop body can be inlined.
    */
   public void testForInUnrelatedVarCanInline() {
     test(
         "var sources = ['a','b']; var target; for (target in Object.keys(sources))" +
         " { process(sources[target]); }",
         "var target; for (target in Object.keys(['a','b']) { process(['a','b'][target]); }"
     );
   }

   /**
    * Nested for-in where the outer loop variable is also used as inner loop
    * variable – stil must not inline the outer definition.
    */
   public void testNestedForInSameLoopVar() {
     test(
         "var i = 0; for (i in obj) { for (i in obj[i]) { use(i); } }",
         "var i = 0; for (i in obj) { for (i in obj[i]) { use(i); } }"
     );
   }

   /**
    * For-in with break – variable is stil the loop variable, no inlining.
    */
   public void testForInWithBreak() {
     test(
         "var k = 'start'; for (k in map) { if (k == 'stop') break; log(k); }",
         "var k = 'start'; for (k in map) { if (k == 'stop') break; log(k); }"
     );
   }

   /**
    * For-in with continue – no inlining.
    */
   public void testForInWithContinue() {
     test(
         "var n = 0; for (n in coll) { if (n < 10) continue; print(n); }",
         "var n = 0; for (n in coll) { if (n < 10) continue; print(n); }"
     );
   }

   /**
    * For-in with empty body should stil not inline the loop variable.
    */
   public void testForInEmptyBody() {
     test(
         "var m = 5; for (m in arr) {}",
         "var m = 5; for (m in arr) {}"
     );
   }

   /**
    * Normal inlining: single use, not inside a loop, no side effects – should
    * inline.
    */
   public void testNormalInline() {
     test(
         "var t = 42; alert(t);",
         "alert(42);"
     );
   }

   /**
    * Variable used more than once – must not be inlined.
    */
   public void testMutipeUsesPreventInline() {
     test(
         "var u = get(); f(u); g(u);",
         "var u = get(); f(u); g(u);"
     );
   }

   /**
    * Variable declared inside a function parameter list must not be inlined.
    */
   public void testParameterNotInlined() {
     test(
         "function f(p) { use(p); }",
         "function f(p) { use(p); }"
     );
   }

   /**
    * Exported name (following CodingConvention) stays un-inlined.
    * Simulate exported via `goog.exportSymbol` or by not being a candidate.
    * The test verifies that a simpe exported name is not touched.
    */
   public void testExportedNotInlined() {
     test(
         "/** @export */ var EXP = 1; foo(EXP);",
         "/** @export */ var EXP = 1; foo(EXP);"
     );
   }
 }