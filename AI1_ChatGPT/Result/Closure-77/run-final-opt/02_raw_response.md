package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class CodeGeneratorNulStringTest {

  @Test
  public void testNulCharacterIsPrintedUsingZeroEscape() {
    Node name = Node.newString(Token.NAME, "x");
    name.addChildToBack(Node.newString(Token.STRING, "\u0000"));

    Node declaration = new Node(Token.VAR);
    declaration.addChildToBack(name);

    assertEquals("var x=\"\\0\"", new CodePrinter.Builder(declaration).build());
  }
}