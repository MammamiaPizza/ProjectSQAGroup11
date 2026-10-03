package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;

public class CodeGeneratorNumericKeyRegressionTest extends TestCase {

  public void testLeadingZeroNumericStringKeyIsNotNormalized() {
    assertEquals("var x={[\"010\"]:1}", printObjectWithKey("010"));
  }

  public void testAllZeroMultiDigitStringKeyIsNotNormalized() {
    assertEquals("var x={[\"00\"]:1}", printObjectWithKey("00"));
  }

  public void testCanonicalNumericStringKeyUsesNumericKeyPrinting() {
    assertEquals("var x={[10]:1}", printObjectWithKey("10"));
  }

  private static String printObjectWithKey(String keyText) {
    Node script = new Node(Token.SCRIPT);
    Node var = new Node(Token.VAR);
    Node name = Node.newString(Token.NAME, "x");
    Node object = new Node(Token.OBJECTLIT);
    Node key = Node.newString(Token.STRING_KEY, keyText);

    key.addChildToBack(Node.newNumber(1));
    object.addChildToBack(key);
    name.addChildToBack(object);
    var.addChildToBack(name);
    script.addChildToBack(var);

    return new CodePrinter.Builder(script).build();
  }
}
