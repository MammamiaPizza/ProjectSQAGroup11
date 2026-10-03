package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class PeepholeFoldConstantsGeneratedTest {

  private void test(String input, String expected) {
    assertEquals(compile(expected, false), compile(input, true));
  }

  private void testSame(String input) {
    test(input, input);
  }

  private String compile(String source, boolean optimize) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);

    Node root = compiler.parseInputs();
    assertEquals(0, compiler.getErrorCount());

    Node externs = root.getFirstChild();
    Node js = externs.getNext();

    if (optimize) {
      CompilerPass pass =
          new PeepholeOptimizationsPass(compiler, new PeepholeFoldConstants());
      pass.process(externs, js);
      pass.process(externs, js);
    }

    assertEquals(0, compiler.getErrorCount());
    return compiler.toSource(js);
  }

  @Test
  public void testFoldsFirstArrayElement() {
    test("[0, 1][0]", "0");
  }

  @Test
  public void testFoldsLastArrayElement() {
    test("[0, 1][1]", "1");
  }

  @Test
  public void testFoldsArrayElementAfterHole() {
    test("[0, , 2][2]", "2");
  }

  @Test
  public void testDoesNotFoldAssignmentTargetPastCurrentArrayLength() {
    testSame("[0][1] = 1");
  }

  @Test
  public void testDoesNotFoldCompoundAssignmentTargetPastCurrentArrayLength() {
    testSame("[0][1] += 1");
  }

  @Test
  public void testDoesNotFoldAssignmentTargetAtExistingArrayIndex() {
    testSame("[0][0] = 1");
  }
}
