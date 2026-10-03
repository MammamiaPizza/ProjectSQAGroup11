public void testRequireWithExtraArgumentDoesNotExtractClassName() {
  com.google.javascript.rhino.Node callee =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.GETPROP,
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.NAME, "goog"),
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.STRING, "require"));
  com.google.javascript.rhino.Node call =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.CALL, callee);
  call.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.STRING, "foo"));
  call.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.STRING, "bar"));
  com.google.javascript.rhino.Node parent =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.EXPR_RESULT, call);

  junit.framework.Assert.assertNull(
      new com.google.javascript.jscomp.ClosureCodingConvention()
          .extractClassNameIfRequire(call, parent));
}

public void testRequirePropertyCallDoesNotExtractClassName() {
  com.google.javascript.rhino.Node requireName =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.GETPROP,
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.NAME, "goog"),
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.STRING, "require"));
  com.google.javascript.rhino.Node callee =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.GETPROP,
          requireName,
          com.google.javascript.rhino.Node.newString(
              com.google.javascript.rhino.Token.STRING, "call"));
  com.google.javascript.rhino.Node call =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.CALL, callee);
  call.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.STRING, "foo"));
  com.google.javascript.rhino.Node parent =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.EXPR_RESULT, call);

  junit.framework.Assert.assertNull(
      new com.google.javascript.jscomp.ClosureCodingConvention()
          .extractClassNameIfRequire(call, parent));
}