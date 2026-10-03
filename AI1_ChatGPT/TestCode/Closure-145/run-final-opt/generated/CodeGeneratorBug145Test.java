package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class CodeGeneratorBug145Test {

  @Test
  public void testIfBodyKeepsBracesAroundLabeledFunction() {
    Node function = new Node(Token.FUNCTION);
    function.addChildToBack(Node.newString(Token.NAME, "goo"));
    function.addChildToBack(new Node(Token.PARAM_LIST));

    Node functionBody = new Node(Token.BLOCK);
    functionBody.addChildToBack(new Node(Token.RETURN, new Node(Token.TRUE)));
    function.addChildToBack(functionBody);

    Node label = new Node(Token.LABEL);
    label.addChildToBack(Node.newString(Token.LABEL_NAME, "A"));
    label.addChildToBack(function);

    Node body = new Node(Token.BLOCK);
    body.addChildToBack(label);

    Node ifNode = new Node(Token.IF);
    ifNode.addChildToBack(Node.newString(Token.NAME, "e1"));
    ifNode.addChildToBack(body);

    assertEquals(
        "if(e1){A:function goo(){return true}}",
        new CodePrinter.Builder(ifNode).build());
  }

  @Test
  public void testIfBodyKeepsBracesAroundLabeledDoLoop() {
    Node doBody = new Node(Token.EXPR_RESULT, Node.newString(Token.NAME, "foo"));

    Node doNode = new Node(Token.DO);
    doNode.addChildToBack(doBody);
    doNode.addChildToBack(Node.newString(Token.NAME, "y"));

    Node label = new Node(Token.LABEL);
    label.addChildToBack(Node.newString(Token.LABEL_NAME, "A"));
    label.addChildToBack(doNode);

    Node body = new Node(Token.BLOCK);
    body.addChildToBack(label);

    Node ifNode = new Node(Token.IF);
    ifNode.addChildToBack(Node.newString(Token.NAME, "x"));
    ifNode.addChildToBack(body);

    assertEquals(
        "if(x){A:do foo();while(y)}",
        new CodePrinter.Builder(ifNode).build());
  }
}
