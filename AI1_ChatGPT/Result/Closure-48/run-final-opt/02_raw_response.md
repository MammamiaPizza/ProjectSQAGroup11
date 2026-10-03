package com.google.javascript.jscomp;

import org.junit.Test;

public class TypedScopeCreatorEnumTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new TypeCheck(
        compiler,
        compiler.getReverseAbstractInterpreter(),
        compiler.getTypeRegistry());
  }

  @Test
  public void testEnumObjectLiteralInitializerProducesNoWarning() {
    testNoWarning(
        "/** @enum {number} */\n"
            + "var Status = {\n"
            + "  OK: 0,\n"
            + "  ERROR: 1\n"
            + "};");
  }

  @Test
  public void testEnumInitializerMayReferencePreviouslyDeclaredEnum() {
    testNoWarning(
        "/** @enum {string} */\n"
            + "var First = {\n"
            + "  VALUE: 'value'\n"
            + "};\n"
            + "/** @enum {string} */\n"
            + "var Second = First;");
  }

  @Test
  public void testNonObjectNonEnumInitializerProducesEnumWarning() {
    testWarning(
        "/** @enum {number} */\n"
            + "var InvalidEnum = 1;",
        "enum initializer must be an object literal or an enum");
  }
}