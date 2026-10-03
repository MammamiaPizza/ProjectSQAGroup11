package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class PeepholeFoldConstantsArrayAccessRegressionTest extends TestCase {

  private CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants(false));
  }

  @Test
  public void testArrayAccessAtZeroFoldsFirstElementWithoutError() {
    test("([10, 20])[0]", "10");
  }

  @Test
  public void testArrayAccessAtLastValidIndexFoldsCorrectElement() {
    test("([10, 20])[1]", "20");
  }

  @Test
  public void testArrayAccessToHoleFoldsToUndefined() {
    test("([10, , 30])[1]", "void 0");
  }

  @Test
  public void testArrayAccessWithNonNumericIndexIsNotFolded() {
    testSame("([10, 20])['0']");
  }

  @Test
  public void testArrayAccessAssignmentTargetIsNotFolded() {
    testSame("([10])[0] += 1");
  }

  @Test
  public void testEmptyArrayAccessReportsOutOfBounds() {
    testSame("([])[0]", PeepholeFoldConstants.INDEX_OUT_OF_BOUNDS_ERROR);
  }

  @Test
  public void testFractionalArrayIndexReportsInvalidIndex() {
    testSame("([10])[0.5]", PeepholeFoldConstants.INVALID_GETELEM_INDEX_ERROR);
  }

  private void test(String source, String expected) {
    Compilation actual = compile(source, true);
    assertEquals(0, actual.compiler.getErrorCount());

    Compilation expectedCompilation = compile(expected, false);
    assertEquals(
        expectedCompilation.compiler.toSource(expectedCompilation.root),
        actual.compiler.toSource(actual.root));
  }

  private void testSame(String source) {
    Compilation actual = compile(source, true);
    assertEquals(0, actual.compiler.getErrorCount());

    Compilation expected = compile(source, false);
    assertEquals(
        expected.compiler.toSource(expected.root),
        actual.compiler.toSource(actual.root));
  }

  private void testSame(String source, Object expectedError) {
    Compilation actual = compile(source, true);
    Compilation expected = compile(source, false);

    assertEquals(
        expected.compiler.toSource(expected.root),
        actual.compiler.toSource(actual.root));
    assertEquals(1, actual.compiler.getErrors().length);
    assertEquals(expectedError, actual.compiler.getErrors()[0].type);
  }

  private Compilation compile(String source, boolean optimize) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    if (optimize) {
      getProcessor(compiler).process(null, root);
    }
    return new Compilation(compiler, root);
  }

  private static final class Compilation {
    final Compiler compiler;
    final Node root;

    Compilation(Compiler compiler, Node root) {
      this.compiler = compiler;
      this.root = root;
    }
  }
}
