package com.google.javascript.jscomp;

import java.util.Collections;
import junit.framework.TestCase;

public class PeepholeFoldConstantsDivisionRegressionTest extends TestCase {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants());
  }

  private void test(String source, String expected) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);

    getProcessor(compiler).process(
        compiler.getExternsRoot(), compiler.getJsRoot());

    assertEquals(0, compiler.getErrorCount());
    assertEquals(
        expected.replaceAll("\\s+", ""),
        compiler.toSource().replaceAll("\\s+", ""));
  }

  public void testDivisionWithZeroResultAndNonZeroDivisor() {
    test("var result = 0 / 8;", "var result = 0;");
  }

  public void testNonzeroConstantDivisionFoldsToInteger() {
    test("var result = 8 / 2;", "var result = 4;");
  }

  public void testFractionalConstantDivisionFolds() {
    test("var result = 1 / 2;", "var result = 0.5;");
  }
}
