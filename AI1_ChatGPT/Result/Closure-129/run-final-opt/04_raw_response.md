@Test
public void checkOnlyRejectsAndWrapsAnUnbracedControlStructureBody() {
  com.google.javascript.rhino.Node root = compiler.parseTestCode("if (x) foo();");

  try {
    new PrepareAst(compiler, true).process(null, root);
    fail("Expected check-only processing to reject the unbraced body");
  } catch (IllegalStateException expected) {
    com.google.javascript.rhino.Node ifNode = root.getFirstChild();
    com.google.javascript.rhino.Node body = ifNode.getLastChild();
    assertTrue(body.isBlock());
    assertTrue(body.getFirstChild().isExprResult());
  }
}

@Test
public void checkOnlyRejectsAndWrapsAnEmptyControlStructureBody() {
  com.google.javascript.rhino.Node root = compiler.parseTestCode("while (x);");

  try {
    new PrepareAst(compiler, true).process(null, root);
    fail("Expected check-only processing to reject the empty unbraced body");
  } catch (IllegalStateException expected) {
    com.google.javascript.rhino.Node whileNode = root.getFirstChild();
    com.google.javascript.rhino.Node body = whileNode.getLastChild();
    assertTrue(body.isBlock());
    assertNull(body.getFirstChild());
  }
}

@Test
public void checkOnlyAcceptsAlreadyNormalizedControlStructureBlocks() {
  com.google.javascript.rhino.Node root =
      compiler.parseTestCode("if (x) { foo(); } else { bar(); }");

  new PrepareAst(compiler, true).process(null, root);

  com.google.javascript.rhino.Node ifNode = root.getFirstChild();
  assertTrue(ifNode.getFirstChild().getNext().isBlock());
  assertTrue(ifNode.getLastChild().isBlock());
}

@Test
public void checkOnlyDoesNotRequireBlocksForLabelsOrSwitches() {
  com.google.javascript.rhino.Node root =
      compiler.parseTestCode("label: foo(); switch (x) { case 0: bar(); }");

  new PrepareAst(compiler, true).process(null, root);

  assertTrue(root.getFirstChild().isLabel());
  assertTrue(root.getFirstChild().getLastChild().isExprResult());
  assertTrue(root.getLastChild().isSwitch());
}