package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Charsets;
import org.junit.Test;

public class CommandLineRunnerCharsetTest {

  @Test
  public void charsetFlagSetsInputAndOutputCharsets() {
    CommandLineRunner runner =
        new CommandLineRunner(new String[] {"--charset", "US-ASCII"});

    CompilerOptions options = runner.createOptions();

    assertEquals(Charsets.US_ASCII, options.inputCharset);
    assertEquals(Charsets.US_ASCII, options.outputCharset);
  }
}
