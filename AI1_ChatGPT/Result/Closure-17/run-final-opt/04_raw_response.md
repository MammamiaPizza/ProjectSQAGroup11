@org.junit.Test
public void testPrototypeObjectLiteralFunctionReturnTypeMismatchIsReported() {
  com.google.javascript.jscomp.Compiler compiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions options =
      new com.google.javascript.jscomp.CompilerOptions();
  options.checkTypes = true;

  compiler.compile(
      java.util.Collections.singletonList(
          com.google.javascript.jscomp.SourceFile.fromCode("externs.js", "")),
      java.util.Collections.singletonList(
          com.google.javascript.jscomp.SourceFile.fromCode(
              "input.js",
              "/** @constructor */ function Foo() {}\n"
                  + "Foo.prototype = {\n"
                  + "  /** @return {string} */\n"
                  + "  bar: function() { return 1; }\n"
                  + "};")),
      options);

  boolean foundInconsistentReturnType = false;
  for (com.google.javascript.jscomp.JSError warning : compiler.getWarnings()) {
    if (warning.description.contains("inconsistent return type")) {
      foundInconsistentReturnType = true;
      break;
    }
  }
  junit.framework.Assert.assertTrue(foundInconsistentReturnType);
}