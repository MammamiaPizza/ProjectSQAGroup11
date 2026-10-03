package com.google.javascript.jscomp;

import org.junit.Test;

public class CheckAccessControlsRegressionTest extends CompilerTestCase {

  public CheckAccessControlsRegressionTest() {
    enableTypeCheck();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckAccessControls(compiler);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

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
    testSame(
        "/** @constructor */ function Foo() {}\n"
            + "/** @private */ Foo.prototype.secret = 0;\n"
            + "var value = new Foo().secret;\n");
  }
}