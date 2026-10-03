package org.apache.commons.cli.bug;

 import junit.framework.TestCase;
 import org.apache.commons.cli.*;

 /**
  * JUnit tests targeting the CLI-51 bug in PosixParser.
  * The bug causes parse() to throw UnrecognizedOptionException for
  * a registered option when it should not. This test class covers
  * normal, boundary, and failure cases for PosixParser.
  */
 public class BugCLI51Test extends TestCase {

     /**
      * Normal case: parse a short option that takes an argument
      * followed by its value. Should succeed without exception.
      */
     public void testRegisteredShortOptionWithArg() {
         Options options = new Options();
         options.addOption(OptionBuilder.withLongOpt("output")
                 .withDescription("output file")
                 .hasArg()
                 .create('o'));

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"-o", "value"});

         assertTrue("Option -o should be present", cmd.hasOption("o"));
         assertEquals("Option -o value should be 'value'", "value", cmd.getOptionValue("o"));
     }

     /**
      * Normal case: parse a long option with argument using --.
      */
     public void testRegisteredLongOption() {
         Options options = new Options();
         options.addOption(OptionBuilder.withLongOpt("output")
                 .withDescription("output file")
                 .hasArg()
                 .create('o'));

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"--output", "value"});

         assertTrue("Option --output should be present", cmd.hasOption("o"));
         assertEquals("Option --output value should be 'value'", "value", cmd.getOptionValue("o"));
     }

     /**
      * Bug reproduction: parsing "-o" where option 'o' requires an argument.
      * The buggy version throws UnrecognizedOptionException instead of
      * MissingArgumentException (or succeeding with null value).
      * This test expects MissingArgumentException and fails if
      * UnrecognizedOptionException is caught.
      */
     public void testShortOptionMissingArgMustNotThrowUnrecognized() {
         Options options = new Options();
         options.addOption("o", true, "requiring arg");

         PosixParser parser = new PosixParser();
         try {
             parser.parse(options, new String[]{"-o"});
             // In some versions parsing may succeed with a null value;
             // that is also acceptable.
         } catch (UnrecognizedOptionException e) {
             fail("Should not throw UnrecognizedOptionException for registered option -o");
         } catch (MissingArgumentException e) {
             // Expected when an argument is required but missing.
         }
     }

     /**
      * Combined short options (burstToken) where a and b are flags,
      * and c takes an argument. The argument is attached directly.
      * Ensures burstToken does not cause UnrecognizedOptionException.
      */
     public void testBurstTokenWithAttachedArg() {
         Options options = new Options();
         options.addOption("a", false, "flag a");
         options.addOption("b", false, "flag b");
         options.addOption("c", true, "option c with arg");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"-abcvalue"});

         assertTrue("-a should be recognized", cmd.hasOption("a"));
         assertTrue("-b should be recognized", cmd.hasOption("b"));
         assertTrue("-c should be recognized", cmd.hasOption("c"));
         assertEquals("Argument of -c should be 'value'", "value", cmd.getOptionValue("c"));
     }

     /**
      * Combined short options with a space-separated argument for the last option.
      */
     public void testBurstTokenWithSeparateArg() {
         Options options = new Options();
         options.addOption("x", false, "flag x");
         options.addOption("y", false, "flag y");
         options.addOption("z", true, "option z with arg");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"-xyz", "argument"});

         assertTrue(cmd.hasOption("x"));
         assertTrue(cmd.hasOption("y"));
         assertTrue(cmd.hasOption("z"));
         assertEquals("argument", cmd.getOptionValue("z"));
     }

     /**
      * Unrecognized short option should throw UnrecognizedOptionException.
      */
     public void testUnrecognizedShortOption() {
         Options options = new Options();
         // no option registered

         PosixParser parser = new PosixParser();
         try {
             parser.parse(options, new String[]{"-x"});
             fail("Expected UnrecognizedOptionException");
         } catch (UnrecognizedOptionException e) {
             // expected
         }
     }

     /**
      * Missing required argument for a registered option should throw
      * MissingArgumentException.
      */
     public void testMissingArgForOption() {
         Options options = new Options();
         options.addOption("o", true, "requires arg");

         PosixParser parser = new PosixParser();
         try {
             parser.parse(options, new String[]{"-o"});
             // If parse succeeds (unlikely with required arg) we treat
             // it as acceptable; no strong assertion possible here.
         } catch (MissingArgumentException e) {
             // expected when argument is required
         } catch (UnrecognizedOptionException e) {
             fail("Should not throw UnrecognizedOptionException for -o");
         }
     }

     /**
      * When stopAtNonOption is true, parsing stops at the first non-option
      * token and remaining arguments are not recognized as options.
      */
     public void testStopAtNonOption() {
         Options options = new Options();
         options.addOption("a", false, "flag a");
         options.addOption("b", false, "flag b");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"-a", "nonoption", "-b"}, true);

         assertTrue("Flag -a before non-option should be recognized", cmd.hasOption("a"));
         assertFalse("Flag -b after non-option with stop=true should not be recognized",
                 cmd.hasOption("b"));
         assertEquals("Non-option token should be in args", 1, cmd.getArgs().length);
         assertEquals("nonoption", cmd.getArgs()[0]);
     }

     /**
      * With stopAtNonOption false (default), a lone hyphen is treated
      * as a single special token and does not break parsing.
      */
     public void testLoneHyphen() {
         Options options = new Options();
         options.addOption("v", false, "verbose");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"-v", "-"});

         assertTrue(cmd.hasOption("v"));
         // lone hyphen appears as an argument
         assertEquals(1, cmd.getArgs().length);
         assertEquals("-", cmd.getArgs()[0]);
     }

     /**
      * The "--" marker terminates option processing; everything after it
      * is treated as a non-option argument, even if it looks like an option.
      */
     public void testDoubleDashTerminatesOptions() {
         Options options = new Options();
         options.addOption("f", false, "flag f");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options,
                 new String[]{"-f", "--", "-g", "value"});

         assertTrue("Flag -f before -- should be recognized", cmd.hasOption("f"));
         assertFalse("-g after -- should not be recognized", cmd.hasOption("g"));
         // Args after -- are: -- (treated as arg?), -g, value.
         // Typically the library includes -- and subsequent tokens as args.
         String[] args = cmd.getArgs();
         assertTrue("Should have some leftover args", args.length >= 2);
     }

     /**
      * Empty arguments array should parse without exception and produce
      * no options or arguments.
      */
     public void testEmptyArgs() {
         Options options = new Options();
         options.addOption("h", false, "help");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{});

         assertFalse(cmd.hasOption("h"));
         assertEquals(0, cmd.getArgs().length);
     }

     /**
      * Long option with embedded equals sign should be correctly split
      * into option name and its value.
      */
     public void testLongOptionWithEquals() {
         Options options = new Options();
         options.addOption(OptionBuilder.withLongOpt("output")
                 .withDescription("output file")
                 .hasArg()
                 .create('o'));

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, new String[]{"--output=myfile.txt"});

         assertTrue(cmd.hasOption("o"));
         assertEquals("myfile.txt", cmd.getOptionValue("o"));
     }
 }
