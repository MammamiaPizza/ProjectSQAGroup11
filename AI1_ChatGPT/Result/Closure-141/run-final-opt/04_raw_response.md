@org.junit.Test
public void testCanBeSideEffectedHonorsKnownConstants() {
  com.google.javascript.rhino.Node name =
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.NAME, "value");

  junit.framework.Assert.assertTrue(NodeUtil.canBeSideEffected(name));

  java.util.Set<String> knownConstants = new java.util.HashSet<String>();
  knownConstants.add("value");
  junit.framework.Assert.assertFalse(
      NodeUtil.canBeSideEffected(name, knownConstants));
}

@org.junit.Test
public void testCanBeSideEffectedRecognizesCallsPropertiesAndChildren() {
  junit.framework.Assert.assertTrue(
      NodeUtil.canBeSideEffected(
          new com.google.javascript.rhino.Node(
              com.google.javascript.rhino.Token.CALL)));
  junit.framework.Assert.assertTrue(
      NodeUtil.canBeSideEffected(
          new com.google.javascript.rhino.Node(
              com.google.javascript.rhino.Token.GETPROP)));

  com.google.javascript.rhino.Node expression =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.OR);
  expression.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.STRING, "literal"));
  expression.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.NAME, "mutable"));

  junit.framework.Assert.assertTrue(NodeUtil.canBeSideEffected(expression));
}