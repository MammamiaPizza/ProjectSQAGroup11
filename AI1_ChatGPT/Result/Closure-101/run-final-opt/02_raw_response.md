package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import junit.framework.TestCase;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class CommandLineRunnerProcessClosurePrimitivesTest extends TestCase {

  private CompilerOptions createOptions(String... args) throws Exception {
    PrintStream output = new PrintStream(new ByteArrayOutputStream());
    PrintStream errors = new PrintStream(new ByteArrayOutputStream());
    return new CommandLineRunner(args, output, errors).createOptions();
  }

  @Test
  public void testProcessClosurePrimitivesEnablesClosurePass() throws Exception {
    CompilerOptions options = createOptions("--process_closure_primitives");

    assertTrue(options.closurePass);
  }

  @Test
  public void testProcessClosurePrimitivesAcceptsExplicitTrue() throws Exception {
    CompilerOptions options = createOptions("--process_closure_primitives=true");

    assertTrue(options.closurePass);
  }

  @Test
  public void testProcessClosurePrimitivesAcceptsExplicitFalse() throws Exception {
    CompilerOptions options = createOptions("--process_closure_primitives=false");

    assertFalse(options.closurePass);
  }

  @Test
  public void testProcessClosurePrimitivesIsDisabledByDefault() throws Exception {
    CompilerOptions options = createOptions();

    assertFalse(options.closurePass);
  }

  @Test(expected = CmdLineException.class)
  public void testProcessClosurePrimitivesRejectsInvalidBooleanValue() throws Exception {
    createOptions("--process_closure_primitives=not-a-boolean");
  }
}