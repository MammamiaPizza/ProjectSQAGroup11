package com.google.javascript.jscomp;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.jstype.JSTypeRegistry;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class Issue1002Test extends TestCase {

 private Compiler compiler;

 @Override
 public void setUp() {
     compiler = new Compiler();
 }

 /**

 - Compiles the given JS source and asserts that the compiler's errors contain
 - the expected substrings. If no expected substrings, asserts no errors.
 - Fails if an IllegalStateException is thrown during compilation.
   */
  private void compileAndCheck(String js, String... expectedErrors) {
  CompilerOptions options = new CompilerOptions();
  options.setCheckTypes(true);
  List<SourceFile> externs = CommandLineRunner.getDefaultExterns();
  compiler.init(externs, ImmutableList.<SourceFile>of(), options);
  SourceFile[] externFiles = externs.toArray(new SourceFile[0]);
  SourceFile[] jsFiles = { SourceFile.fromCode("test.js", js) };
  Result result;
  try {
  result = compiler.compile(externFiles, jsFiles, null);
  } catch (IllegalStateException e) {
  fail("Unexpected IllegalStateException: " + e.getMessage());
  return;
  }
  JSError[] errors = result.errors;
  if (expectedErrors.length == 0) {
  assertTrue("Unexpected errors: " + Arrays.toString(errors), errors.length == 0);
  } else {
  for (String expected : expectedErrors) {
      boolean found = false;
      for (JSError e : errors) {
          if (e.description.contains(expected)) {
              found = true;
              break;
          }
      }
      assertTrue("Expected error containing '" + expected + "' not found among " +
          Arrays.toString(errors), found);
  }
  }
  }

 public void testInterfaceEmptyBody() {
     String js = "/** @interface */ function I() {}";
     compileAndCheck(js);
 }

 public void testInterfaceMethodWithBody() {
     String js = "/** @interface /\n" +
                  "function I() {}\n" +
                  "/* @return {number} */\n" +
                  "I.prototype.f = function() { return 1; };";
     compileAndCheck(js, "interface members can only be empty property declarations");
 }

 public void testInterfaceMethodEmptyBodyOk() {
     String js = "/** @interface */\n" +
                  "function I() {}\n" +
                  "I.prototype.g = function() {};";
     compileAndCheck(js);
 }

 public void testEnumElementAlreadyDefined() {
     String js = "/** @enum {number} */ var E = { A:1, A:2 };";
     compileAndCheck(js, "enum element A already defined");
 }

 public void testEnumKeyNotSytacticConstant() {
     String js = "var x = 'A'; /** @enum {string} */ var E = { [x]: 'val' };";
     // The expected error message may vary; use a known string
     compileAndCheck(js, "enum key", "syntactic constant");
 }

 public void testEnumNonExistentElement() {
     String js = "/** @enum {number} */ var E = { A:1 };\n" +
                  "var y = E.B;";
     compileAndCheck(js, "element B does not exist on this enum");
 }

 public void testInterfacePropertyRedefinition() {
     String js = "/** @interface */\n" +
                  "function I() {}\n" +
                  "I.prototype.prop = function() {};\n" +
                  "I.prototype.prop = function() {};";
     compileAndCheck(js, "property prop already defined on interface I");
 }

 public void testInterfaceExtendsNotImplements() {
     String js = "/** @interface /\n" +
                  "function I() {}\n" +
                  "/* @interface\n @implements {I} */\n" +
                  "function J() {}";
     compileAndCheck(js, "an interface can only extend, but not implement interfaces");
 }

 public void testReturnTypeMismatchOnInterfaceMethod() {
     String js = "/** @interface /\n" +
                  "function I() {}\n" +
                  "/* @return {number} */\n" +
                  "I.prototype.h = function() { return 'wrong'; };";
     compileAndCheck(js, "return", "number", "string"); // expects a type mismatch error
 }

 public void testTypeofStringCheck() {
     // Verify typeof expressions do not cause exceptions
     String js = "var s = typeof 'hello' === 'string';";
     compileAndCheck(js);
 }

}