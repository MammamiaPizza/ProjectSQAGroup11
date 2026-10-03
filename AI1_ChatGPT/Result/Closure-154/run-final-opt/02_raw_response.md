package com.google.javascript.jscomp;

import com.google.javascript.rhino.JSError;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeCheckInterfaceInheritanceRegressionTest {

  private Result compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
    return compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("test.js", source)),
        options);
  }

  private boolean hasWarningContaining(Result result, String text) {
    for (JSError warning : result.warnings) {
      if (warning.description != null && warning.description.contains(text)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void interfaceExtendingConstructorProducesInterfaceOnlyWarning() {
    Result result = compile(
        "/** @constructor */ function Parent() {}\n"
            + "/** @interface @extends {Parent} */ function Child() {}\n");

    assertTrue(
        "An interface must not be allowed to extend a constructor.",
        hasWarningContaining(result, "interface can only extend interfaces"));
  }

  @Test
  public void interfaceExtendingAnotherInterfaceDoesNotProduceInterfaceOnlyWarning() {
    Result result = compile(
        "/** @interface */ function Parent() {}\n"
            + "/** @interface @extends {Parent} */ function Child() {}\n");

    assertFalse(
        "A valid interface inheritance declaration must not be diagnosed as extending a non-interface.",
        hasWarningContaining(result, "interface can only extend interfaces"));
  }

  @Test
  public void implementingTypeMustImplementPropertiesInheritedThroughInterface() {
    Result result = compile(
        "/** @interface */ function Root() {}\n"
            + "/** @type {number} */ Root.prototype.count;\n"
            + "/** @interface @extends {Root} */ function ChildInterface() {}\n"
            + "/** @constructor @implements {ChildInterface} */ function Implementation() {}\n");

    assertTrue(
        "Inherited interface properties must be required on implementing types.",
        hasWarningContaining(result, "not implemented by type"));
  }

  @Test
  public void implementingInheritedInterfacePropertyWithCompatibleTypeDoesNotWarn() {
    Result result = compile(
        "/** @interface */ function Root() {}\n"
            + "/** @type {number} */ Root.prototype.count;\n"
            + "/** @interface @extends {Root} */ function ChildInterface() {}\n"
            + "/** @constructor @implements {ChildInterface} */ function Implementation() {}\n"
            + "Implementation.prototype.count = 1;\n");

    assertFalse(
        "A compatible implementation of an inherited interface property must be accepted.",
        hasWarningContaining(result, "not implemented by type"));
  }
}