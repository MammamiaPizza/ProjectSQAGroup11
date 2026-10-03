@Test
public void testSub() {
    Node left = IR.number(1);
    Node right = IR.number(2);
    Node result = IR.sub(left, right);
    assertNotNull(result);
    assertEquals(com.google.javascript.rhino.Token.SUB, result.getType());
}

@Test
public void testArraylitWithExpression() {
    Node expr = IR.number(5);
    Node array = IR.arraylit(expr);
    assertNotNull(array);
    assertEquals(com.google.javascript.rhino.Token.ARRAYLIT, array.getType());
    assertEquals(1, array.getChildCount());
}

@Test
public void testHook() {
    Node cond = IR.trueNode();
    Node trueval = IR.number(1);
    Node falseval = IR.number(0);
    Node hook = IR.hook(cond, trueval, falseval);
    assertNotNull(hook);
    assertEquals(com.google.javascript.rhino.Token.HOOK, hook.getType());
    assertEquals(3, hook.getChildCount());
}

@Test
public void testBreakNodeWithLabel() {
    Node label = IR.labelName("l");
    Node brk = IR.breakNode(label);
    assertNotNull(brk);
    assertEquals(com.google.javascript.rhino.Token.BREAK, brk.getType());
    assertEquals(label, brk.getFirstChild());
}