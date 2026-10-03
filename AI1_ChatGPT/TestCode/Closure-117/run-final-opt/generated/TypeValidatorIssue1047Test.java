package com.google.javascript.jscomp;

import org.junit.Test;

public final class TypeValidatorIssue1047Test extends TypeCheckTest {

  @Test
  public void testNestedPropertyAccessReportsDeclaredReceiverType() {
    testSame(
        "/** @constructor */ function C2() {}\n"
            + "/** @constructor */ function C3() {}\n"
            + "/** @type {C2} */ C3.prototype.c2_;\n"
            + "C3.prototype.c2_.p;",
        "Property p never defined on C2");
  }

  @Test
  public void testNestedPropertyAccessAllowsPropertyDeclaredOnReceiverType() {
    testSame(
        "/** @constructor */ function C2() {}\n"
            + "/** @type {number} */ C2.prototype.p;\n"
            + "/** @constructor */ function C3() {}\n"
            + "/** @type {C2} */ C3.prototype.c2_;\n"
            + "C3.prototype.c2_.p;");
  }
}
