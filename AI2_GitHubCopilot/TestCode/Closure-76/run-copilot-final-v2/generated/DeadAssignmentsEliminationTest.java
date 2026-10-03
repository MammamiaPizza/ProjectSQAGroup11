package com.google.javascript.jscomp;

 import com.google.common.base.Preconditions;
 import com.google.javascript.rhino.Node;

 /**
  * Tests for {@link DeadAssignmentsElimination} focusing on the bug reported
  * in Issue 384: incorrect removal of assignments when expressions contain
  * side effects or nested assignments.
  */
 public class DeadAssignmentsEliminationTest extends CompilerTestCase {

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     enableTypeCheck(CheckLevel.OFF);
     enableNormalize(false);
     allowExternsChanges(false);
   }

   @Override
   protected CompilerPass getProcessor(final Compiler compiler) {
     return new CompilerPass() {
       @Override
       public void process(Node externs, Node root) {
         new DeadAssignmentsElimination(compiler).process(externs, root);
       }
     };
   }

   /**
    * Nested dead assignments without side effects: both should be removed.
    * (Trigger: testInExpression2)
    */
   @Test
   public void testInExpression2() {
     // x = (y = 1) – both x and y are dead, no calls, so entire statement is
     // eliminated.
     test("function f() { var x,y; x = (y = 1); }",
          "function f() { }");
   }

   /**
    * Dead assignment whose RHS is a function call must be kept as an
    * expression statement so that the call’s side effects execute.
    * (Trigger: testIssue384b)
    */
   @Test
   public void testIssue384b() {
     // x is dead; foo() has side-effect → preserve the call.
     test("function f() { var x; x = foo(); }",
          "function f() { foo(); }");
   }

   /**
    * Dead assignment inside an object literal: the property value is an
    * assignment whose RHS has side effects. The assignment itself is dead but
    * the call must remain.
    * (Trigger: testIssue384c)
    */
   @Test
   public void testIssue384c() {
     test("function f() { var x; ({p: x = foo()}); }",
          "function f() { ({p: foo()}); }");
   }

   /**
    * Dead assignment used only in a dead branch and a dead assignment after
    * the branch. Both assignments should be removed; the dead branch itself
    * remains.
    * (Trigger: testIssue384d)
    */
   @Test
   public void testIssue384d() {
     test("function f() { var x; if(false){x=1;} x=2; }",
          "function f() { if(false){ } }");
   }

   /**
    * An assignment whose RHS has a side-effect (property write) is not dead
    * even if the assigned variable is unused.
    */
   @Test
   public void testDeadAssignmentWithPropertyWrite() {
     test("function f() { var a; a.b = 123; }",
          "function f() { var a; a.b = 123; }");
   }

   /**
    * Dead assignment used as the condition of an if-statement: the variable
    * is dead but the condition expression includes a call that must execute.
    */
   @Test
   public void testDeadAssignmentInCondition() {
     test("function f() { var x; if(x = foo()) { } }",
          "function f() { if(foo()) { } }");
   }

   /**
    * For-loop increment is required for correct loop semantics even when the
    * loop variable is dead after the loop.
    */
   @Test
   public void testForLoopIncrementKept() {
     test("function f() { for(var i=0; i<10; i++) { } }",
          "function f() { for(var i=0; i<10; i++) { } }");
   }

   /**
    * A chain of assignments where the RHS contains calls: the side effects
    * must be preserved.  Only the dead inner assignments without side effects
    * can be removed.
    */
   @Test
   public void testDeadAssignmentCommaWithCalls() {
     // x is dead; the RHS is a comma expression with two calls. The whole
     // assignment becomes the comma expression.
     test("function f() { var x; x = (foo(), bar()); }",
          "function f() { foo(), bar(); }");
   }

   /**
    * Simple dead assignment (no side effects, variable unused) is eliminated.
    */
   @Test
   public void testSimpleDeadAssignment() {
     test("function f() { var x; x = 1; }",
          "function f() { }");
   }

   /**
    * Assignment to a variable that is read later is never removed.
    */
   @Test
   public void testLiveAssignment() {
     test("function f() { var x; x = 1; alert(x); }",
          "function f() { var x; x = 1; alert(x); }");
   }

   /**
    * Dead assignment whose RHS is a property read (side-effect free) is
    * removed together with the entire statement.
    */
   @Test
   public void testDeadAssignmentRhsPropertyRead() {
     test("function f() { var x; x = a.b; }",
          "function f() { }");
   }

   /**
    * While-loop condition with a side-effectful dead assignment: the call
    * must stay.
    */
   @Test
   public void testDeadAssignmentInWhileCondition() {
     test("function f() { var x; while(x = bar()) { } }",
          "function f() { while(bar()) { } }");
   }
 }
