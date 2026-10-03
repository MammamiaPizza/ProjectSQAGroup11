@org.junit.Test
public void testAllResultsMatchFollowsPossibleResultBranches() {
  com.google.common.base.Predicate<com.google.javascript.rhino.Node> isNumber =
      nodeUtilTypePredicateForCoverage(com.google.javascript.rhino.Token.NUMBER);

  com.google.javascript.rhino.Node assignment =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.ASSIGN,
          com.google.javascript.rhino.Node.newString("x"),
          com.google.javascript.rhino.Node.newNumber(1));
  org.junit.Assert.assertTrue(NodeUtil.allResultsMatch(assignment, isNumber));

  com.google.javascript.rhino.Node and =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.AND,
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newString("value"));
  org.junit.Assert.assertFalse(NodeUtil.allResultsMatch(and, isNumber));

  com.google.javascript.rhino.Node hook =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.HOOK,
          com.google.javascript.rhino.Node.newString("condition"),
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newNumber(2));
  org.junit.Assert.assertTrue(NodeUtil.allResultsMatch(hook, isNumber));
}

@org.junit.Test
public void testAnyResultsMatchIgnoresNonResultBranches() {
  com.google.common.base.Predicate<com.google.javascript.rhino.Node> isNumber =
      nodeUtilTypePredicateForCoverage(com.google.javascript.rhino.Token.NUMBER);

  com.google.javascript.rhino.Node comma =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.COMMA,
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newString("value"));
  org.junit.Assert.assertFalse(NodeUtil.anyResultsMatch(comma, isNumber));

  com.google.javascript.rhino.Node or =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.OR,
          com.google.javascript.rhino.Node.newString("left"),
          com.google.javascript.rhino.Node.newNumber(1));
  org.junit.Assert.assertTrue(NodeUtil.anyResultsMatch(or, isNumber));

  com.google.javascript.rhino.Node hook =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.HOOK,
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newString("trueBranch"),
          com.google.javascript.rhino.Node.newString("falseBranch"));
  org.junit.Assert.assertFalse(NodeUtil.anyResultsMatch(hook, isNumber));
}

private static com.google.common.base.Predicate<com.google.javascript.rhino.Node>
    nodeUtilTypePredicateForCoverage(final int type) {
  return new com.google.common.base.Predicate<com.google.javascript.rhino.Node>() {
    @Override
    public boolean apply(com.google.javascript.rhino.Node node) {
      return node.getType() == type;
    }
  };
}