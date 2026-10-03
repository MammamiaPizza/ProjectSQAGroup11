package com.google.javascript.jscomp;

import java.util.Collections;
import junit.framework.TestResult;
import org.junit.Assert;
import org.junit.Test;

public class TypeInferenceIssue669RegressionTest {

  @Test
  public void issue669ProducesNoUnexpectedWarnings() {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;

    Result result =
        compiler.compile(
            Collections.<SourceFile>emptyList(),
            Collections.singletonList(
                SourceFile.fromCode(
                    "issue669.js",
                    "/**\n"
                        + " * @param {string=} opt_value\n"
                        + " * @return {string}\n"
                        + " */\n"
                        + "function issue669(opt_value) {\n"
                        + "  return opt_value || '';\n"
                        + "}\n")),
            options);

    Assert.assertEquals(
        "Issue 669 must compile without type-check errors.", 0, result.errors.length);
    Assert.assertEquals(
        "Issue 669 must compile without unexpected type-check warnings.", 0, result.warnings.length);
  }
}