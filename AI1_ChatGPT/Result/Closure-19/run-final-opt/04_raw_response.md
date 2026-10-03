@Test
public void getFirstReturnsInterpreterBeforeAnyLinksAreAppended() {
  assertSame(interpreter, interpreter.getFirst());
}