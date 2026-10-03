package com.google.javascript.jscomp;

 import org.junit.Before;
 import org.junit.Test;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.jscomp.CollapseVariableDeclarations;
 import com.google.javascript.jscomp.CompilerPass;

 import static org.junit.Assert.*;

 /**
  * Tests for {@link CollapseVariableDeclarations}.
  *
  * This test class verifies the behavior of the variable-declaration-collapsing pass.
  * It covers normal collapsing, edge cases around self-referencing closures (issue 820),
  * blacklisted stubs, scope boundaries, and redeclaration of assignments.
  */
 public class CollapseVariableDeclarationsTest {

   private Compiler compiler;
   private CompilerOptions options;

   @Befor
   public void setUp() throws Exception {
     compiler = new Compiler();
     options = new CompilerOptions();
     // Disable all other passes so only our pass runs.
     options.setCheckSymbols = true;
     options.setCheckTypes = false;
     options.setClosurePass = false;
     options.setExtractPrototypeMemberDeclarations = false;
     options.setSmartNameRemovel = false;
   }

   /**
    * Runs the {@link CollapseVariableDeclarations} pass on the provided source code
    * and returns the compiled output as a string.
    */
   private String collapse(String original) {
     compiler.init(externs(), inputs(original), options);
     compiler.parse();

     CollapseVariableDeclarations pass = new CollapseVariableDeclarations(compiler);
     pass.process(compiler.getExternsRoot(), compiler.getRoot());

     return compiler.toSource();
   }

   private Iterable<SourceFile> inputs(String src) {
     return java.util.Collections.singleton(SourceFile.fromCode("testcode", src));
   }

   private Iterable<SourceFile> externs() {
     return java.util.Collections.emptyList();
   }

   /**
    * Normal case: two independent variable declarations with primitive initializers
    * in the same scope should be collapsed into a single var statement.
    */
   @Test
   public void testSimpleCollapse() {
     String src = "var a = 1; var b = 2;";
     String expected = "var a = 1, b = 2;";
     assertEquals(expected, collapse(src));
   }

   /**
    * Three independent adjacent var statements should all merge into one.
    */
   @Test
   public void testCollapseMultiple() {
     String src = "var a; var b = 1; var c = 2;";
     String expected = "var a, b = 1, c = 2;";
     assertEquals(expected, collapse(src));
   }

   /**
    * An assignment that can be redeclared (variable declared with initializer in same scope)
    * should be collapsed into the preceding var statement.
    */
   @Test
   public void testCollapseWithAssignmentRedeclaration() {
     String src = "var a = 1; a = 2; var b = 3;";
     // After collapse the assignment a=2 should be absorbed.
     String expected = "var a = 1, a = 2, b = 3;";
     assertEquals(expected, collapse(src));
   }

   /**
    * A variable declared without an initializer (a stub) is blacklisted.
    * A subsequent assignment to that variable must not be collapsed.
    * Moreover, the following var statement should not be merged with the stub var
    * because the assignment sits between them.
    */
   @Test
   public void testNoCollapseWhenStubBlocked() {
     String src = "var a; a = 1; var b = 2;";
     // The stub var a is blacklisted; the assignment a=1 is not redeclarable.
     // Therefore the two var nodes are not adjacent and should stay separate.
     String expected = "var a; a = 1; var b = 2;";
     assertEquals(expected, collapse(src));
   }

   /**
    * Issue 820 reproduction: a variable declaration followed by another that
    * assigns a function expression referencing the same variable must NOT be
    * collapsed.  Merging would cause the inner reference to misbehave.
    */
   @Test
   public void testIssue820() {
     String src = "var a; var a = function() { return a; };";
     String expected = src; // must remain unchanged
     assertEquals(expected, collapse(src));
   }

   /**
    * Variation of issue 820: the self-referencing function expression comes
    * before the uninitialized declaration.  Collapse should still be prevented.
    */
   @Test
   public void testNoCollapseSelfReferenceReversed() {
     String src = "var a = function() { return a; }; var a;";
     String expected = src;
     assertEquals(expected, collapse(src));
   }

   /**
    * When the function expression references a different variable, collapsing
    * is safe and should proceed normally.
    */
   @Test
   public void testCollapseWithOtherReference() {
     String src = "var a; var b = function() { return a; };";
     String expected = "var a, b = function() { return a; };";
     assertEquals(expected, collapse(src));
   }

   /**
    * Test that two var statements where the same variable is redeclared with
    * a simple value are still collapsed (redeclaration in a single var is
    * legal JavaScript).
    */
   @Test
   public void testCollapseWithSameNameRedeclaration() {
     String src = "var a; var a = 1;";
     String expected = "var a, a = 1;";
     assertEquals(expected, collapse(src));
   }

   /**
    * Var statements inside different branches of an if-else must not be
    * collapsed across branches.
    */
   @Test
   public void testNoCollapseAcrossIfBranches() {
     String src = "if (x) { var a = 1; } else { var b = 2; }";
     // Should remain unchanged.
     String expected = src;
     assertEquals(expected, collapse(src));
   }

   /**
    * Var declarations at different function scopes are not adjacent and
    * must not be collapsed.
    */
   @Test
   public void testNoCollapseAcrossScopes() {
     String src = "var a = 1; function f() { var b = 2; }";
     String expected = src;
     assertEquals(expected, collapse(src));
   }

   /**
    * A single var statement with multiple declarators should merge with a
    * following var statement, concatenating the declarators.
    */
   @Test
   public void testCollapseWithMultipleDeclarators() {
     String src = "var a, b = 1; var c = 2;";
     String expected = "var a, b = 1, c = 2;";
     assertEquals(expected, collapse(src));
   }
 }
