package com.google.javascript.jscomp;

import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class FunctionTypeBackwardsTypedefTest {

  @Test
  public void testBackwardsTypedefUseAcceptsCompatibleArgument() {
    Result result = compile(
        "/** @param {Fn} x */ function g(x) {}\n"
        + "/** @typedef {function(string): number} */ var Fn;\n"
        + "/** @param {string} x @return {number} */ function f(x) { return 0; }\n"
        + "g(f);");

    TestCase.assertEquals(0, result.errors.length);
    TestCase.assertEquals(0, result.warnings.length);
  }

  @Test
  public void testBackwardsTypedefUseRejectsIncompatibleArgument() {
    Result result = compile(
        "/** @param {Fn} x */ function g(x) {}\n"
        + "/** @typedef {function(string): number} */ var Fn;\n"
        + "/** @param {number} x @return {number} */ function f(x) { return 0; }\n"
        + "g(f);");

    TestCase.assertEquals(0, result.errors.length);
    TestCase.assertEquals(1, result.warnings.length);
    TestCase.assertEquals(
        "actual parameter 1 of g does not match formal parameter\n"
        + "found   : function (number): number\n"
        + "required: function (string): number",
        result.warnings[0].description);
  }

  private Result compile(String source) {
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;

    Compiler compiler = new Compiler();
    return compiler.compile(
        Collections.singletonList(SourceFile.fromCode("externs.js", "")),
        Collections.singletonList(SourceFile.fromCode("test.js", source)),
        options);
  }
}