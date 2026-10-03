package com.google.javascript.jscomp;

import org.junit.Test;

public final class ControlFlowAnalysisInstanceofRegressionTest
    extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckUnreachableCode(compiler, CheckLevel.WARNING);
  }

  @Test
  public void testInstanceofInTryCanReachCatch() {
    testSame(
        "try {"
            + "  left instanceof right;"
            + "} catch (e) {"
            + "  e;"
            + "}");
  }

  @Test
  public void testInstanceofInTryCanReachFollowingCatchStatements() {
    testSame(
        "try {"
            + "  left instanceof right;"
            + "} catch (e) {"
            + "  first;"
            + "  second;"
            + "}");
  }

  @Test
  public void testInstanceofConditionCanReachCatch() {
    testSame(
        "try {"
            + "  if (left instanceof right) {"
            + "    thenBranch;"
            + "  }"
            + "} catch (e) {"
            + "  handled;"
            + "}");
  }

  @Test
  public void testInstanceofWithoutHandlerDoesNotMakeFollowingCodeUnreachable() {
    testSame(
        "left instanceof right;"
            + "followingStatement;");
  }

  @Test
  public void testInstanceofTryCatchFinallyKeepsAllHandlersReachable() {
    testSame(
        "try {"
            + "  left instanceof right;"
            + "} catch (e) {"
            + "  handled;"
            + "} finally {"
            + "  cleanup;"
            + "}"
            + "afterTry;");
  }
}