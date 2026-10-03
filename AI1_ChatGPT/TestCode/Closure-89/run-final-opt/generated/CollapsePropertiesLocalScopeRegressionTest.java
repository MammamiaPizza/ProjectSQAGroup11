package com.google.javascript.jscomp;

public class CollapsePropertiesLocalScopeRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true);
  }

  public void testCollapsesSimpleGlobalProperty() {
    test(
        "var namespace = {}; namespace.property = 1;",
        "var namespace$property = 1;");
  }

  public void testDoesNotCollapsePropertyOfLocalFunctionVariable() {
    testSame(
        "function outer() {"
            + "  var local = function() {};"
            + "  local.property = 1;"
            + "}");
  }

  public void testDoesNotCollapseChildOfLocalFunctionProperty() {
    testSame(
        "function outer() {"
            + "  var local = function() {};"
            + "  local.child = {};"
            + "  local.child.property = 1;"
            + "}");
  }

  public void testDoesNotCollapsePropertyOfLocalNamedFunction() {
    testSame(
        "function outer() {"
            + "  function LocalConstructor() {}"
            + "  LocalConstructor.property = 1;"
            + "}");
  }

  public void testDoesNotCollapsePropertyOfLocalFunctionAtDepthOne() {
    testSame(
        "function outer() {"
            + "  var local = function() {};"
            + "  local.property = 1;"
            + "  return local;"
            + "}");
  }

  public void testDoesNotCollapsePropertyOfLocalFunctionAtDepthTwo() {
    testSame(
        "function outer() {"
            + "  function inner() {"
            + "    var local = function() {};"
            + "    local.property = 1;"
            + "  }"
            + "  inner();"
            + "}");
  }

  public void testDoesNotInlineAliasToLocalObjectAtDepthOne() {
    testSame(
        "function outer() {"
            + "  var local = {};"
            + "  var alias = local;"
            + "  alias.property = 1;"
            + "}");
  }

  public void testDoesNotInlineAliasToLocalFunctionAtDepthOne() {
    testSame(
        "function outer() {"
            + "  var local = function() {};"
            + "  var alias = local;"
            + "  alias.property = 1;"
            + "}");
  }

  public void testDoesNotInlineAliasToLocalFunctionAtDepthTwo() {
    testSame(
        "function outer() {"
            + "  function inner() {"
            + "    var local = function() {};"
            + "    var alias = local;"
            + "    alias.property = 1;"
            + "  }"
            + "  inner();"
            + "}");
  }
}
