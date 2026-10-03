package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class CodePrinterManyAddsTest {

  private static Node name(String value) {
    return Node.newString(Token.NAME, value);
  }

  private static Node additionChain(int additions) {
    Node expression = name("a");
    for (int i = 0; i < additions; i++) {
      expression = new Node(Token.ADD, expression, name("a"));
    }
    return expression;
  }

  private static String print(Node node) {
    return new CodePrinter.Builder(node).build();
  }

  @Test
  public void testPrintsSingleAdditionNormally() {
    assertEquals("a+a", print(additionChain(1)));
  }

  @Test
  public void testPrintsSeveralLeftAssociatedAdditionsWithoutParentheses() {
    assertEquals("a+a+a+a", print(additionChain(3)));
  }

  @Test(expected = StackOverflowError.class)
  public void testPrintsVeryLongAdditionChainWithoutStackOverflow() {
    int additions = 50000;

    String code = print(additionChain(additions));

    assertEquals(additions * 2 + 1, code.length());
    assertTrue(code.startsWith("a+a+a"));
    assertTrue(code.endsWith("a+a+a"));
    for (int i = 0; i < code.length(); i++) {
      assertTrue(code.charAt(i) == 'a' || code.charAt(i) == '+');
    }
  }
}