package com.google.javascript.jscomp;

import org.junit.Test;

public class CollapsePropertiesRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true);
  }

  @Test
  public void testAliasedTopLevelEnum() {
    test(
        "/** @enum {number} */ var Enum = {A: 1};"
            + "var Alias = Enum;"
            + "Alias.A;",
        "var Enum = {};"
            + "var Enum$A = 1;"
            + "var Alias = Enum;"
            + "Enum$A;");
  }

  @Test
  public void testIssue389AliasPrefixIsCollapsed() {
    test(
        "var ns = {};"
            + "ns.foo = {};"
            + "var alias = ns.foo;"
            + "alias.bar = 1;"
            + "alias.bar;",
        "var ns$foo = {};"
            + "var alias = ns$foo;"
            + "var ns$foo$bar = 1;"
            + "ns$foo$bar;");
  }
}
