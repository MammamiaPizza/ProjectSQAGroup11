package com.google.javascript.jscomp.parsing;

 import junit.framework.TestCase;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.parsing.Config.LanguageMode;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.head.ErrorReporter;
 import com.google.javascript.rhino.head.ast.AstRoot;

 /**
  * Tests for the bug related to for-each loop transformation in {@link IRFactory}.
  */
 public class IRFactoryForEachBugTest extends TestCase {

   private Compiler compiler;
   private Config config;
   private ErrorReporter errorReporter;
   private Parser parser;

   @Override
   protected void setUp() throws Exception {
     compiler = new Compiler();
     compiler.initOptions(new CompilerOptions());
     config = new Config(LanguageMode.ECMASCRIPT3, false);
     errorReporter = new com.google.javascript.jscomp.CompilerErrorReporter(compiler);
     parser = new Parser(config, errorReporter);
   }

   private Node parse(String js) throws Exception {
     AstRoot ast = parser.parse(js, null, 1);
     return IRFactory.transformTree(ast, null, js, config, errorReporter);
   }

   private Node getFirstStatement(String js) throws Exception {
     Node script = parse(js);
     return script.getFirstChild();
   }

   public void testForEachBasicStructure() throws Exception {
     Node forEachNode = getFirstStatement("for each (var a in b) { c(); }");
     assertEquals("Expected FOR_IN token", Token.FOR_IN, forEachNode.getType());
     assertEquals("Expected 4 children for for-each", 4, forEachNode.getChildCount());
     Node each = forEachNode.getFirstChild();
     assertEquals("each", each.getString());
     Node iter = each.getNext();
     assertEquals(Token.VAR, iter.getType());
     Node obj = iter.getNext();
     assertEquals(Token.NAME, obj.getType());
     assertEquals("b", obj.getString());
     Node body = obj.getNext();
     assertEquals(Token.BLOCK, body.getType());
   }

   public void testForEachHasEachChild() throws Exception {
     Node forEachNode = getFirstStatement("for each (a in b) { }");
     assertEquals(Token.FOR_IN, forEachNode.getType());
     assertTrue("for-each node must have an 'each' child", forEachNode.getChildCount() == 4);
     Node eachChild = forEachNode.getFirstChild();
     assertNotNull("First child should represent 'each' keyword", eachChild);
     assertEquals("each", eachChild.getString());
   }

   public void testForInWithoutEach() throws Exception {
     Node forInNode = getFirstStatement("for (var a in b) { }");
     assertEquals(Token.FOR_IN, forInNode.getType());
     assertEquals("Plain for-in should have exactly 3 children", 3, forInNode.getChildCount());
     Node first = forInNode.getFirstChild();
     assertFalse("First child of plain for-in must not be 'each'",
"each".equals(first.getString()));
   }

   public void testForEachNested() throws Exception {
     Node outer = getFirstStatement(
         "for each (var i in obj) { for each (var j in i) { k(); } }");
     assertEquals(Token.FOR_IN, outer.getType());
     assertEquals(4, outer.getChildCount());
     Node outerBody = outer.getFirstChild().getNext().getNext().getNext();
     assertEquals(Token.BLOCK, outerBody.getType());
     Node inner = outerBody.getFirstChild();
     assertEquals(Token.FOR_IN, inner.getType());
     assertEquals(4, inner.getChildCount());
     assertEquals("each", inner.getFirstChild().getString());
   }

   public void testForEachInsideFor() throws Exception {
     Node outer = getFirstStatement("for (;;) { for each (x in y) z(); }");
     assertEquals(Token.FOR, outer.getType());
     Node outerBody = outer.getFirstChild().getNext().getNext(); // body is third child of FOR
     Node innerForEach = outerBody.getFirstChild();
     assertEquals("Inner statement should be a for-each loop",
         Token.FOR_IN, innerForEach.getType());
     assertEquals(4, innerForEach.getChildCount());
     assertEquals("each", innerForEach.getFirstChild().getString());
   }

   public void testForEachWithLabel() throws Exception {
     Node labeled = getFirstStatement("lbl: for each (a in b) { break lbl; }");
     assertEquals(Token.LABEL, labeled.getType());
     Node forEachNode = labeled.getLastChild();
     assertEquals(Token.FOR_IN, forEachNode.getType());
     assertEquals("each", forEachNode.getFirstChild().getString());
   }

   public void testForEachEmptyBody() throws Exception {
     Node forEachNode = getFirstStatement("for each (a in b);");
     assertEquals(Token.FOR_IN, forEachNode.getType());
     assertEquals(4, forEachNode.getChildCount());
     Node body = forEachNode.getFirstChild().getNext().getNext().getNext();
     assertEquals(Token.EMPTY, body.getType());
   }

   public void testForEachJsDoc() throws Exception {
     Node forEachNode = getFirstStatement("/** @type {number} */ for each (a in b) {}");
     assertEquals(Token.FOR_IN, forEachNode.getType());
     assertNotNull("JSDoc should be attached to the for-each node",
         forEachNode.getJSDocInfo());
   }

   public void testForEachMissingInReportsError() throws Exception {
     parse("for each (a) {}");
     assertTrue("Parser should report an error for missing 'in'",
         compiler.getErrorCount() > 0);
   }

   public void testForEachMissingBodyReportsError() throws Exception {
     parse("for each (a in b)");
     assertTrue("Parser should report an error for missing body",
         compiler.getErrorCount() > 0);
   }

   public void testEachStringNotTreatedAsDirective() throws Exception {
     Node script = parse("'each'; for each (a in b) {}");
     Node first = script.getFirstChild();
     assertEquals("String 'each' should remain as an expression statement",
         Token.EXPR_RESULT, first.getType());
     Node stringNode = first.getFirstChild();
     assertEquals(Token.STRING, stringNode.getType());
     assertEquals("each", stringNode.getString());
     Node second = first.getNext();
     assertEquals("Second statement should be the for-each loop",
         Token.FOR_IN, second.getType());
   }

   public void testUseStrictDirectiveWorks() throws Exception {
     Node script = parse("'use strict'; for each (a in b) {}");
     Node first = script.getFirstChild();
     assertEquals("After directive removal, first statement should be the for-each loop",
         Token.FOR_IN, first.getType());
     assertEquals("each", first.getFirstChild().getString());
   }
 }