package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class CompilerDependencySortingTest {

  @Test
  public void sortsReverseOrderedDependencyChainWithinModule() {
    JSModule base = new JSModule("base");
    base.add(SourceFile.fromCode(
        "base.js", "goog.provide('test.base'); var BASE_MARKER = 1;"));

    JSModule application = new JSModule("application");
    application.addDependency(base);
    application.add(SourceFile.fromCode(
        "consumer.js",
        "goog.require('test.middle'); var CONSUMER_MARKER = MIDDLE_MARKER;"));
    application.add(SourceFile.fromCode(
        "middle.js",
        "goog.require('test.provider'); goog.provide('test.middle');"
            + " var MIDDLE_MARKER = PROVIDER_MARKER;"));
    application.add(SourceFile.fromCode(
        "provider.js",
        "goog.provide('test.provider'); var PROVIDER_MARKER = 1;"));

    Compiler compiler = compile(base, application);

    assertEquals(0, compiler.getErrorCount());
    String[] output = compiler.toSourceArray(application);
    assertEquals(3, output.length);
    assertTrue(indexOf(output, "PROVIDER_MARKER") < indexOf(output, "MIDDLE_MARKER"));
    assertTrue(indexOf(output, "MIDDLE_MARKER") < indexOf(output, "CONSUMER_MARKER"));
  }

  @Test
  public void keepsIndependentInputsInTheirOriginalOrder() {
    JSModule base = new JSModule("base");
    base.add(SourceFile.fromCode(
        "base.js", "goog.provide('test.base'); var BASE_MARKER = 1;"));

    JSModule application = new JSModule("application");
    application.addDependency(base);
    application.add(SourceFile.fromCode(
        "first.js", "var FIRST_INDEPENDENT_MARKER = 1;"));
    application.add(SourceFile.fromCode(
        "second.js", "var SECOND_INDEPENDENT_MARKER = 2;"));

    Compiler compiler = compile(base, application);

    assertEquals(0, compiler.getErrorCount());
    String[] output = compiler.toSourceArray(application);
    assertEquals(2, output.length);
    assertTrue(indexOf(output, "FIRST_INDEPENDENT_MARKER")
        < indexOf(output, "SECOND_INDEPENDENT_MARKER"));
  }

  @Test
  public void emitsInputsFromDependentModulesAfterTheirDependencies() {
    JSModule provider = new JSModule("provider");
    provider.add(SourceFile.fromCode(
        "provider.js",
        "goog.provide('test.provider'); var MODULE_PROVIDER_MARKER = 1;"));

    JSModule consumer = new JSModule("consumer");
    consumer.addDependency(provider);
    consumer.add(SourceFile.fromCode(
        "consumer.js",
        "goog.require('test.provider');"
            + " var MODULE_CONSUMER_MARKER = MODULE_PROVIDER_MARKER;"));

    Compiler compiler = compile(provider, consumer);

    assertEquals(0, compiler.getErrorCount());
    String[] output = compiler.toSourceArray();
    assertTrue(indexOf(output, "MODULE_PROVIDER_MARKER")
        < indexOf(output, "MODULE_CONSUMER_MARKER"));
  }

  @Test
  public void reportsErrorWhenModuleDependsOnModuleListedLater() {
    JSModule dependent = new JSModule("dependent");
    JSModule dependency = new JSModule("dependency");
    dependent.addDependency(dependency);
    dependent.add(SourceFile.fromCode(
        "dependent.js", "var LATE_DEPENDENT_MARKER = 1;"));
    dependency.add(SourceFile.fromCode(
        "dependency.js", "var LATE_DEPENDENCY_MARKER = 1;"));

    CompilerOptions options = new CompilerOptions();
    options.setDependencyOptions(DependencyOptions.sortOnly());
    Compiler compiler = new Compiler();
    compiler.compileModules(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(dependent, dependency),
        options);

    assertTrue(compiler.getErrorCount() > 0);
  }

  private static Compiler compile(JSModule first, JSModule second) {
    CompilerOptions options = new CompilerOptions();
    options.setDependencyOptions(DependencyOptions.sortOnly());

    Compiler compiler = new Compiler();
    compiler.compileModules(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(first, second),
        options);
    return compiler;
  }

  private static int indexOf(String[] sources, String marker) {
    for (int i = 0; i < sources.length; i++) {
      if (sources[i].contains(marker)) {
        return i;
      }
    }
    throw new AssertionError("Marker not found in compiler output: " + marker);
  }
}
