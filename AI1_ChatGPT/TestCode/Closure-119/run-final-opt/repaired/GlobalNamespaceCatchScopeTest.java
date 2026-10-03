package com.google.javascript.jscomp;

import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GlobalNamespaceCatchScopeTest {

  private void testSame(String source) {
    Compiler compiler = runCheck(source);
    assertEquals(0, compiler.getWarnings().length);
  }

  private void testSame(String source, DiagnosticType expectedWarning) {
    Compiler compiler = runCheck(source);
    assertEquals(1, compiler.getWarnings().length);
    assertEquals(expectedWarning, compiler.getWarnings()[0].type);
  }

  private Compiler runCheck(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    compiler.parseInputs();
    new CheckGlobalNames(compiler, CheckGlobalNames.CheckLevel.WARNING).process(
        compiler.getRoot().getFirstChild(), compiler.getRoot().getLastChild());
    return compiler;
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
