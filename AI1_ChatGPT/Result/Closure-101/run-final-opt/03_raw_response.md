package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import junit.framework.TestCase;
import org.junit.Test;

public class CommandLineRunnerProcessClosurePrimitivesTest extends TestCase {

  private CompilerOptions createOptions(String... args) throws Exception {
    PrintStream output = new PrintStream(new ByteArrayOutputStream());
    PrintStream errors = new PrintStream(new ByteArrayOutputStream());
    return new CommandLineRunner(args, output, errors).createOptions();
  }

  @Test
  public void testProcessClosurePrimitivesWithoutValueDisablesClosurePass() throws Exception {
    CompilerOptions options = createOptions("--process_closure_primitives");

    assertFalse(options.closurePass);
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
  public void testProcessClosurePrimitivesIsEnabledByDefault() throws Exception {
    CompilerOptions options = createOptions();

    assertTrue(options.closurePass);
  }

  @Test
  public void testProcessClosurePrimitivesTreatsInvalidBooleanValueAsFalse() throws Exception {
    CompilerOptions options = createOptions("--process_closure_primitives=not-a-boolean");

    assertFalse(options.closurePass);
  }
}