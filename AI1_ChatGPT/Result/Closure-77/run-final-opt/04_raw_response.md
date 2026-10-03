@org.junit.Test
public void testNulCharacterBeforeDigitUsesHexEscape() {
  com.google.javascript.rhino.Node declaration =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.VAR);
  com.google.javascript.rhino.Node name =
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.NAME, "x");
  name.addChildToBack(com.google.javascript.rhino.Node.newString("\0" + "1"));
  declaration.addChildToBack(name);

  assertEquals("var x=\"\\x001\"", new CodePrinter.Builder(declaration).build());
}