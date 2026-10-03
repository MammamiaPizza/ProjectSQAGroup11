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

@org.junit.Test
public void testBinaryDefineValueRequiresValidLeftOperand() {
  com.google.javascript.rhino.Node value =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.ADD,
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.NAME, "UNKNOWN_DEFINE"),
          com.google.javascript.rhino.Node.newString("suffix"));

  assertFalse(NodeUtil.isValidDefineValue(value, java.util.Collections.<String>emptySet()));
}

@org.junit.Test
public void testUnaryDefineValueWithKnownNameIsValid() {
  com.google.javascript.rhino.Node value =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.NOT,
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.NAME, "BOOLEAN_DEFINE"));

  assertTrue(
      NodeUtil.isValidDefineValue(
          value, java.util.Collections.singleton("BOOLEAN_DEFINE")));
}

@org.junit.Test
public void testStringConcatenationWithQualifiedDefinedNameIsValid() {
  com.google.javascript.rhino.Node qualifiedName =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.GETPROP,
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.NAME, "defines"),
          com.google.javascript.rhino.Node.newString("STRING_DEFINE"));
  com.google.javascript.rhino.Node value =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.ADD,
          com.google.javascript.rhino.Node.newString("prefix"),
          qualifiedName);

  assertTrue(
      NodeUtil.isValidDefineValue(
          value, java.util.Collections.singleton("defines.STRING_DEFINE")));
}
}
