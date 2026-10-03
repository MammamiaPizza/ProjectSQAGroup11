package com.google.javascript.jscomp.parsing;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import com.google.javascript.jscomp.mozilla.rhino.ErrorReporter;
 import com.google.javascript.jscomp.mozilla.rhino.EvaluatorException;
 import com.google.javascript.jscomp.mozilla.rhino.Parser;
 import com.google.javascript.jscomp.mozilla.rhino.ast.AstRoot;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 /**
  * Tests for IRFactory handling of function nodes, focusing on the unnamed function statement
  * bug (#251). The faulty behavior is that an unnamed function statement produces a FUNCTION node
  * without a NAME child. After the fix, the NAME child is present and contains an empty string.
  */
 public class IRFactoryTest {

   private static final Config CONFIG;
   private static final ErrorReporter ERROR_REPORTER = new ErrorReporter() {
     @Override public void warning(String msg, String src, int line, String lineSrc, int off) {}
     @Override public void error(String msg, String src, int line, String lineSrc, int off) {}
     @Override public EvaluatorException runtimeError(String msg, String src, int line,
                                                       String lineSrc, int off) {
       return new EvaluatorException(msg);
     }
   };

   static {
     CONFIG = new Config();
     CONFIG.acceptConstKeyword = false; // not relevant for these tests
   }

   private static Node parseAndTransform(String source) {
     AstRoot astRoot = Parser.parse(source, "test", 1);
     return IRFactory.transformTree(astRoot, source, CONFIG, ERROR_REPORTER);
   }

   @Test
   public void testUnnamedFunctionStatement() {
     // Bug #251: unnamed function statement should produce a FUNCTION node with an
     // empty NAME child. Before the fix, the NAME child was missing.
     Node root = parseAndTransform("function() {}");
     assertEquals("SCRIPT should have one child", 1, root.getChildCount());
     Node exprResult = root.getFirstChild();
     assertEquals(Token.EXPR_RESULT, exprResult.getType());
     Node func = exprResult.getFirstChild();
     assertEquals(Token.FUNCTION, func.getType());
     Node name = func.getFirstChild();
     assertNotNull("FUNCTION node must have a NAME child", name);
     assertEquals(Token.NAME, name.getType());
     assertEquals("", name.getString());
   }

   @Test
   public void testNamedFunctionStatement() {
     Node root = parseAndTransform("function foo() {}");
     assertEquals(1, root.getChildCount());
     Node func = root.getFirstChild();
     assertEquals(Token.FUNCTION, func.getType());
     Node name = func.getFirstChild();
     assertNotNull(name);
     assertEquals(Token.NAME, name.getType());
     assertEquals("foo", name.getString());
   }

   @Test
   public void testUnnamedFunctionExpression() {
     Node root = parseAndTransform("var a = function() {};");
     Node var = root.getFirstChild();
     assertEquals(Token.VAR, var.getType());
     assertEquals(2, var.getChildCount());
     Node varName = var.getFirstChild();
     assertEquals(Token.NAME, varName.getType());
     assertEquals("a", varName.getString());
     Node func = varName.getNext();
     assertEquals(Token.FUNCTION, func.getType());
     Node funcName = func.getFirstChild();
     assertNotNull("function expression must have an empty NAME", funcName);
     assertEquals(Token.NAME, funcName.getType());
     assertEquals("", funcName.getString());
   }

   @Test
   public void testNamedFunctionExpression() {
     Node root = parseAndTransform("var f = function bar() { return 1; };");
     Node var = root.getFirstChild();
     Node func = var.getFirstChild().getNext();
     assertEquals(Token.FUNCTION, func.getType());
     Node funcName = func.getFirstChild();
     assertEquals(Token.NAME, funcName.getType());
     assertEquals("bar", funcName.getString());
   }

   @Test
   public void testFunctionWithParameters() {
     Node root = parseAndTransform("function sum(a, b) { return a + b; }");
     Node func = root.getFirstChild();
     assertEquals(Token.FUNCTION, func.getType());
     assertEquals("sum", func.getFirstChild().getString());
     Node params = func.getFirstChild().getNext();
     assertNotNull(params);
     assertEquals(Token.PARAM_LIST, params.getType());
     assertEquals(2, params.getChildCount());
     assertEquals("a", params.getFirstChild().getString());
     assertEquals("b", params.getFirstChild().getNext().getString());
   }

   @Test
   public void testEmptyFunctionBody() {
     Node root = parseAndTransform("function empty() {}");
     Node func = root.getFirstChild();
     Node body = func.getLastChild();
     assertEquals(Token.BLOCK, body.getType());
     assertTrue(body.getChildCount() == 0);
   }

   @Test
   public void testReturnStatement() {
     Node root = parseAndTransform("function f() { return 42; }");
     Node func = root.getFirstChild();
     Node ret = func.getLastChild().getFirstChild();
     assertEquals(Token.RETURN, ret.getType());
     assertEquals(42.0, ret.getFirstChild().getDouble(), 0.001);
   }

   @Test
   public void testMultipleFunctionStatements() {
     Node root = parseAndTransform("function a() {} function b() {}");
     assertEquals(2, root.getChildCount());
     assertEquals("a", root.getFirstChild().getFirstChild().getString());
     assertEquals("b", root.getLastChild().getFirstChild().getString());
   }

   @Test
   public void testNestedFunction() {
     Node root = parseAndTransform("function outer() { function inner() {} }");
     Node outer = root.getFirstChild();
     assertEquals(Token.FUNCTION, outer.getType());
     assertEquals("outer", outer.getFirstChild().getString());
     Node outerBody = outer.getLastChild();
     Node inner = outerBody.getFirstChild();
     assertEquals(Token.FUNCTION, inner.getType());
     assertEquals("inner", inner.getFirstChild().getString());
   }
 }
