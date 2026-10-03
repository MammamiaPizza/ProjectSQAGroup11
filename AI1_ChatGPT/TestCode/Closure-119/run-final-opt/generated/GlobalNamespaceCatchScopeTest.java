package com.google.javascript.jscomp;

import org.junit.Test;

public class GlobalNamespaceCatchScopeTest extends CompilerTestCase {

  public GlobalNamespaceCatchScopeTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckGlobalNames(compiler, CheckGlobalNames.CheckLevel.WARNING);
  }

  @Test
  public void testCatchParameterDirectReadIsNotGlobal() {
    testSame("try { throw 1; } catch (e) { e; }");
  }

  @Test
  public void testCatchParameterPropertyReadIsNotGlobal() {
    testSame("try { throw {}; } catch (e) { e.message; }");
  }

  @Test
  public void testCatchParameterAssignmentAndIncrementAreNotGlobal() {
    testSame("try { throw 0; } catch (e) { e = 1; e++; }");
  }

  @Test
  public void testCatchParameterCapturedByNestedFunctionIsNotGlobal() {
    testSame(
        "try { throw 1; } catch (e) {"
            + "  (function() { return e; })();"
            + "}");
  }

  @Test
  public void testCatchParameterDoesNotEscapeCatchScope() {
    testSame(
        "try { throw 1; } catch (e) { e; }"
            + "e;",
        CheckGlobalNames.UNDEFINED_NAME);
  }
}
