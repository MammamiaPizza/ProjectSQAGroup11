@Test
public void testArrayToStringWithNullElement() {
    Node array = new Node(Token.ARRAYLIT);
    Node nameNode = new Node(Token.NAME, Node.newString("x"));
    array.addChildToBack(nameNode);
    assertNull(NodeUtil.arrayToString(array));
}

@Test
public void testEvaluatesToLocalValueWithGetProp() {
    Node getprop = new Node(Token.GETPROP, new Node(Token.NAME, Node.newString("x")),
Node.newString("prop"));
    assertFalse(NodeUtil.evaluatesToLocalValue(getprop));
}

@Test
public void testEvaluatesToLocalValueWithFunctionExpression() {
    Node func = new Node(Token.FUNCTION);
    assertTrue(NodeUtil.evaluatesToLocalValue(func));
}

@Test
public void testEvaluatesToLocalValueWithNonConstantName() {
    Node nameNode = new Node(Token.NAME, Node.newString("x"));
    assertFalse(NodeUtil.evaluatesToLocalValue(nameNode));
}