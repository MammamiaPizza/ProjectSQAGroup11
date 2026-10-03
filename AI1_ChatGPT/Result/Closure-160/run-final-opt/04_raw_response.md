@org.junit.Test
public void testAcceptEcmaScript5MatchesConfiguredLanguageMode() {
  com.google.javascript.jscomp.Compiler ecmaScript3Compiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions ecmaScript3Options =
      new com.google.javascript.jscomp.CompilerOptions();
  ecmaScript3Options.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT3);
  ecmaScript3Compiler.initOptions(ecmaScript3Options);
  org.junit.Assert.assertFalse(ecmaScript3Compiler.acceptEcmaScript5());

  com.google.javascript.jscomp.Compiler ecmaScript5Compiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions ecmaScript5Options =
      new com.google.javascript.jscomp.CompilerOptions();
  ecmaScript5Options.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT5);
  ecmaScript5Compiler.initOptions(ecmaScript5Options);
  org.junit.Assert.assertTrue(ecmaScript5Compiler.acceptEcmaScript5());

  com.google.javascript.jscomp.Compiler strictCompiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions strictOptions =
      new com.google.javascript.jscomp.CompilerOptions();
  strictOptions.setLanguageIn(
      com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
  strictCompiler.initOptions(strictOptions);
  org.junit.Assert.assertTrue(strictCompiler.acceptEcmaScript5());
}

@org.junit.Test
public void testAcceptConstKeywordMatchesConfiguredOption() {
  com.google.javascript.jscomp.Compiler rejectingCompiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions rejectingOptions =
      new com.google.javascript.jscomp.CompilerOptions();
  rejectingOptions.setAcceptConstKeyword(false);
  rejectingCompiler.initOptions(rejectingOptions);
  org.junit.Assert.assertFalse(rejectingCompiler.acceptConstKeyword());

  com.google.javascript.jscomp.Compiler acceptingCompiler =
      new com.google.javascript.jscomp.Compiler();
  com.google.javascript.jscomp.CompilerOptions acceptingOptions =
      new com.google.javascript.jscomp.CompilerOptions();
  acceptingOptions.setAcceptConstKeyword(true);
  acceptingCompiler.initOptions(acceptingOptions);
  org.junit.Assert.assertTrue(acceptingCompiler.acceptConstKeyword());
}