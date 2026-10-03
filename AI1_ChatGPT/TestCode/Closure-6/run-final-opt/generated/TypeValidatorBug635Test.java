package com.google.javascript.jscomp;

import com.google.javascript.jscomp.parsing.parser.trees.ParseTree;
import java.util.Collections;
import junit.framework.TestCase;

public class TypeValidatorBug635Test extends TestCase {

  public void testEnumPropertyRedefinedAsConstructorReportsTypeMismatchNotDuplicateDeclaration() {
    Compiler compiler = compile(
        "var a = {};\n"
            + "/** @enum {number} */ a.A = { ONE: 1 };\n"
            + "/** @constructor */ a.A = function() {};\n");

    assertEquals(0, compiler.getErrors().length);
    assertTrue(
        "Expected the incompatible enum/constructor assignment to be type checked.",
        containsWarning(compiler, "assignment to property A"));
    assertFalse(
        "A qualified enum redefinition must not be reported as a duplicate variable declaration.",
        containsWarning(compiler, "redefined with type"));
  }

  public void testForwardReferencedDeclaredTypeStillChecksInvalidInitializer() {
    Compiler compiler = compile(
        "/** @type {Foo} */ var value = 1;\n"
            + "/** @constructor */ function Foo() {}\n");

    assertEquals(0, compiler.getErrors().length);
    assertTrue(
        "A forward-referenced type name must not suppress a real initializer mismatch.",
        containsWarning(compiler, "initializing variable value"));
  }

  private Compiler compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);
    return compiler;
  }

  private boolean containsWarning(Compiler compiler, String text) {
    for (JSError warning : compiler.getWarnings()) {
      if (warning.description != null && warning.description.indexOf(text) >= 0) {
        return true;
      }
    }
    return false;
  }
}
