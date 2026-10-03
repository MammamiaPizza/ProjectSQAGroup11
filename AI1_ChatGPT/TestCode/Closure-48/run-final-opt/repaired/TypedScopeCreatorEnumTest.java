package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TypedScopeCreatorEnumTest {

  @Test
  public void testEnumObjectLiteralInitializerProducesNoWarning() {
    assertNoWarnings(
        "/** @enum {number} */\n"
            + "var Status = {\n"
            + "  OK: 0,\n"
            + "  ERROR: 1\n"
            + "};");
  }

  @Test
  public void testEnumInitializerMayReferencePreviouslyDeclaredEnum() {
    assertNoWarnings(
        "/** @enum {string} */\n"
            + "var First = {\n"
            + "  VALUE: 'value'\n"
            + "};\n"
            + "/** @enum {string} */\n"
            + "var Second = First;");
  }

  @Test
  public void testNonObjectNonEnumInitializerProducesEnumWarning() {
    Compiler compiler =
        runTypeCheck(
            "/** @enum {number} */\n"
                + "var InvalidEnum = 1;");

    assertEquals(0, compiler.getErrors().length);
    assertEquals(1, compiler.getWarnings().length);
    assertEquals(
        "enum initializer must be an object literal or an enum",
        compiler.getWarnings()[0].description);
  }

  private void assertNoWarnings(String source) {
    Compiler compiler = runTypeCheck(source);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(0, compiler.getWarnings().length);
  }

  private Compiler runTypeCheck(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    compiler.parseInputs();

    CompilerPass pass =
        new TypeCheck(
            compiler,
            compiler.getReverseAbstractInterpreter(),
            compiler.getTypeRegistry());
    pass.process(compiler.getExternsRoot(), compiler.getJsRoot());
    return compiler;
  }
}
