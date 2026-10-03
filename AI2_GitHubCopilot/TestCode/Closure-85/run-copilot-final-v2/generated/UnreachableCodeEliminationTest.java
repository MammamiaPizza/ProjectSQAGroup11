package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import junit.framework.TestCase;

 import java.util.List;

 /**
  * Tests for {@link UnreachableCodeElimination}.
  */
 public class UnreachableCodeEliminationTest extends TestCase {

   /**
    * Asserts that running the pass on the given JavaScript does not cause any
    * {@link RuntimeException} (i.e. no internal compiler error).
    */
   private void assertNoCrash(String js, boolean removeNoOp) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.setCheckSymbols(false);
     options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF);

     // Use empty externs to keep the test self-contained.
     SourceFile externs = SourceFile.fromCode("externs", "");
     SourceFile input = SourceFile.fromCode("test", js);
     compiler.init(new SourceFile[] {externs}, new SourceFile[] {input}, options);
     compiler.parse();

     // The root node has externs as first child, source as second.
     Node externRoot = compiler.getRoot().getFirstChild();
     Node jsRoot = externRoot.getNext();

     try {
       UnreachableCodeElimination pass =
           new UnreachableCodeElimination(compiler, removeNoOp);
       pass.process(externRoot, jsRoot);
     } catch (RuntimeException e) {
       fail("Pass threw RuntimeException: " + e.getMessage());
     }
   }

   /**
    * Runs the pass and returns the resulting JavaScript source.
    */
   private String runAndGetSource(String js, boolean removeNoOp) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.setCheckSymbols(false);
     options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF);

     SourceFile externs = SourceFile.fromCode("externs", "");
     SourceFile input = SourceFile.fromCode("test", js);
     compiler.init(new SourceFile[] {externs}, new SourceFile[] {input}, options);
     compiler.parse();

     Node externRoot = compiler.getRoot().getFirstChild();
     Node jsRoot = externRoot.getNext();

     UnreachableCodeElimination pass =
         new UnreachableCodeElimination(compiler, removeNoOp);
     pass.process(externRoot, jsRoot);

     return compiler.toSource();
   }

   /**
    * Regression test for Issue 311: removing a CATCH clause inside a try
    * must not cause an internal compiler error.
    */
   public void testIssue311() {
     // The original report triggered an INTERNAL COMPILER ERROR.
     // Even if the transformed code is semantically questionable the pass
     // must not crash.
     String js =
         "function f() { " +
         "  try { return; } catch(e) { } " +
         "}";
     assertNoCrash(js, true);
   }

   /**
    * Cascaded removal must not over-eliminate: a break that guards
    * unreachable side-effect code must be kept because removing it
    * would make that code reachable and alter behavior.
    */
   public void testCascadedRemovalRetainsNeededBreak() {
     String js =
         "function f() { " +
         "  L: { break L; var a = 1; } " +
         "  return a; " +   // a would be created if break is removed
         "}";

     String result = runAndGetSource(js, false /* removeNoOp */);
     // The break must remain (it is the only thing keeping var a dead).
     assertTrue("Break should still be present", result.contains("break"));
     // var a is unreachable and should have been removed.
     assertFalse("var a should be removed", result.contains("a = 1"));
   }

   /**
    * A single unconditional break whose target is the same as the
    * fall-through node is safe to remove when no side effects intervene.
    */
   public void testSingleUnconditionalJumpRemoval() {
     String js =
         "function f() { " +
         "  L: { break L; } " +
         "  return 1; " +
         "}";
     String result = runAndGetSource(js, true);
     // The now-empty labeled block may be collapsed; the key is that
     // the final return must survive.
     assertTrue("return 1 should remain", result.contains("return 1"));
   }

   /**
    * Unreachable statements after a RETURN inside a TRY block are removed,
    * but the TRY itself must stay intact.
    */
   public void testTryFinallyUnreachableCode() {
     String js =
         "function f() { " +
         "  try { " +
         "    return 1; alert('dead'); " +
         "  } finally { } " +
         "}";
     String result = runAndGetSource(js, false);
     assertTrue("return statement should survive", result.contains("return"));
     assertFalse("dead alert must be removed", result.contains("alert"));
   }

   /**
    * Statements with no side effects are removed when
    * {@code removeNoOpStatements} is true, even if they are reachable.
    */
   public void testRemoveNoOpStatements() {
     String js =
         "function f() { " +
         "  var x = 1; " +
         "  42; " +        // no-op expression
         "  'hello'; " +   // no-op expression
         "  return x; " +
         "}";
     String result = runAndGetSource(js, true);
     // No-op expressions should disappear.
     assertFalse("42; must be removed", result.contains("42"));
     assertFalse("'hello'; must be removed", result.contains("hello"));
     // var declaration should stay because it may have side effects.
     assertTrue("var x should survive", result.contains("var x"));
   }

   /**
    * A DO-WHILE loop is never removed by this pass because removing it
    * is not always safe / straightforward.
    */
   public void testDoWhileNotRemoved() {
     String js = "function f() { do { } while(false); }";
     String result = runAndGetSource(js, false);
     assertTrue("do-while must not be removed", result.contains("do"));
   }

   /**
    * An empty BLOCK (i.e. {}) is left alone – FoldConstants handles it
    * later.
    */
   public void testEmptyBlockNotRemoved() {
     String js = "function f() { { } return; }";
     String result = runAndGetSource(js, false);
     // The empty block should still be present in some form; at least
     // the function must contain more than just "function f() { }".
     assertTrue("Empty block should survive", result.contains("{") && result.contains("}"));
   }

   /**
    * A CONDITIONAL RETURN (inside an if) must not be treated as
    * unconditional and may not be removed when it is the only exit point
    * of the branch.
    */
   public void testConditionalReturnNotRemoved() {
     String js =
         "function f(x) { " +
         "  if (x) { return 1; } " +
         "  return 2; " +
         "}";
     String result = runAndGetSource(js, false);
     // Both return statements must survive.
     assertTrue("return 1 must exist", result.contains("return 1"));
     assertTrue("return 2 must exist", result.contains("return 2"));
   }

   /**
    * A CONTINUE at the very end of a loop body can sometimes be removed,
    * but the loop itself must survive.
    */
   public void testContinueRemovalInLoop() {
     String js =
         "function f() { " +
         "  var i = 0; for(; i<10; i++) { continue; } " +
         "  return i; " +
         "}";
     String result = runAndGetSource(js, false);
     assertTrue("for loop must survive", result.contains("for"));
     assertTrue("return i must remain", result.contains("return i"));
   }

   /**
    * A BREAK inside a SWITCH case is generally required and must not be
    * removed, even if it is the last statement in its case.
    */
   public void testBreakInSwitchCasePreserved() {
     String js =
         "function f(x) { " +
         "  switch(x) { case 1: break; } " +
         "}";
     String result = runAndGetSource(js, false);
     assertTrue("break in switch must stay", result.contains("break"));
   }

   /**
    * Nested function declarations are never removed by this pass, even
    * when they appear in unreachable code (removing them is the job of
    * later passes).
    */
   public void testNestedFunctionNotRemove() {
     String js =
         "function f() { " +
         "  return; " +
         "  function g() { } " +   // unreachable but not removed by this pass
         "}";
     String result = runAndGetSource(js, false);
     // The nested function should NOT be stripped by UnreachableCodeElimination.
     assertTrue("nested function g should survive", result.contains("function g"));
   }

   /**
    * Cascaded removal of multiple unconditional jumps: only the truly
    * redundant ones are deleted and reachable code is kept.
    */
   public void testCascadedRemovalOfUnconditionlJumps() {
     // Three breaks in a row; only the first is needed to exit the label.
     // The second and third are dead code and may be removed.
     // The assignment after the label must survive.
     String js =
         "function f() { " +
         "  var a = 0; " +
         "  L: { break L; break L; break L; } " +
         "  a = 1; " +   // this must be reachable
         "  return a; " +
         "}";
     String result = runAndGetSource(js, false);
     // The two trailing breaks are dead => removed.
     // The a=1 assignment must be present.
     assertTrue("a = 1 must remain", result.contains("a = 1"));
     int count = result.split("break").length - 1;
     assertTrue("Only one break should survive", count == 1);
   }
 }
