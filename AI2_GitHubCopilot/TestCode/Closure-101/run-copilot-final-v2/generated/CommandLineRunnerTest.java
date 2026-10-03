package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import org.kohsuke.args4j.CmdLineException;
 import org.kohsuke.args4j.CmdLineParser;
 import org.kohsuke.args4j.Option;

 /**
  * Tests for flag parsing and option handling of --process_closure_primitives
  * in CommandLineRunner.
  */
 public class CommandLineRunnerTest extends TestCase {

   /** Helper to construct a runner with silent output streams. */
   private static TestableRunner createRunner(String... args) throws CmdLineException {
     return new TestableRunner(args);
   }

   /**
    * Subclass that exposes the protected createOptions() method so we can
    * inspect the resulting CompilerOptions without actually running the compiler.
    */
   private static class TestableRunner extends CommandLineRunner {
     TestableRunner(String[] args) throws CmdLineException {
       super(args,
             new PrintStream(new ByteArrayOutputStream()),
             new PrintStream(new ByteArrayOutputStream()));
     }

     @Override
     public CompilerOptions createOptions() {
       return super.createOptions();
     }
   }

   // -- Flag parsing & options exercises -----------------------------------

   /** Flag present without explicit value (defaults to true). */
   public void testProcessClosurePrimitives_FlagPresent() throws Exception {
     TestableRunner runner = createRunner("--js=foo.js", "--process_closure_primitives");
     CompilerOptions options = runner.createOptions();
     assertTrue("closurePass should be true when flag is present", options.closurePass);
   }

   /** Flag absent – expected default false. */
   public void testProcessClosurePrimitives_FlagAbsent() throws Exception {
     TestableRunner runner = createRunner("--js=foo.js");
     CompilerOptions options = runner.createOptions();
     assertFalse("closurePass should be false by default", options.closurePass);
   }

   /** Explicitly set to true via --process_closure_primitives=true. */
   public void testProcessClosurePrimitives_ExplicitTrue() throws Exception {
     TestableRunner runner = createRunner("--js=foo.js", "--process_closure_primitives=true");
     CompilerOptions options = runner.createOptions();
     assertTrue("closurePass should be true when set to true", options.closurePass);
   }

   /** Explicitly set to false via --process_closure_primitives=false. */
   public void testProcessClosurePrimitives_ExplicitFalse() throws Exception {
     TestableRunner runner = createRunner("--js=foo.js", "--process_closure_primitives=false");
     CompilerOptions options = runner.createOptions();
     assertFalse("closurePass should be false when explicitly set to false", options.closurePass);
   }

   /** Invalid value should produce a CmdLineException. */
   public void testProcessClosurePrimitives_InvalidValue() {
     try {
       createRunner("--js=foo.js", "--process_closure_primitives=invalid");
       fail("Expected CmdLineException for an invalid boolean value");
     } catch (CmdLineException e) {
       // expected
       assertTrue(e.getMessage().toLowerCase().contains("illegal boolean value"));
     }
   }

   /** The flag combined with other common flags still correctly sets closurePass. */
   public void testProcessClosurePrimitives_WithOtherFlags() throws Exception {
     TestableRunner runner = createRunner(
         "--js=input.js",
         "--externs=extern.js",
         "--js_output_file=output.js",
         "--process_closure_primitives"
     );
     CompilerOptions options = runner.createOptions();
     assertTrue("closurePass should be true alongside other flags", options.closurePass);
   }

   /** No arguments at all – no exception, closurePass should be false. */
   public void testNoArgs() throws Exception {
     TestableRunner runner = createRunner();
     CompilerOptions options = runner.createOptions();
     assertFalse("closurePass should be false when no args given", options.closurePass);
   }

   // -- BooleanOptionHandler direct tests ----------------------------------

   /** Simple bean that uses BooleanOptionHandler for parsing tests. */
   public static class BooleanHolder {
     @Option(name = "--test",
             handler = CommandLineRunner.BooleanOptionHandler.class)
     public boolean value;
   }

   /** Null value (flag present without argument) – should set true. */
   public void testBooleanOptionHandler_NullValue() throws Exception {
     BooleanHolder holder = new BooleanHolder();
     CmdLineParser parser = new CmdLineParser(holder);
     parser.parseArgument(new String[] {"--test"});
     assertTrue("null value should be interpreted as true", holder.value);
   }

   /** Explicit "true" value. */
   public void testBooleanOptionHandler_TrueValue() throws Exception {
     BooleanHolder holder = new BooleanHolder();
     CmdLineParser parser = new CmdLineParser(holder);
     parser.parseArgument(new String[] {"--test=true"});
     assertTrue("'true' value should set to true", holder.value);
   }

   /** Explicit "false" value. */
   public void testBooleanOptionHandler_FalseValue() throws Exception {
     BooleanHolder holder = new BooleanHolder();
     CmdLineParser parser = new CmdLineParser(holder);
     parser.parseArgument(new String[] {"--test=false"});
     assertFalse("'false' value should set to false", holder.value);
   }

   /** Invalid value should throw CmdLineException. */
   public void testBooleanOptionHandler_InvalidValue() {
     BooleanHolder holder = new BooleanHolder();
     CmdLineParser parser = new CmdLineParser(holder);
     try {
       parser.parseArgument(new String[] {"--test=bad"});
       fail("Expected CmdLineException for invalid boolean value");
     } catch (CmdLineException e) {
       // expected
       assertTrue(e.getMessage().toLowerCase().contains("illegal boolean value"));
     }
   }
 }
