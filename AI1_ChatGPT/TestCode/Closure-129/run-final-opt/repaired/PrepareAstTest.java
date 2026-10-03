package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class PrepareAstTest {

  private static Node call(Node callee) {
    return new Node(Token.CALL, callee);
  }

  private static Node script(Node child) {
    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(child);
    return script;
  }

  @Test
  public void processAnnotatesFreeAndDirectEvalCallsInExterns() {
    Node evalName = Node.newString(Token.NAME, "eval");
    Node externCall = call(evalName);

    new PrepareAst(new Compiler()).process(script(externCall), null);

    assertTrue(externCall.getBooleanProp(Node.FREE_CALL));
    assertTrue(evalName.getBooleanProp(Node.DIRECT_EVAL));
  }

  @Test
  public void processDoesNotMarkPropertyCallsAsFree() {
    Node getProp =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "receiver"),
            Node.newString(Token.STRING, "method"));
    Node propertyCall = call(getProp);

    new PrepareAst(new Compiler()).process(null, script(propertyCall));

    assertFalse(propertyCall.getBooleanProp(Node.FREE_CALL));
  }

  @Test
  public void processMarksOrdinaryNameCallsAsFreeWithoutDirectEvalAnnotation() {
    Node functionName = Node.newString(Token.NAME, "invoke");
    Node ordinaryCall = call(functionName);

    new PrepareAst(new Compiler()).process(null, script(ordinaryCall));

    assertTrue(ordinaryCall.getBooleanProp(Node.FREE_CALL));
    assertFalse(functionName.getBooleanProp(Node.DIRECT_EVAL));
  }

  @Test
  public void processIgnoresCastsWhenDeterminingDirectEvalAndFreeCall() {
    Node evalName = Node.newString(Token.NAME, "eval");
    Node castEval = new Node(Token.CAST, new Node(Token.CAST, evalName));
    Node evalCall = call(castEval);

    new PrepareAst(new Compiler()).process(null, script(evalCall));

    assertTrue(evalCall.getBooleanProp(Node.FREE_CALL));
    assertTrue(evalName.getBooleanProp(Node.DIRECT_EVAL));
  }

  @Test
  public void processIgnoresCastsAroundPropertyCallsWhenDeterminingThisContext() {
    Node property =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "receiver"),
            Node.newString(Token.STRING, "method"));
    Node castProperty = new Node(Token.CAST, property);
    Node propertyCall = call(castProperty);

    new PrepareAst(new Compiler()).process(null, script(propertyCall));

    assertFalse(propertyCall.getBooleanProp(Node.FREE_CALL));
  }
}
