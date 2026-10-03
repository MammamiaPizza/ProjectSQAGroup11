package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import java.util.ArrayList;
 import java.util.List;

 /**
  * Tests for {@link UnreachableCodeElimination} focusing on bug 936:
  * break, continue, and return statements inside try-finally blocks
  * must not be removed.
  */
 public class UnreachableCodeEliminationTest extends TestCase {

   private Compiler compiler;
   private CompilerOptions options;

   @Override
   protected void setUp() {
     compiler = new Compiler();
     options = new CompilerOptions();
   }

   /**
    * Parses the given JS, runs only the UnreachableCodeElimination pass,
    * and returns the resulting source.
    */
   private String process(String js) {
     List<SourceFile> externs = new ArrayList<SourceFile>();
     externs.add(SourceFile.fromCode("externs.js", ""));
     List<SourceFile> inputs = new ArrayList<SourceFile>();
     inputs.add(SourceFile.fromCode("test.js", js));
     compiler.init(externs, inputs, options);
     compiler.parse();
     Node root = compiler.getRoot();
     Node externRoot = compiler.getExternsRoot();
     UnreachableCodeElimination pass =
         new UnreachableCodeElimination(compiler, true);
     pass.process(externRoot, root);
     return compiler.toSource();
   }

   private void testSame(String js) {
     assertEquals("Code should be unchanged", js.trim(), process(js).trim());
   }

   private void test(String js, String expected) {
     assertEquals(expected.trim(), process(js).trim());
   }

   // ---- Bug 936: try-finally preserves branching ----

   public void testIssue4177428_return() {
     testSame(
         "function f() { try { return 1; } finally { cleanup(); } }");
   }

   public void testDontRemoveBreakInTryFinally() {
     testSame(
         "function f() { while(a) { try { break; } finally { x(); } } }");
   }

   public void testIssue4177428_continue() {
     testSame(
         "function f() { for(;;) { try { continue; } finally { x(); } } }");
   }

   public void testDontRemoveBreakInTryFinallySwitch() {
     testSame(
         "function f() { while(a) { switch(b) { case 1: try { break; } finally { x(); } } } }");
   }

   public void testIssue4177428a() {
     testSame(
         "function f() { l: try { return 1; } finally { x(); } }");
   }

   public void testIssue4177428c() {
     testSame(
         "function f() { for(;;) { try { if (a) return; } finally { x(); } } }");
   }

   // ---- Additional try-finally edge cases ----

   public void testBreakInNestedTryFinally() {
     testSame(
         "function f() { outer: while(a) { try { try { break outer; } finally { y(); } } finally {
x(); } } }");
   }

   public void testContinueInTryFinallyLoop() {
     testSame(
         "function f() { for(var i=0;i<10;i++) { try { if (i==5) continue; } finally { x(); } } }");
   }

   public void testUnreachableCodeAfterTryFinallyReturnStillRemoved() {
     test(
         "function f() { try { return 1; } finally { x(); } return 2; }",
         "function f() { try { return 1; } finally { x(); } }");
   }

   // ---- Normal unreachable removal still works ----

   public void testNormalUnreachableAfterReturn() {
     test(
         "function f() { return 1; alert(2); }",
         "function f() { return 1; }");
   }

   public void testNormalUnreachableAfterBreak() {
     test(
         "function f() { while(a) { if (b) { break; x(); } } }",
         "function f() { while(a) { if (b) { break; } } }");
   }
 }