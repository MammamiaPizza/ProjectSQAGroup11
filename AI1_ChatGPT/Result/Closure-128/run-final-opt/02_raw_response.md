package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class CodeGeneratorIssue942Test {

  @Test
  public void testQuotedZeroPropertyKeyIsPrintedAsNumericKey() {
    assertEquals("var x={0:1}", printObjectLiteralWithQuotedKey("0"));
  }

  @Test
  public void testQuotedMultiDigitPropertyKeyIsPrintedAsNumericKey() {
    assertEquals("var x={123:1}", printObjectLiteralWithQuotedKey("123"));
  }

  @Test
  public void testLeadingZeroPropertyKeyRemainsQuoted() {
    assertEquals("var x={\"01\":1}", printObjectLiteralWithQuotedKey("01"));
  }

  @Test
  public void testNonNumericPropertyKeyRemainsQuoted() {
    assertEquals("var x={\"-1\":1}", printObjectLiteralWithQuotedKey("-1"));
  }

  @Test
  public void testSimpleNumberClassificationIncludesZeroButRejectsLeadingZeros() {
    assertTrue(CodeGenerator.isSimpleNumber("0"));
    assertTrue(CodeGenerator.isSimpleNumber("9"));
    assertTrue(CodeGenerator.isSimpleNumber("10"));
    assertFalse(CodeGenerator.isSimpleNumber(""));
    assertFalse(CodeGenerator.isSimpleNumber("01"));
    assertFalse(CodeGenerator.isSimpleNumber("-1"));
    assertFalse(CodeGenerator.isSimpleNumber("1.0"));
  }

  private String printObjectLiteralWithQuotedKey(String key) {
    Node property = Node.newString(Token.STRING_KEY, key);
    property.setQuotedString();
    property.addChildToBack(Node.newNumber(1));

    Node objectLiteral = new Node(Token.OBJECTLIT);
    objectLiteral.addChildToBack(property);

    Node name = Node.newString(Token.NAME, "x");
    name.addChildToBack(objectLiteral);

    Node var = new Node(Token.VAR);
    var.addChildToBack(name);

    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(var);

    return new CodePrinter.Builder(script).build();
  }
}