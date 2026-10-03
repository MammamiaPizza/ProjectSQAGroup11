package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeUtilDefineValueRegressionTest {

  private static Set<String> defines(String... names) {
    Set<String> result = new HashSet<String>();
    for (String name : names) {
      result.add(name);
    }
    return result;
  }

  @Test
  public void testLiteralDefineValuesAreValid() {
    Set<String> knownDefines = defines();

    assertTrue(NodeUtil.isValidDefineValue(Node.newString("value"), knownDefines));
    assertTrue(NodeUtil.isValidDefineValue(Node.newNumber(1), knownDefines));
    assertTrue(NodeUtil.isValidDefineValue(new Node(Token.TRUE), knownDefines));
    assertTrue(NodeUtil.isValidDefineValue(new Node(Token.FALSE), knownDefines));
  }

  @Test
  public void testStringConcatenationIsValidDefineValue() {
    Node value =
        new Node(
            Token.ADD,
            Node.newString("prefix-"),
            Node.newString("suffix"));

    assertTrue(NodeUtil.isValidDefineValue(value, defines()));
  }

  @Test
  public void testStringConcatenationWithDefinedNameIsValid() {
    Node value =
        new Node(
            Token.ADD,
            NodeUtil.newQualifiedNameNode("STRING_DEFINE", 1, 0),
            Node.newString("-override"));

    assertTrue(NodeUtil.isValidDefineValue(value, defines("STRING_DEFINE")));
  }

  @Test
  public void testBinaryDefineValueRequiresValidRightOperand() {
    Node invalidRightOperand =
        new Node(
            Token.CALL,
            Node.newString(Token.NAME, "getValue"));
    Node value =
        new Node(
            Token.BITAND,
            Node.newNumber(1),
            invalidRightOperand);

    assertFalse(NodeUtil.isValidDefineValue(value, defines()));
  }

  @Test
  public void testKnownQualifiedDefineNameIsValid() {
    Node value = NodeUtil.newQualifiedNameNode("config.FEATURE_ENABLED", 1, 0);

    assertTrue(
        NodeUtil.isValidDefineValue(
            value, defines("config.FEATURE_ENABLED")));
  }

  @Test
  public void testUnknownDefineNameIsInvalid() {
    Node value = NodeUtil.newQualifiedNameNode("UNKNOWN_DEFINE", 1, 0);

    assertFalse(NodeUtil.isValidDefineValue(value, defines("OTHER_DEFINE")));
  }

  @Test
  public void testUnaryOperatorRequiresValidOperand() {
    Node invalidOperand =
        new Node(
            Token.CALL,
            Node.newString(Token.NAME, "compute"));
    Node value = new Node(Token.NEG, invalidOperand);

    assertFalse(NodeUtil.isValidDefineValue(value, defines()));
  }
}
