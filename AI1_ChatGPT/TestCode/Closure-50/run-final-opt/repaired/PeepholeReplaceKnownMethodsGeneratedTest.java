package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;

public class PeepholeReplaceKnownMethodsGeneratedTest extends TestCase {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeReplaceKnownMethods());
  }

  private void test(String source, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    Node externsRoot = root.getFirstChild();
    Node jsRoot = externsRoot.getNext();
    getProcessor(compiler).process(externsRoot, jsRoot);

    Compiler expectedCompiler = new Compiler();
    expectedCompiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("expected", expected)),
        new CompilerOptions());
    Node expectedRoot = expectedCompiler.parseInputs();
    Node expectedExternsRoot = expectedRoot.getFirstChild();
    Node expectedJsRoot = expectedExternsRoot.getNext();

    assertTrue(expectedJsRoot.isEquivalentTo(jsRoot));
  }

  @Test
  public void testFoldsLiteralArrayJoinWithDefaultSeparator() {
    test("['a', 'b'].join()", "'a,b'");
  }

  @Test
  public void testFoldsLiteralArrayJoinWithExplicitStringSeparator() {
    test("['a', 'b', 'c'].join('-')", "'a-b-c'");
  }

  @Test
  public void testFoldsJoinContainingStringLiteralAddition() {
    test("['a' + 'b', 'c'].join('-')", "'ab-c'");
  }

  @Test
  public void testFoldsJoinContainingStringAndNumberAdditions() {
    test("['x' + 1, 2 + 'y'].join('|')", "'x1|2y'");
  }

  @Test
  public void testFoldsJoinOfEmptyArray() {
    test("[].join('-')", "''");
  }

  @Test
  public void testDoesNotFoldJoinOnStringReceiver() {
    test("'ab'.join(',')", "'ab'.join(',')");
  }

  @Test
  public void testDoesNotFoldJoinWithNonStringSeparator() {
    test("['a', 'b'].join(1)", "['a', 'b'].join(1)");
  }

  @Test
  public void testDoesNotFoldJoinWithMultipleArguments() {
    test("['a', 'b'].join('-', ',')", "['a', 'b'].join('-', ',')");
  }
}
