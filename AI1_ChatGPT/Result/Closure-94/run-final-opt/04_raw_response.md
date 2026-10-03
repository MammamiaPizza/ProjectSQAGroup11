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