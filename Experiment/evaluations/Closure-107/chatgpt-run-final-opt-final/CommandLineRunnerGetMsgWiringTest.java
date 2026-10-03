package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

import org.junit.Test;

public class CommandLineRunnerGetMsgWiringTest {

  private static final class TestCommandLineRunner extends CommandLineRunner {
    TestCommandLineRunner(String[] args, PrintStream out, PrintStream err) {
      super(args, out, err);
    }

    CompilerOptions getOptionsForTest() {
      return createOptions();
    }

    Compiler getCompilerForTest() {
      return createCompiler();
    }
  }

  private TestCommandLineRunner newRunner(String[] args) {
    PrintStream output = new PrintStream(new ByteArrayOutputStream());
    PrintStream errors = new PrintStream(new ByteArrayOutputStream());
    return new TestCommandLineRunner(args, output, errors);
  }

  private Compiler compileGetMsg(TestCommandLineRunner runner) {
    Compiler compiler = runner.getCompilerForTest();
    SourceFile input = SourceFile.fromCode(
        "getmsg.js",
        "var goog = {};\n"
            + "goog.getMsg = function(message) { return message; };\n"
            + "var MSG_HELLO = goog.getMsg('Hello');\n");

    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(input),
        runner.getOptionsForTest());
    return compiler;
  }

  @Test
  public void testDefaultConfigurationCompilesGetMsgWithoutDiagnostics() {
    TestCommandLineRunner runner = newRunner(new String[] {});

    assertTrue(runner.shouldRunCompiler());

    Compiler compiler = compileGetMsg(runner);
    assertEquals(0, compiler.getErrorCount());
    assertEquals(0, compiler.getWarningCount());
  }

  @Test
  public void testAdvancedConfigurationCompilesGetMsgWithoutDiagnostics() {
    TestCommandLineRunner runner = newRunner(
        new String[] {"--compilation_level", "ADVANCED_OPTIMIZATIONS"});

    assertTrue(runner.shouldRunCompiler());

    Compiler compiler = compileGetMsg(runner);
    assertEquals(0, compiler.getErrorCount());
    assertEquals(0, compiler.getWarningCount());
  }

  @Test
  public void testInvalidCompilationLevelPreventsCompilerExecution() {
    TestCommandLineRunner runner = newRunner(
        new String[] {"--compilation_level", "NOT_A_COMPILATION_LEVEL"});

    assertFalse(runner.shouldRunCompiler());
  }
}
