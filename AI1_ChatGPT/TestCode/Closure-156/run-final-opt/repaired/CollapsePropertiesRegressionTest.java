package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CollapsePropertiesRegressionTest {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true);
  }

  private void test(String input, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", input)),
        new CompilerOptions());
    compiler.parseInputs();

    Node externs = compiler.getExternsRoot();
    Node root = compiler.getJsRoot();
    getProcessor(compiler).process(externs, root);

    assertEquals(
        expected.replaceAll("\\s+", ""),
        compiler.toSource().replaceAll("\\s+", ""));
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
