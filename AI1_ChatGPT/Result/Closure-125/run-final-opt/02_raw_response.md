package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import org.junit.Test;

public class TypeCheckIssue1002RegressionTest {

  @Test
  public void testIssue1002StyleTypedPrototypeTraversalCompletes() {
    String source =
        "/** @constructor */\n"
            + "function Issue1002() {}\n"
            + "/** @type {function(number): number} */\n"
            + "Issue1002.prototype.transform = function(value) {\n"
            + "  return value;\n"
            + "};\n"
            + "/** @param {Issue1002} instance */\n"
            + "function invoke(instance) {\n"
            + "  return (function() {\n"
            + "    return instance.transform(1);\n"
            + "  })();\n"
            + "}\n"
            + "invoke(new Issue1002());\n";

    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);

    Compiler compiler = new Compiler();
    Result result =
        compiler.compile(
            Collections.<SourceFile>emptyList(),
            Collections.singletonList(SourceFile.fromCode("issue1002.js", source)),
            options);

    assertNotNull(result);
    assertEquals(0, result.errors.length);
  }
}