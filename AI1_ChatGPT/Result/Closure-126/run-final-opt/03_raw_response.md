package com.google.javascript.jscomp;

import com.google.javascript.jscomp.testing.CompilerTestCase;
import org.junit.Test;

public class MinimizeExitPointsGeneratedTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new MinimizeExitPoints(compiler);
  }

  @Test
  public void testFunctionReturnOptimizationMovesFollowingStatementsIntoElse() {
    test(
        "function f(){if(x)return;else y();z();}",
        "function f(){if(x);else{y();z()}}");
  }

  @Test
  public void testTrailingBareFunctionReturnIsRemoved() {
    test("function f(){x();return;}", "function f(){x()}");
  }

  @Test
  public void testValuedFunctionReturnIsNotRemoved() {
    testSame("function f(){x();return 1;}");
  }

  @Test
  public void testContinueOptimizationMovesFollowingStatementsIntoElse() {
    test(
        "while(x){if(y)continue;z();}",
        "while(x){if(y);else{z()}}");
  }

  @Test
  public void testFalseDoLoopBreakOptimizationMovesFollowingStatementsIntoElse() {
    test(
        "do{if(x)break;foo()}while(false);",
        "do{if(x);else{foo()}}while(false);");
  }

  @Test
  public void testLabeledBreakOptimizationMovesFollowingStatementsIntoElse() {
    test(
        "L:{if(x)break L;foo();}",
        "L:{if(x);else{foo()}}");
  }

  @Test
  public void testDontRemoveBreakInTryFinally() {
    testSame("do{try{foo()}finally{break;}}while(false);");
  }

  @Test
  public void testDontRemoveReturnInFinally() {
    testSame("function f(){try{foo()}finally{return;}}");
  }
}