package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import org.junit.Test;

public class FunctionTypeBackwardsTypedefTest {

  private Result compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    return compiler.compile(
        SourceFile.fromCode("externs.js", ""),
        SourceFile.fromCode("input.js", source),
        options);
  }

  @Test
  public void testOrdinaryThisTypeCompilesNormally() {
    Result result =
        compile(
            "/** @this {Object} */\n"
                + "function f() {}\n");

    assertNotNull(result);
    assertEquals(0, result.errors.length);
  }

  @Test
  public void testBackwardsTypedefToStringInThisTypeDoesNotCrash() {
    Result result =
        compile(
            "/** @this {LaterType} */\n"
                + "function f() {}\n"
                + "/** @typedef {string} */\n"
                + "var LaterType;\n");

    assertNotNull("Resolving a backwards typedef to string must complete", result);
    assertNotNull(result.errors);
  }

  @Test
  public void testBackwardsTypedefToUnionInThisTypeDoesNotCrash() {
    Result result =
        compile(
            "/** @this {LaterType} */\n"
                + "function f() {}\n"
                + "/** @typedef {(string|number)} */\n"
                + "var LaterType;\n");

    assertNotNull("Resolving a backwards typedef to a union must complete", result);
    assertNotNull(result.errors);
  }

  @Test
  public void testBackwardsTypedefToUnionWithObjectMemberInThisTypeDoesNotCrash() {
    Result result =
        compile(
            "/** @this {LaterType} */\n"
                + "function f() {}\n"
                + "/** @typedef {(string|Object)} */\n"
                + "var LaterType;\n");

    assertNotNull(
        "Resolving a backwards typedef whose union includes an object must complete", result);
    assertNotNull(result.errors);
  }
}
