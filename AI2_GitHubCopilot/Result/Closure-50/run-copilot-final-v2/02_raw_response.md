package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**

 - Tests for PeepholeReplaceKnownMethods focusing on array join folding.
  */
 public class PeepholeReplaceKnownMethodsBugTest {
  /**
  - Runs PeepholeReplaceKnownMethods on the given JavaScript and returns
  - the printed AST after normalization.
    */
   private void testOptimization(String input, String expectedCode) {
   Compiler compiler = new Compiler();
   Node root = compiler.parseTestCode(input);
   if (root == null) {
   throw new AssertionError("Failed to parse input: " + input);
   }
   Node externs = new Node(Token.BLOCK);
   List<AbstractPeepholeOptimization> passes = new ArrayList<>();
   passes.add(new PeepholeReplaceKnownMethods());
   new PeepholeOptimizationsPass(passes).process(externs, root);
   String actual = compiler.toSource(root);
   Compiler expectedCompiler = new Compiler();
   Node expectedRoot = expectedCompiler.parseTestCode(expectedCode);
   if (expectedRoot == null) {
   throw new AssertionError("Failed to parse expected: " + expectedCode);
   }
   String expected = expectedCompiler.toSource(expectedRoot);
   assertEquals(expected, actual);
  }
  // ------------------- Basic folding -------------------
  @Test
  public void testSimpleJoinEmptySeparator() {
      testOptimization("var a = [1,2,3].join("")", "var a = "123"");
  }
  @Test
  public void testSimpleJoinNonEmptySeparator() {
      testOptimization("var a = [1,2,3].join("-")", "var a = "1-2-3"");
  }
  @Test
  public void testArrayJoinDefaultSeparator() {
      // join() uses default separator ","
      testOptimization("var a = [1,2].join()", "var a = "1,2"");
  }
  @Test
  public void testEmptyArrayJoin() {
      testOptimization("var a = [].join(",")", "var a = """);
  }
  @Test
  public void testSingleElementArrayJoin() {
      testOptimization("var a = ["a"].join("-")", "var a = "a"");
  }
  // ------------------- Join in addition contexts -------------------
  @Test
  public void testStringJoinAdd_EmptySepLeft() {
      testOptimization("var a = "x" + [1,2].join("")",
              "var a = "x" + "12"");
  }
  @Test
  public void testStringJoinAdd_EmptySepRight() {
      testOptimization("var a = [1,2].join("") + "x"",
              "var a = "12" + "x"");
  }
  @Test
  public void testStringJoinAdd_NonEmptySep() {
      testOptimization("var a = "x" + [1,2].join("-")",
              "var a = "x" + "1-2"");
  }
  @Test
  public void testStringJoinAdd_Chain() {
      testOptimization("var a = "x" + [1,2].join("") + "y"",
              "var a = "x" + "12" + "y"");
  }
  // ------------------- Folding should NOT happen -------------------
  @Test
  public void testNoStringJoin_VariableArray() {
      // A variable reference should not be resolved to a literal
      testOptimization("var a = b.join("")", "var a = b.join("")");
  }
  @Test
  public void testNoStringJoin_NonLiteralArray() {
      // Same pattern with a more complex identifier
      testOptimization("var a = obj.arr.join("-")",
              "var a = obj.arr.join("-")");
  }
  @Test
  public void testNoStringJoin_OutOfBoundsIndex() {
      // charAt with out-of-bounds index should not be folded
      // (exercises a different path, validates the general principle)
      testOptimization("var a = "hello".charAt(10)",
              "var a = "hello".charAt(10)");
  }

}