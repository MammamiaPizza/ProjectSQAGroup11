package com.google.javascript.jscomp;

import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FoldConstantsStringJoinRegressionTest {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new FoldConstants(compiler);
  }

  private void test(String source, String expected) {
    assertEquals(compile(expected, false), compile(source, true));
  }

  private void testSame(String source) {
    test(source, source);
  }

  private String compile(String source, boolean fold) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());

    if (fold) {
      getProcessor(compiler).process(null, compiler.parseInputs());
    } else {
      compiler.parseInputs();
    }

    return compiler.toSource();
  }

  @Test
  public void testFoldsAdjacentStringSuffixAfterUnknownValue() {
    test(
        "var x = a + 'b' + 'c';",
        "var x = a + 'bc';");
  }

  @Test
  public void testFoldsLongAdjacentStringSuffixAfterUnknownValue() {
    test(
        "var x = a + 'x' + 'y' + 'z';",
        "var x = a + 'xyz';");
  }

  @Test
  public void testFoldsStringAndNumericLiteralSuffixAfterUnknownValue() {
    test(
        "var x = a + 'x' + 1;",
        "var x = a + 'x1';");
  }

  @Test
  public void testFoldsEmptyStringSuffix() {
    test(
        "var x = a + '' + 'x';",
        "var x = a + 'x';");
  }

  @Test
  public void testDoesNotFoldWhenPriorAdditionMayBeNumeric() {
    testSame("var x = a + 1 + 'x';");
  }

  @Test
  public void testFoldsFullyConstantStringAddition() {
    test(
        "var x = 'a' + 'b' + 'c';",
        "var x = 'abc';");
  }
}
