package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import java.io.File;
import java.util.Collections;
import org.junit.Test;

public class ProcessCommonJSModulesGeneratedTest {

  @Test
  public void testToModuleNameRemovesLeadingDotAndJsExtension() {
    String separator = File.separator;
    assertEquals(
        "module$foo$bar_baz",
        ProcessCommonJSModules.toModuleName(
            "." + separator + "foo" + separator + "bar-baz.js"));
  }

  @Test
  public void testToModuleNameOnlyRemovesTrailingJsExtension() {
    String separator = File.separator;
    assertEquals(
        "module$foo$library.jsx",
        ProcessCommonJSModules.toModuleName(
            "foo" + separator + "library.jsx"));
  }

  @Test
  public void testRelativeRequiredModuleNameUsesCurrentFileDirectory() {
    String output = process("dir/main.js", "", "require('./dependency');");
    assertTrue(output.contains("goog.require('module$dir$dependency')"));

    output = process("dir/sub/main.js", "", "require('../dependency');");
    assertTrue(output.contains("goog.require('module$dir$dependency')"));
  }

  @Test
  public void testExportsAreRewrittenToModuleReference() {
    String output = process("pkg/module.js", "pkg", "exports.answer = 42;");

    assertTrue(output.contains("goog.provide('module$module')"));
    assertTrue(output.contains("module$module.answer = 42"));
    assertTrue(output.contains("module$module.module$exports"));
  }

  @Test
  public void testModuleExportsAssignmentIsRewritten() {
    String output = process("pkg/module.js", "pkg", "module.exports = {answer: 42};");

    assertTrue(output.contains("module$module.module$exports"));
    assertFalse(output.contains("module.exports"));
  }

  @Test
  public void testRequireIsRewrittenAndGlobalVariablesAreSuffixed() {
    String output =
        process(
            "pkg/module.js",
            "pkg",
            "var dependency = require('./other-module'); exports.value = dependency;");

    assertTrue(output.contains("goog.require('module$pkg$other_module')"));
    assertTrue(output.contains("module$pkg$other_module"));
    assertTrue(output.contains("dependency$$module$module"));
    assertTrue(output.contains("module$module.value = dependency$$module$module"));
  }

  @Test
  public void testNonStringRequireIsNotTreatedAsModuleDependency() {
    String output = process("pkg/module.js", "pkg", "var name = 'dep'; require(name);");

    assertTrue(output.contains("require(name$$module$module)"));
    assertFalse(output.contains("goog.require("));
  }

  @Test
  public void testFilenamePrefixIsRemovedOnlyOnceWhenGeneratingModuleName() {
    String output = process("root/root/file.js", "root", "exports.value = 1;");

    assertTrue(output.contains("goog.provide('module$root$file')"));
    assertTrue(output.contains("module$root$file.value = 1"));
    assertFalse(output.contains("goog.provide('module$file')"));
  }

  @Test
  public void testScriptsWithoutExportsStillGetModuleWrapperAndRenamedGlobals() {
    String output = process("pkg/plain.js", "pkg", "var value = 1; value++;");

    assertTrue(output.contains("goog.provide('module$plain')"));
    assertTrue(output.contains("value$$module$plain"));
    assertTrue(output.contains("module$plain.module$exports"));
  }

  private String process(String filename, String prefix, String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode(filename, source)),
        options);
    compiler.parseInputs();

    Node externs = null;
    Node root = compiler.getJsRoot();
    new ProcessCommonJSModules(compiler, prefix).process(externs, root);

    return compiler.toSource();
  }
}
