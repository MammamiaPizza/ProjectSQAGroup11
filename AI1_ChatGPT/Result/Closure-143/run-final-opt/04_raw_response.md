@Test
public void testCallNestedInDiscardedArithmeticIsPreserved() {
  String input = "1 + 2 * foo();";
  String expected = "foo();";
  Assert.assertEquals(parse(expected), transform(input));
}

@Test
public void testNewNestedInDiscardedArithmeticIsPreserved() {
  String input = "1 + 2 * new Foo();";
  String expected = "new Foo();";
  Assert.assertEquals(parse(expected), transform(input));
}