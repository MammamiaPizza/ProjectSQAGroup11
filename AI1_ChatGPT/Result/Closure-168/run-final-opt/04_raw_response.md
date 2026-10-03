@Test
public void enumInitializedFromNonEnumNameReportsInitializerWarning() {
  testSame(
      "function f() {"
          + "var value = 0;"
          + "/** @enum {number} */ var LocalEnum = value;"
          + "}");
  assertEquals(0, compiler.getErrors().length);
  assertTrue(hasEnumInitializerWarning(compiler));
}

@Test
public void enumWithoutInitializerReportsInitializerWarning() {
  testSame(
      "function f() {"
          + "/** @enum {number} */ var LocalEnum;"
          + "}");
  assertEquals(0, compiler.getErrors().length);
  assertTrue(hasEnumInitializerWarning(compiler));
}