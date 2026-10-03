@org.junit.Test
public void testArrayToStringConcatenatesLiteralElements() {
  com.google.javascript.rhino.Node array =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.ARRAYLIT);
  array.addChildToBack(
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.STRING, "first"));
  array.addChildToBack(
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.STRING, "second"));

  junit.framework.Assert.assertEquals("first,second", NodeUtil.arrayToString(array));
}

@org.junit.Test
public void testArrayToStringReturnsNullForNonLiteralElement() {
  com.google.javascript.rhino.Node array =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.ARRAYLIT);
  com.google.javascript.rhino.Node call =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.CALL,
          new com.google.javascript.rhino.Node(
              com.google.javascript.rhino.Token.NAME, "f"));
  array.addChildToBack(call);

  junit.framework.Assert.assertNull(NodeUtil.arrayToString(array));
}