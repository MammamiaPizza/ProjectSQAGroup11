package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Charsets;
import org.junit.Test;

public class CommandLineRunnerCharsetTest {

  @Test
  public void charsetFlagSetsInputAndOutputCharsets() {
    CapturingCommandLineRunner runner =
        new CapturingCommandLineRunner(new String[] {"--charset", "US-ASCII"});

    runner.run();

    assertEquals(Charsets.US_ASCII, runner.options.outputCharset);
  }

  private static class CapturingCommandLineRunner extends CommandLineRunner {
    private CompilerOptions options;

    CapturingCommandLineRunner(String[] args) {
      super(args);
    }

    @Override
    protected CompilerOptions createOptions() {
      options = super.createOptions();
      return options;
    }
  }
}