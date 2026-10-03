package com.google.javascript.jscomp;

 import org.junit.Before;
 import org.junit.Test;
 import static org.junit.Assert.*;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 /**
  * Unit tests for {@link Normalize} covering split-var, move-named-functions,
  * remove-duplicate-declarations, label normalization, for-initializer extraction,
  * and the Issue 115 regression.
  */
 public class NormalizeTest {

   private Compiler compiler;
   private CompilerOptions options;

   @Before
   public void setUp() {
     compiler = new Compiler();
     options = new CompilerOptions();
   }

   /**
    * Parses the given JavaScript, applies the Normalize pass, and returns the
    * resulting source text.
    */
   private String normalize(String js) {
     compiler.init(options);

     compiler.compile(
         SourceFile.fromCode("externs.js", ""),
         SourceFile.fromCode("input.js", js),
         options);

     Node root = compiler.getRoot();
     Node jsNode = root.getLastChild();
     Node externsNode = jsNode.getPrevious();

     Normalize normalizePass = new Normalize(compiler, false);
     normalizePass.process(externsNode, jsNode);

     return compiler.toSource();
   }

   @Test
   public void testSplitVarDeclarations() {
     String result = normalize("function f(){var x=1,y=2;}");
     assertTrue("Multi-var should be split: " + result,
         result.contains("var x") && result.contains("var y"));
     // The two VARs should be separate statements.
     int firstVar = result.indexOf("var ");
     int lastVar = result.lastIndexOf("var ");
     assertTrue("Should produce separate VAR nodes: " + result, firstVar != lastVar);
   }

   @Test
   public void testRemoveDuplicateVarDeclarations() {
     String result = normalize("function f(){var a=1;var a=2;}");
     int first = result.indexOf("var a");
     int last = result.lastIndexOf("var a");
     assertEquals("Duplicate var 'a' must be removed: " + result, first, last);
   }

   @Test
   public void testMoveNamedFunctionsBeforeStatements() {
     String result = normalize(
         "function f(){var x=1;if(true){function g(){}}function h(){}}");
     int gIdx = result.indexOf("function g()");
     int hIdx = result.indexOf("function h()");
     int ifIdx = result.indexOf("if(true)");
     int varXIdx = result.indexOf("var x");
     assertTrue("g moved before if: " + result, gIdx < ifIdx);
     assertTrue("h moved before var x: " + result, hIdx < varXIdx);
   }

   @Test
   public void testNormalizeLabelsWrapsNonBlock() {
     String result = normalize("function f(){L:var x=1;}");
     assertTrue("Label L preserved: " + result, result.contains("L:"));
     // A non-block statement after a label must be wrapped in a block.
     assertTrue("Wrapped in block: " + result,
         result.contains("{") && result.contains("}"));
   }

   @Test
   public void testIssue115DuplicateGlobalVar() {
     // Regression test for Issue 115: duplicate var declarations in the same scope.
     String result = normalize("var x=1;var x=2;");
     int first = result.indexOf("var x");
     int last = result.lastIndexOf("var x");
     assertEquals("Duplicate global var must be removed: " + result, first, last);
   }

   @Test
   public void testFunctionDeclarationsMovedFromWhile() {
     String result = normalize("function f(){while(true){function g(){}}return g();}");
     int gIdx = result.indexOf("function g()");
     int whileIdx = result.indexOf("while");
     assertTrue("g moved before while: " + result, gIdx >= 0 && gIdx < whileIdx);
   }

   @Test
   public void testSingleVarDeclarationUnchanged() {
     String result = normalize("function f(){var x=1;return x;}");
     assertTrue("Single var preserved: " + result, result.contains("var x=1"));
   }

   @Test
   public void testAssertOnChangeThrowsOnCodeChange() {
     compiler.init(options);
     compiler.compile(
         SourceFile.fromCode("externs.js", ""),
         SourceFile.fromCode("input.js", "function f(){var x=1,y=2;}"),
         options);

     Node root = compiler.getRoot();
     Node jsNode = root.getLastChild();
     Node externsNode = jsNode.getPrevious();

     Normalize strictNormalize = new Normalize(compiler, true);
     try {
       strictNormalize.process(externsNode, jsNode);
       fail("assertOnChange=true must throw on AST modification");
     } catch (IllegalStateException expected) {
       // expected path – Normalize changes trigger the assertion
     }
   }

   @Test
   public void testNestedBlockFunctionHoisting() {
     String result = normalize(
         "function f(){if(true){if(false){function g(){}}}}");
     int gIdx = result.indexOf("function g()");
     int ifIdx = result.indexOf("if(true)");
     assertTrue("Nested g hoisted above outer if: " + result, gIdx < ifIdx);
   }

   @Test
   public void testMultipleDuplicateDeclarations() {
     String result = normalize(
         "function f(){var x=1;var y=2;var x=3;var y=4;}");
     int xFirst = result.indexOf("var x");
     int xLast = result.lastIndexOf("var x");
     assertEquals("Only one var x: " + result, xFirst, xLast);
     int yFirst = result.indexOf("var y");
     int yLast = result.lastIndexOf("var y");
     assertEquals("Only one var y: " + result, yFirst, yLast);
   }

   @Test
   public void testEmptyFunctionBody() {
     String result = normalize("function f(){}");
     assertNotNull("Empty function handled", result);
     assertTrue("Function name preserved: " + result, result.contains("function f()"));
   }

   @Test
   public void testLabelWithBlockAlreadyUnchanged() {
     String result = normalize("function f(){L:{var x=1;}}");
     assertTrue("Label L preserved: " + result, result.contains("L:"));
     // Should not double-wrap – the block already exists.
     int blockCount = 0;
     for (int i = 0; i < result.length(); i++) {
       if (result.charAt(i) == '{') blockCount++;
     }
     assertEquals("Should have exactly 2 blocks: " + result, 2, blockCount);
   }
 }