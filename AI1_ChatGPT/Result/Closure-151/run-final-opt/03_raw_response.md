package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class CommandLineRunnerVersionFlagTest {

  @Test
  public void versionFlagIsRecognizedAsAnInformationalNonCompilerInvocation() {
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();

    CommandLineRunner runner =
        new CommandLineRunner(
            new String[] {"--version"},
            new PrintStream(outBytes),
            new PrintStream(errBytes));

    assertFalse(runner.shouldRunCompiler());

    String output = outBytes.toString() + errBytes.toString();
    assertFalse(output.contains("Unknown option"));
  }

  @Test
  public void noArgumentsLeavesCompilerRunnable() {
    CommandLineRunner runner =
        new CommandLineRunner(
            new String[] {},
            new PrintStream(new ByteArrayOutputStream()),
            new PrintStream(new ByteArrayOutputStream()));

    assertTrue(runner.shouldRunCompiler());
  }

  @Test
  public void unknownOptionMakesConfigurationInvalid() {
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    CommandLineRunner runner =
        new CommandLineRunner(
            new String[] {"--definitely_unknown_option"},
            new PrintStream(new ByteArrayOutputStream()),
            new PrintStream(errBytes));

    assertFalse(runner.shouldRunCompiler());
    assertTrue(errBytes.toString().length() > 0);
  }

  @Test
  public void helpFlagDoesNotRunCompiler() {
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    CommandLineRunner runner =
        new CommandLineRunner(
            new String[] {"--help"},
            new PrintStream(new ByteArrayOutputStream()),
            new PrintStream(errBytes));

    assertFalse(runner.shouldRunCompiler());
    assertTrue(errBytes.toString().length() > 0);
  }
}