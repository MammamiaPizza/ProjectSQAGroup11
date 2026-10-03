package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;

public class ProcessClosurePrimitivesAdditionalTest extends CompilerTestCase {

  private final CheckLevel requiresLevel = CheckLevel.ERROR;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ProcessClosurePrimitives(compiler, requiresLevel, false);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testTopLevelProvideCreatesNamespaceVariable() {
    test("goog.provide('foo');", "var foo = {};");
  }

  @Test
  public void testNestedProvideCreatesAllNamespaceLevels() {
    test(
        "goog.provide('foo.bar');",
        "var foo = {};foo.bar = {};");
  }

  @Test
  public void testRequireOfPreviouslyProvidedNamespaceIsRemoved() {
    test(
        "goog.provide('foo');goog.require('foo');",
        "var foo = {};");
  }

  @Test
  public void testMissingRequireReportsMissingProvideError() {
    testError(
        "goog.require('missing.namespace');",
        ProcessClosurePrimitives.MISSING_PROVIDE_ERROR);
  }

  @Test
  public void testRequireBeforeProvideReportsLateProvideError() {
    testError(
        "goog.require('foo');goog.provide('foo');",
        ProcessClosurePrimitives.LATE_PROVIDE_ERROR);
  }

  @Test
  public void testIndependentModulesKeepSeparateTopLevelProvidesInTheirModules() {
    JSModule first = new JSModule("first");
    first.add(SourceFile.fromCode("first.js", "goog.provide('first');"));

    JSModule second = new JSModule("second");
    second.add(SourceFile.fromCode("second.js", "goog.provide('second');"));

    test(
        new JSModule[] {first, second},
        new String[] {"var first = {};", "var second = {};"});
  }
}

abstract class CompilerTestCase extends TestCase {

  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected int getNumRepetitions() {
    return 1;
  }

  protected void test(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode.js", source)),
        new CompilerOptions());
    compiler.parseInputs();
    runProcessor(compiler);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(expected, compiler.toSource(compiler.getJsRoot()));
  }

  protected void testError(String source, DiagnosticType error) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode.js", source)),
        new CompilerOptions());
    compiler.parseInputs();
    runProcessor(compiler);
    JSError[] errors = compiler.getErrors();
    assertEquals(1, errors.length);
    assertEquals(error, errors[0].type);
  }

  protected void test(JSModule[] modules, String[] expected) {
    Compiler compiler = new Compiler();
    List<JSModule> moduleList = Arrays.asList(modules);
    compiler.initModules(
        Collections.<SourceFile>emptyList(),
        moduleList,
        new CompilerOptions());
    compiler.parseInputs();
    runProcessor(compiler);
    assertEquals(0, compiler.getErrors().length);

    for (int i = 0; i < modules.length; i++) {
      Node root = modules[i].getInputs().get(0).getAstRoot(compiler);
      assertEquals(expected[i], compiler.toSource(root));
    }
  }

  private void runProcessor(Compiler compiler) {
    for (int i = 0; i < getNumRepetitions(); i++) {
      getProcessor(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());
    }
  }
}