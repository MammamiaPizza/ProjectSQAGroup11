@org.junit.Test
public void doesNotApplyStrictDirectiveFromOneInputToFollowingInput() {
  com.google.javascript.jscomp.Compiler compiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions options =
      new com.google.javascript.jscomp.CompilerOptions();
  options.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT5);

  compiler.compile(
      java.util.Collections.<com.google.javascript.jscomp.SourceFile>emptyList(),
      java.util.Arrays.asList(
          com.google.javascript.jscomp.SourceFile.fromCode(
              "strict.js", "\"use strict\";\nvar value = 1;\n"),
          com.google.javascript.jscomp.SourceFile.fromCode(
              "nonstrict.js", "with (value) { value = 2; }\n")),
      options);

  junit.framework.Assert.assertEquals(0, compiler.getErrorCount());
}