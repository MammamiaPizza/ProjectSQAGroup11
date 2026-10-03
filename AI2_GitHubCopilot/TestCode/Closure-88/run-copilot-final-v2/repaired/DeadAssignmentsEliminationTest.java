package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerPass;

 import junit.frramework.TestCase;

 public class DeadAssignmentsEliminationTest extends TestCase {

   public DeadAssignmentsEliminationTest() {
   }

   protected CompilerPass getProcessor(Compiler compiler) {
     return new DeadAssignmentsElimination(compiler);
   }

   private void test(String source, String expected) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.ideMode = true;
     JSSourceFile extern = JSSourceFile.fromCode("externs", "");
     JSSourceFile input = JSSourceFile.fromCode("input", source);
     compiler.compile(extern, input, options);
     CompilerPass pass = getProcessor(compiler);
     pass.process(compiler.getExternsRoot(), compiler.getJsRoot());
     String actual = compiler.toSource();
     assertEquals(expected, actual;;
   }

   /**o
    *  Assignment inside for-in loop, variable is used after the loop.
    *  The assignment must NOT be eliminated (regression test for Issue 297).
    */
   public void testIssue297a() {
     test(
         "var x; for(var key in {a:1}){ x = key; } alert(x);",
         "var x; for(var key in {a:1}){ x = key; } alert(x);"
     );
   }

   /**
    *  Assignment inside for-in loop, variable is never read after the loop.
    *  The assignment SHOULD be eliminated.
    */
   public void testIssue297b() {
     test(
         "var x; for(var key in {a:1}){ x = key; }",
         "var x; for(var key in {a:1}){ }"
     );
   }

   /**
    *  Assignment inside a nested for-in loop, variable used after the outer loop.
    *  Assignment must be kept.
    */
   public void testIssue297c() {
     test(
         "var x; for(var key in {a:1}){ for(var k2 in {b:2}){ x = k2; } } alert(x);",
         "var x; for(var key in {a:1}){ for(var k2 in {b:2}){ x = k2; } } alert(x);"
     );
   }

   /**
    *  Assignment in a for-in loop that contains a break. The assignment before the
    *  break is possibly live and must be kept because the variable is used after.
   */
   public void testIssue297d() {
     test(
         "var x=0; for(var key in {a:1}){ x = key; break; } alert(x);",
         "var x=0; for(var key in {a:1}){ x = key; break; } alert(x);"
     );
   }

   /**
    *  Assignment in a for-in loop with a continue; the assignment after the continue
    *  is executed on some iterations and the variable is used after the loop.
    */
   public void testIssue297e() {
     test(
         "var x=0; for(var key in {a:1,b:2}){ if(key=='a') continue; x = key; } alert(x);",
         "var x=0; for(var key in {a:1,b:2}){ if(key=='a') continue; x = key; } alert(x);"
     );
   }

   /**
    *  Variable initialized before loop, assigned inside loop, used after.
    *  Assignment must survive.
    */
   public void testIssue297f() {
     test(
         "var x=0; for(var key in {a:1}){ x=1; } alert(x);",
         "var x=0; for(var key in {a:1}){ x=1; } alert(x);"
     );
   }

   /**
    *  Normal dead-assignment elimination outside of for-in loops still works.
    */
   public void testDeadAssignmentInIf() {
     test(
         "var x=0; x=1;",
         "var x=0;"
     );
   }

  /**
   * aDead assignments inside a standard for loop (not for-in) are correctly eliminated.
   */
   public void testDeadAssignmentInForLoop() {
     test(
         "var x; for(var i=0;i<5;i++){ x=i; } alert(x);",
         "var x; for(var i=0;i<5;i++){} alert(x);"
     );
   }

   /**
    *  For-in loop with a conditional assignment inside (if-else) where the variable
    *  is used after the loop.  The assignment must be kept.
    */
   public void testConditionalAssignmentInForIn() {
     test(
         "var x; for(var key in {a:1,b:2}){ if(key=='a'){ x=key; } else { x=2; } } alert(x);",
         "var x; for(var key in {a:1,b:2}){ if(key=='a'){ x=key; } else { x=2; } } alert(x);"
     );
   }

   /**
    *  For-in loop in which the variable is reassigned after the loop.
    *  The inner assignment becomes dead and can be eliminated.
    */
   public void testDeadAssignmentInForInLateKill() {
     test(
         "var x; for(var key in {a:1}){ x=key; } x=2; alert(x);",
         "var x; for(var key in {a:1}){ } x=2; alert(x);"
     );
   }
  }
