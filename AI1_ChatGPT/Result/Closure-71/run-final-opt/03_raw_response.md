package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class CheckAccessControlsRegressionTest {

  @Test
  public void testPrivatePrototypePropertyOverrideInAnotherFileIsRejected() {
    testError(
        new String[] {
          "/** @constructor */ function Foo() {}\n"
              + "/** @private */ Foo.prototype.secret = 0;\n",
          "/** @constructor @extends {Foo} */ function Bar() {}\n"
              + "Bar.prototype.secret = 1;\n"
        },
        CheckAccessControls.PRIVATE_OVERRIDE);
  }

  @Test
  public void testPrivatePrototypePropertyAccessFromAnotherFileIsRejected() {
    testError(
        new String[] {
          "/** @constructor */ function Foo() {}\n"
              + "/** @private */ Foo.prototype.secret = 0;\n",
          "var value = new Foo().secret;\n"
        },
        CheckAccessControls.BAD_PRIVATE_PROPERTY_ACCESS);
  }

  @Test
  public void testPrivateInheritedPrototypePropertyAccessFromAnotherFileIsRejected() {
    testError(
        new String[] {
          "/** @constructor */ function Foo() {}\n"
              + "/** @private */ Foo.prototype.secret = 0;\n",
          "/** @constructor @extends {Foo} */ function Bar() {}\n"
              + "var value = new Bar().secret;\n"
        },
        CheckAccessControls.BAD_PRIVATE_PROPERTY_ACCESS);
  }

  @Test
  public void testPrivatePrototypePropertyAccessInDeclaringFileIsAllowed() {
    Compiler compiler = compile(
        new String[] {
          "/** @constructor */ function Foo() {}\n"
              + "/** @private */ Foo.prototype.secret = 0;\n"
              + "var value = new Foo().secret;\n"
        });
    assertEquals(0, compiler.getErrors().length);
  }

  private void testError(String[] sources, DiagnosticType expectedError) {
    Compiler compiler = compile(sources);
    JSError[] errors = compiler.getErrors();
    assertEquals(1, errors.length);
    assertEquals(expectedError, errors[0].type);
  }

  private Compiler compile(String[] sources) {
    SourceFile[] inputs = new SourceFile[sources.length];
    for (int i = 0; i < sources.length; i++) {
      inputs[i] = SourceFile.fromCode("input" + i + ".js", sources[i]);
    }

    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;

    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(inputs),
        options);
    return compiler;
  }
}