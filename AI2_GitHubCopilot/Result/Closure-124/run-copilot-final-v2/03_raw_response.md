package com.google.javascript.jscomp;

 import com.google.common.collect.ImmutableList;
 import com.google.javascript.rhino.Node;
 import junit.framework.TestCase;

 /**
  * Tests for {@link ExploitAssigns}.
  */
 public class ExploitAssignsTest extends TestCase {

     // ---- testIssue1017 -----
     /**
      * Regression test for issue #1017: when a variable is reassigned inside
      * a conditional block, a later read of the original alias must not be
      * replaced with the new value because the alias was captured before the
      * conditional assignment.
      */
     public void testIssue1017() {
         String js = "var x=1;var y=x;if(true){x=2}var z=y";
         // With the bug, the pass would collapse "z=y" into "z=x" which
         // changes semantics (x may have been changed after the alias).
         // Therefore the input should remain unchanged.
         String expected = "var x=1;var y=x;if(true){x=2}var z=y";
         assertEquals(expected, process(js));
     }

     // ---- Normal safe collapses ----

     /** Simple variable to variable assignment collapse. */
     public void testSimpleVarCollapse() {
         assertEquals("var b=1,a=b", process("var b=1;var a=b"));
     }

     /** Boolean immutable value used as rvalue in subsequent statement. */
     public void testBooleanCollapse() {
         assertEquals("var b=true,a=b", process("var b=true;var a=true"));
     }

     /** Numeric immutable value used as rvalue in subsequent statement. */
     public void testNumberCollapse() {
         assertEquals("var b=3,a=b", process("var b=3;var a=3"));
     }

     /** String immutable value used as rvalue in subsequent statement. */
     public void testStringCollapse() {
         assertEquals("var b=\"hi\",a=b", process("var b=\"hi\";var a=\"hi\""));
     }

     /** Null literal used as rvalue in subsequent statement. */
     public void testNullCollapse() {
         assertEquals("var b=null,a=b", process("var b=null;var a=null"));
     }

     /** Nested assign chain collapse. b=c; a=b; => a=b=c. */
     public void testNestedAssignCollapse() {
         assertEquals("var c=1,b=c,a=b", process("var c=1;var b=c;var a=b"));
     }

     // ---- Property (GETPROP) L-value safety ----

     /** Collapse is allowed for properties on 'this'. */
     public void testThisPropCollapse() {
         assertEquals("var a=this.x=a", process("var a=this.x;a=this.x"));
     }

     /** Collapse must NOT happen for properties on non-this objects. */
     public void testNonThisPropNotCollapsed() {
         String js = "var a=obj.x;a=obj.x";
         assertEquals(js, process(js));
     }

     /** Assign-to-property chain: a.b=null; a.b.c=null; must not exploit first assign. */
     public void testPropAssignChainSafety() {
         String js = "a.b=null;a.b.c=null";
         assertEquals(js, process(js));
     }

     // ---- Return / conditional contexts ----

     /** The collapse may happen inside a return statement. */
     public void testReturnContextCollapse() {
         assertEquals("return b=3,a=b", process("var b=3;return b=3"));
     }

     // ---- helper ----

     /**
      * Parses {@code js}, runs {@link ExploitAssigns} on the resulting AST,
      * and returns the source produced by pretty-printing the modified tree.
      */
     private String process(String js) {
         Compiler compiler = new Compiler();
         compiler.init(ImmutableList.<SourceFile>of(),
                       ImmutableList.<SourceFile>of(),
                       new CompilerOptions());
         Node root = compiler.parse(SourceFile.fromCode("testcode", js));
         if (root == null) {
             throw new RuntimeException("Parse error in test input: " + js);
         }
         ExploitAssigns pass = new ExploitAssigns();
         NodeTraversal.traverse(compiler, root, pass);
         return compiler.toSource(root);
     }
 }