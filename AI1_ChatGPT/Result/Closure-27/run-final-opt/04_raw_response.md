@Test
public void testBlockKeepsMultipleStatementsInOrder() {
  Node first = IR.exprResult(IR.number(1));
  Node second = IR.exprResult(IR.number(2));
  Node block = IR.block(first, second);

  assertTrue(block.isBlock());
  assertEquals(2, block.getChildCount());
  assertSame(first, block.getFirstChild());
  assertSame(second, block.getLastChild());
}

@Test
public void testArrayLiteralKeepsEmptySlotsAndExpressions() {
  Node emptySlot = IR.empty();
  Node value = IR.number(1);
  Node array = IR.arraylit(emptySlot, value);

  assertEquals(2, array.getChildCount());
  assertSame(emptySlot, array.getFirstChild());
  assertSame(value, array.getLastChild());
}

@Test
public void testRegexpWithFlagsKeepsPatternAndFlags() {
  Node pattern = IR.string("a");
  Node flags = IR.string("g");
  Node regexp = IR.regexp(pattern, flags);

  assertEquals(2, regexp.getChildCount());
  assertSame(pattern, regexp.getFirstChild());
  assertSame(flags, regexp.getLastChild());
}

@Test
public void testHookKeepsAllExpressionOperands() {
  Node condition = IR.trueNode();
  Node trueValue = IR.sub(IR.number(3), IR.number(1));
  Node falseValue = IR.or(IR.falseNode(), IR.trueNode());
  Node hook = IR.hook(condition, trueValue, falseValue);

  assertEquals(3, hook.getChildCount());
  assertSame(condition, hook.getFirstChild());
  assertSame(trueValue, condition.getNext());
  assertSame(falseValue, hook.getLastChild());
}