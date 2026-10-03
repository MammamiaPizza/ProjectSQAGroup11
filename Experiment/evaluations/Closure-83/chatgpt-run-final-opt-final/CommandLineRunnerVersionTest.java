package com.google.javascript.jscomp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandLineRunnerVersionTest {

  @Test
  public void versionFlagPrintsVersionInformationAndPreventsCompilation() {
    ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
    CommandLineRunner runner = new CommandLineRunner(
        new String[] {"--version"},
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(errorBytes));

    assertTrue(runner.shouldRunCompiler());

    String output = errorBytes.toString();
    assertTrue(output.contains("Closure Compiler"));
    assertTrue(output.contains("Version:"));
    assertTrue(output.contains("Built on:"));
  }

  @Test
  public void versionFlagBeforeInputFlagPreventsCompilation() {
    CommandLineRunner runner = new CommandLineRunner(
        new String[] {"--version", "--js", "input.js"},
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(new ByteArrayOutputStream()));

    assertTrue(runner.shouldRunCompiler());
  }

  @Test
  public void versionFlagAfterInputFlagPreventsCompilation() {
    CommandLineRunner runner = new CommandLineRunner(
        new String[] {"--js", "input.js", "--version"},
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(new ByteArrayOutputStream()));

    assertTrue(runner.shouldRunCompiler());
  }

  @Test
  public void explicitFalseVersionValueDoesNotRequestVersion() {
    CommandLineRunner runner = new CommandLineRunner(
        new String[] {"--version=false"},
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(new ByteArrayOutputStream()));

    assertTrue(runner.shouldRunCompiler());
  }

  @Test
  public void unrecognizedOptionMakesConfigurationInvalid() {
    ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
    CommandLineRunner runner = new CommandLineRunner(
        new String[] {"--not_a_real_option"},
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(errorBytes));

    assertFalse(runner.shouldRunCompiler());
    assertTrue(errorBytes.size() > 0);
  }

  @Test
  public void emptyArgumentsLeaveConfigurationRunnable() {
    CommandLineRunner runner = new CommandLineRunner(
        new String[0],
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(new ByteArrayOutputStream()));

    assertTrue(runner.shouldRunCompiler());
  }
}
