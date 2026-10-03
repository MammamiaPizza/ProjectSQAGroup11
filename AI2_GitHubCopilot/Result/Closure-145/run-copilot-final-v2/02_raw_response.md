package com.google.javascript.jscomp;

import junit.framework.TestCase;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

public class CodeGeneratorTest extends TestCase {

  /** Consumer that captures generated code to a StringBuilder. */ private static class TestConsumer
extends CodeConsumer {
    private final StringBuilder sb = new StringBuilder();

 // Constructor
 TestConsumer() {
   super(null, false); // avoids NPE in some implementations
 }

 @Override
 void add(String newcode) {
   sb.append(newcode);
 }

 @Override
 void beginBlock() { sb.append("{"); }

 @Override
 void endBlock(boolean unused) { sb.append("}"); }

 @Override
 void endStatement(boolean unused) { sb.append(";"); }

 @Override
 void maybeLineBreak() { /* ignore */ }

 @Override
 void endFunction(boolean unused) { }

 @Override
 boolean shouldPreserveExtraBlocks() { return false; }

 @Override
 boolean continueProcessing() { return true; }

 @Override
 void startSourceMapping(Node node) { }

 @Override
 void listSeparator() { sb.append(","); }

 @Override
 void beginCaseBody() { }

 @Override
 void endCaseBody() { }

 @Override
 void notePreferredLineBreak() { }

 @Override
 void addOp(String op, boolean word) { sb.append(op); }

 String getOutput() { return sb.toString(); } }

  /** Consumer that forces preservation of extra blocks. */ private static class PreservingConsumer
extends TestConsumer {
    @Override
    boolean shouldPreserveExtraBlocks() { return true; } }

  // ---- helpers to build AST snippets ----

  private Node name(String ident) {
    Node n = new Node(Token.NAME);
    n.setString(ident);
    return n; }

  private Node block(Node... children) {
    Node b = new Node(Token.BLOCK);
    for (Node c : children) {
      b.addChildToBack(c);
    }
    return b; }

  private Node label(String label, Node stmt) {
    Node lab = new Node(Token.LABEL);
    lab.addChildToBack(name(label));
    lab.addChildToBack(stmt);
    return lab; }

  private Node functionNode(String name, Node body) {
    Node func = new Node(Token.FUNCTION);
    func.addChildToBack(name(name));
    Node params = new Node(Token.PARAMLIST);
    func.addChildToBack(params);
    func.addChildToBack(body);
    return func; }

  private Node doNode(Node body, Node cond) {
    Node doNode = new Node(Token.DO);
    doNode.addChildToBack(body);
    doNode.addChildToBack(cond);
    return doNode; }

  private Node ifNode(Node cond, Node body) {
    Node iff = new Node(Token.IF);
    iff.addChildToBack(cond);
    iff.addChildToBack(body);
    // else is optional; skip for these tests
    return iff; }

  private Node simpleExpr(String name) {
    return name(name); // a simple variable reference }

  // ---- tests ----

  public void testFunctionLabeledInBlockPreserved_Compat() {
    // if(e1){A:function goo(){return true}}
    // The block must not be stripped even when extra blocks are not forced,
    // because the sole child is a LABEL that wraps a FUNCTION.
    Node func = functionNode("goo",
        block(Node.newReturn(simpleExpr("true"))));
    Node labelled = label("A", func);
    Node bodyBlock = block(labelled);
    Node cond = simpleExpr("e1");
    Node ifStmt = ifNode(cond, bodyBlock);

 TestConsumer consumer = new TestConsumer(); // shouldPreserveExtraBlocks = false
 CodeGenerator cg = new CodeGenerator(consumer);
 cg.add(ifStmt, CodeGenerator.Context.STATEMENT);

 String output = consumer.getOutput();
 assertTrue("Block braces missing; expected {...} around labeled function",
     output.contains("{A:"));
 assertTrue("Block braces missing; expected ...}", output.contains("}")); }

  public void testDoLabeledInBlockPreserved_Compat() {
    // if(x){A:do foo();while(y)}
    Node doStmt = doNode(block(simpleExpr("foo")), simpleExpr("y"));
    Node labelled = label("A", doStmt);
    Node bodyBlock = block(labelled);
    Node cond = simpleExpr("x");
    Node ifStmt = ifNode(cond, bodyBlock);

 TestConsumer consumer = new TestConsumer(); // shouldPreserveExtraBlocks = false
 CodeGenerator cg = new CodeGenerator(consumer);
 cg.add(ifStmt, CodeGenerator.Context.STATEMENT);

 String output = consumer.getOutput();
 assertTrue("Block braces missing; expected {...} around labeled do-while",
     output.contains("{A:"));
 assertTrue("Block braces missing; expected ...}", output.contains("}")); }

  public void testBlockWithMultipleChildrenAlwaysPreserved() {
    // if(e1){x;y}  -> must have braces because more than one child
    Node stmt1 = simpleExpr("x");
    Node stmt2 = simpleExpr("y");
    Node bodyBlock = block(stmt1, stmt2);
    Node cond = simpleExpr("e1");
    Node ifStmt = ifNode(cond, bodyBlock);

 TestConsumer consumer = new TestConsumer(); // false
 CodeGenerator cg = new CodeGenerator(consumer);
 cg.add(ifStmt, CodeGenerator.Context.STATEMENT);

 String output = consumer.getOutput();
 assertTrue("Block with multiple children should be preserved",
     output.contains("{"));
 assertTrue("Block with multiple children should be preserved",
     output.contains("}")); }

  public void testBlockWithSingleNonSpecialChildStripped() {
    // if(e1) x;   no braces because only child is a simple statement
    Node stmt = simpleExpr("x");
    Node bodyBlock = block(stmt);
    Node cond = simpleExpr("e1");
    Node ifStmt = ifNode(cond, bodyBlock);

 TestConsumer consumer = new TestConsumer(); // false
 CodeGenerator cg = new CodeGenerator(consumer);
 cg.add(ifStmt, CodeGenerator.Context.STATEMENT);

 String output = consumer.getOutput();
 assertFalse("Block with single non-function/non-do child should be stripped",
     output.contains("{")); }

  public void testExtraBlocksPreserveBlockWithSingleNonSpecial() {
    // shouldPreserveExtraBlocks = true -> always keep braces
    Node stmt = simpleExpr("x");
    Node bodyBlock = block(stmt);
    Node cond = simpleExpr("e1");
    Node ifStmt = ifNode(cond, bodyBlock);

 PreservingConsumer consumer = new PreservingConsumer(); // returns true
 CodeGenerator cg = new CodeGenerator(consumer);
 cg.add(ifStmt, CodeGenerator.Context.STATEMENT);

 String output = consumer.getOutput();
 assertTrue("Extra blocks forced: braces must remain",
     output.contains("{"));
 assertTrue("Extra blocks forced: closing brace expected", output.contains("}")); }

}