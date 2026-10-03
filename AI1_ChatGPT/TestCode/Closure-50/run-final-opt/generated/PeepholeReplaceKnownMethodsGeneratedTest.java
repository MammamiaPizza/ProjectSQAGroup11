package com.google.javascript.jscomp;

import org.junit.Test;

public class PeepholeReplaceKnownMethodsGeneratedTest extends CompilerTestCase {

  public PeepholeReplaceKnownMethodsGeneratedTest() {
    super("", "");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeReplaceKnownMethods());
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
