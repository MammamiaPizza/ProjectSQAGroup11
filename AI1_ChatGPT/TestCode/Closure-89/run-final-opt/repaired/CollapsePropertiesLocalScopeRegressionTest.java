package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;

public class CollapsePropertiesLocalScopeRegressionTest extends TestCase {

  private String process(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);
    Node root = compiler.parseInputs();
    new CollapseProperties(compiler, false, true)
        .process(compiler.getExternsRoot(), root);
    return compiler.toSource();
  }

  private String print(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);
    compiler.parseInputs();
    return compiler.toSource();
  }

  private void test(String source, String expected) {
    assertEquals(print(expected), process(source));
  }

  private void testSame(String source) {
    test(source, source);
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
