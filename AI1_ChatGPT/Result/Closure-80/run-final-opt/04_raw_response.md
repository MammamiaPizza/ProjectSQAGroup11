@org.junit.Test
public void testArrayToStringConcatenatesLiteralElements() {
  Node array = new Node(Token.ARRAYLIT);
  array.addChildToBack(Node.newString(Token.STRING, "first"));
  array.addChildToBack(Node.newString(Token.STRING, "second"));

  assertEquals("first,second", NodeUtil.arrayToString(array));
}

@org.junit.Test
public void testArrayToStringReturnsNullForNonLiteralElement() {
  Node array = new Node(Token.ARRAYLIT);
  array.addChildToBack(Node.newString(Token.STRING, "first"));
  array.addChildToBack(Node.newString(Token.NAME, "value"));

  assertNull(NodeUtil.arrayToString(array));
}

@org.junit.Test
public void testCallHasLocalResultRequiresCallNode() {
  assertFalse(NodeUtil.callHasLocalResult(new Node(Token.CALL)));

  boolean threw = false;
  try {
    NodeUtil.callHasLocalResult(Node.newString(Token.NAME, "notACall"));
  } catch (IllegalStateException expected) {
    threw = true;
  }
  assertTrue(threw);
}