@Test
public void testArrayToStringConvertsSupportedLiteralElements() {
  com.google.javascript.rhino.Node array =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.ARRAYLIT);
  array.addChildToBack(com.google.javascript.rhino.Node.newString("x"));
  array.addChildToBack(
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.NULL));
  array.addChildToBack(
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.TRUE));
  array.addChildToBack(com.google.javascript.rhino.Node.newNumber(2));

  assertEquals("x,,true,2", NodeUtil.arrayToString(array));
}

@Test
public void testArrayToStringReturnsNullForNonLiteralElement() {
  com.google.javascript.rhino.Node array =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.ARRAYLIT);
  array.addChildToBack(
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.NAME));

  assertNull(NodeUtil.arrayToString(array));
}

@Test
public void testPureBooleanValueForVoidOfAssignmentIsUnknown() {
  com.google.javascript.rhino.Node assignment =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.ASSIGN,
          new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.NAME),
          com.google.javascript.rhino.Node.newNumber(1));
  com.google.javascript.rhino.Node voidAssignment =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.VOID, assignment);

  assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(voidAssignment));
}