package com.google.javascript.jscomp;

import org.junit.Test;

public class CollapsePropertiesIssue931Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true);
  }

  @Test
  public void testIssue931NestedReferenceBeforePropertyDeclaration() {
    test(
        "var ns = {}; ns.part.value; ns.part = {}; ns.part.value = 1;",
        "ns$part$value; var ns$part$value = 1;");
  }

  @Test
  public void testCollapsesNestedPropertyDeclarationAndReference() {
    test(
        "var ns = {}; ns.part = {}; ns.part.value = 1; ns.part.value;",
        "var ns$part$value = 1; ns$part$value;");
  }

  @Test
  public void testInlinesAliasBeforeCollapsingPropertyReference() {
    test(
        "var ns = {}; ns.member = 1; var alias = ns; alias.member;",
        "var ns$member = 1; ns$member;");
  }

  @Test
  public void testInlinesAliasChainBeforeCollapsingPropertyReference() {
    test(
        "var ns = {}; ns.member = 1; var first = ns; var second = first; second.member;",
        "var ns$member = 1; ns$member;");
  }

  @Test
  public void testCreatesFlattenedStubForReadOnlyProperty() {
    test(
        "var ns = {}; ns.member;",
        "var ns$member;");
  }

  @Test
  public void testDoesNotCollapseBracketPropertyAccess() {
    testSame("var ns = {}; ns['member'] = 1; ns['member'];");
  }

  @Test
  public void testCollapsesFunctionPropertyUsedAsCallTarget() {
    test(
        "var ns = {}; ns.method = function() { return 1; }; ns.method();",
        "var ns$method = function() { return 1; }; ns$method();");
  }
}