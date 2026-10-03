package com.google.javascript.jscomp;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.javascript.rhino.Node;

import java.io.File;

public class ProcessCommonJSModulesBug732Test {

  // ---- Tests for static toModuleName(String) ----

  @Test public void testToModuleNameBasic() {
    assertEquals("module$foo", ProcessCommonJSModules.toModuleName("foo.js"));
    assertEquals("module$foo$bar", ProcessCommonJSModules.toModuleName("foo/bar.js")); }

  @Test public void testToModuleNameDash() {
    assertEquals("module$foo_bar", ProcessCommonJSModules.toModuleName("foo-bar.js")); }

  @Test public void testToModuleNameLeadingDotSlash() {
    assertEquals("module$foo", ProcessCommonJSModules.toModuleName("./foo.js")); }

  @Test public void testToModuleNameTrailingJsOnly() {
    assertEquals("module$foo.js_bar", ProcessCommonJSModules.toModuleName("foo.js/bar.js")); }

  @Test public void testToModuleNameEmpty() {
    assertEquals("module$", ProcessCommonJSModules.toModuleName("")); }

  @Test(expected = NullPointerException.class) public void testToModuleNameNull() {
    ProcessCommonJSModules.toModuleName(null); }

  // ---- Tests for static toModuleName(required, current) ----

  @Test public void testToModuleNameRelativeSameDir() {
    assertEquals("module$foo$bar",
        ProcessCommonJSModules.toModuleName("./bar.js", "foo/foo.js")); }

  @Test public void testToModuleNameRelativeParentDir() {
    assertEquals("module$bar",
        ProcessCommonJSModules.toModuleName("../bar.js", "foo/foo.js")); }

  @Test public void testToModuleNameAbsolutePath() {
    assertEquals("module$bar",
        ProcessCommonJSModules.toModuleName("/bar.js", "foo/foo.js")); }

  @Test public void testToModuleNameComplexRelative() {
    assertEquals("module$src$common$util",
        ProcessCommonJSModules.toModuleName("../../common/util", "src/client/app/app.js")); }

  // ---- Integration tests via pass processing ----

  @Test public void testSuffixVarsRenamesExports() throws Exception {
    String result = compile("exports = {};");
    assertTrue(result, result.contains("module$test"));
    assertFalse(result, result.contains("exports")); }

  @Test public void testRequireCallRewritten() throws Exception {
    String result = compile("var foo = require('./bar');");
    assertTrue(result, result.contains("goog.require('module$bar')"));
    assertTrue(result, result.contains("module$bar")); }

  private String compile(String source) throws Exception {
    Compiler compiler = new Compiler();
    compiler.initOptions(new CompilerOptions());
    compiler.parse(SourceFile.fromCode("test.js", source));
    ProcessCommonJSModules pass = new ProcessCommonJSModules(compiler, ".");
    pass.process(null, compiler.getRoot());
    return compiler.toSource(); }
}
