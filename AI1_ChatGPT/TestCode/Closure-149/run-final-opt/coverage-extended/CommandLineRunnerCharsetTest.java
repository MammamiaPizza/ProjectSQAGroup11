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

@Test
public void setLooseTypesIsRetainedWhenOptionsAreCloned() throws Exception {
  com.google.javascript.jscomp.CompilerOptions options =
      new com.google.javascript.jscomp.CompilerOptions();
  java.lang.reflect.Field looseTypes =
      com.google.javascript.jscomp.CompilerOptions.class.getDeclaredField("looseTypes");
  looseTypes.setAccessible(true);

  options.setLooseTypes(true);
  junit.framework.Assert.assertTrue(looseTypes.getBoolean(options));

  com.google.javascript.jscomp.CompilerOptions cloned =
      (com.google.javascript.jscomp.CompilerOptions) options.clone();
  junit.framework.Assert.assertTrue(looseTypes.getBoolean(cloned));

  options.setLooseTypes(false);
  junit.framework.Assert.assertFalse(looseTypes.getBoolean(options));
  junit.framework.Assert.assertTrue(looseTypes.getBoolean(cloned));
}
}
