@Test
public void testInterfacePropertyWithUnknownExtendedInterfaceDoesNotCrash() {
  testSame(
      "/**\n"
          + " * @interface\n"
          + " * @extends {MissingParent}\n"
          + " */\n"
          + "function Child() {}\n"
          + "/** @type {number} */\n"
          + "Child.prototype.value;");
}