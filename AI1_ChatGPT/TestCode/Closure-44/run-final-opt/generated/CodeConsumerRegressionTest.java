package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import junit.framework.TestCase;

public class CodeConsumerRegressionTest extends TestCase {

  public void testIssue620PreservesSpaceInRegexCharacterClass() {
    assertEquals("alert(/ //[ ]/ /)", print("alert(/ //[]/ /)"));
  }

  public void testRegexCharacterClassContainingOnlySpaceIsNotCollapsed() {
    assertEquals("alert(/[ ]/)", print("alert(/[ ]/)"));
  }

  public void testRegularRegexCharacterClassPrintsWithoutExtraSpacing() {
    assertEquals("alert(/[a]/)", print("alert(/[a]/)"));
  }

  private String print(String source) {
    Compiler compiler = new Compiler();
    Node root = compiler.parseTestCode(source);
    return new CodePrinter.Builder(root).build();
  }
}
