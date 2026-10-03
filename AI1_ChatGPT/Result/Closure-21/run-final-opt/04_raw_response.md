@Test
public void testArithmeticResultWithCallIsReported() throws Exception {
  assertWarningCount("x + foo();", 1);
}

@Test
public void testPropertyAccessResultWithCallIsReported() throws Exception {
  assertWarningCount("foo().bar;", 1);
}

@Test
public void testConditionalResultWithCallsIsReported() throws Exception {
  assertWarningCount("x ? foo() : bar();", 1);
}

@Test
public void testUselessForInitializerWithCallIsReported() throws Exception {
  assertWarningCount("for (x == foo();;) {}", 1);
}