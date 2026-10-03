package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public final class ControlFlowAnalysisInstanceofRegressionTest {

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());

    compiler.parseInputs();
    Node root = compiler.getRoot();
    new CheckUnreachableCode(compiler, CheckLevel.WARNING)
        .process(root.getFirstChild(), root.getLastChild());

    assertEquals(0, compiler.getErrors().length);
    assertEquals(0, compiler.getWarnings().length);
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