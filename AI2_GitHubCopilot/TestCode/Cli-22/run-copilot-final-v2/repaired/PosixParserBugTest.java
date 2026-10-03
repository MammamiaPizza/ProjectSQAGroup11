package org.apache.commons.cli;

 import junit.framework.TestCase;

 /**
  * JUnit 3.8.1 tests for the PosixParser "--" delimiter bug (Defects4J Cli-22b).
  * The bug causes the flatten method to insert a spurious extra "--" token
  * when encountering the POSIX end-of-options delimiter "--", which corrupts
  * option values and argument lists.
  */
 public class PosixParserBugTest extends TestCase {

     private PosixParser parser;
     private Options options;

     protected void setUp() {
         parser = new PosixParser();
         options = new Options();
         options.addOption("b", true, "an option that takes an argument");
         options.addOption("x", false, "a boolean option");
         options.addOption("v", false, "verbose flag");
         options.addOption("D", true, "define property");
     }

     /**
      * Reproduces testStopAtExpectedArg: option -b before "--", value after.
      * Expected: "-b" receives value "foo", not the spurious "--".
      */
     public void testDoubleDashMidArgsOptionValue() throws ParseException {
         String[] args = { "-b", "--", "foo" };
         CommandLine cmd = parser.parse(options, args, true);
         assertEquals("Option -b should get value 'foo', not '--'",
                 "foo", cmd.getOptionValue("b"));
     }

     /**
      * Reproduces testGroovy: "--" followed by a single non-option argument.
      * Expected: the non-option "println 'hello'" is preserved, not replaced by "--".
      */
     public void testDoubleDashFollowedByNonOption() throws ParseException {
         String[] args = { "--", "println 'hello'" };
         CommandLine cmd = parser.parse(options, args, true);
         assertEquals("Should have exactly one remaining arg", 1, cmd.getArgs().length);
         assertEquals("Remaining arg should be 'println 'hello'', not '--'",
                 "println 'hello'", cmd.getArgs()[0]);
     }

     /**
      * "--" at the beginning of the argument list with multiple non-options.
      * All tokens after "--" must be treated as non-option arguments.
      */
     public void testDoubleDashAtStartWithMultipleArgs() throws ParseException {
         String[] args = { "--", "foo", "bar", "baz" };
         CommandLine cmd = parser.parse(options, args, true);
         assertEquals("Should have three remaining args", 3, cmd.getArgs().length);
         assertEquals("foo", cmd.getArgs()[0]);
         assertEquals("bar", cmd.getArgs()[1]);
         assertEquals("baz", cmd.getArgs()[2]);
     }

     /**
      * Normal parsing without any "--" delimiter (sanity check).
      * Options and their values should be parsed correctly.
      */
     public void testNoDashDashNormal() throws ParseException {
         String[] args = { "-b", "val1", "-x", "-v" };
         CommandLine cmd = parser.parse(options, args, true);
         assertEquals("val1", cmd.getOptionValue("b"));
         assertTrue("Option -x should be set", cmd.hasOption("x"));
         assertTrue("Option -v should be set", cmd.hasOption("v"));
         assertEquals("No remaining args expected", 0, cmd.getArgs().length);
     }

     /**
      * Options parsed before "--", then a single non-option after.
      * Options preceding "--" must be recognized; token after is a non-option.
      */
     public void testOptionsBeforeDoubleDash() throws ParseException {
         String[] args = { "-x", "-v", "--", "file.txt" };
         CommandLine cmd = parser.parse(options, args, true);
         assertTrue("Option -x should be set", cmd.hasOption("x"));
         assertTrue("Option -v should be set", cmd.hasOption("v"));
         assertEquals("One remaining arg expected", 1, cmd.getArgs().length);
         assertEquals("file.txt", cmd.getArgs()[0]);
     }

     /**
      * Multiple non-option arguments after "--".
      * All tokens following "--" must appear as remaining args.
      */
     public void testMultipleArgsAfterDoubleDash() throws ParseException {
         String[] args = { "-x", "--", "a", "b", "c" };
         CommandLine cmd = parser.parse(options, args, true);
         assertTrue("Option -x should be set", cmd.hasOption("x"));
         assertEquals("Three remaining args expected", 3, cmd.getArgs().length);
         assertEquals("a", cmd.getArgs()[0]);
         assertEquals("b", cmd.getArgs()[1]);
         assertEquals("c", cmd.getArgs()[2]);
     }

     /**
      * "--" is the very last token; nothing follows it.
      * Options before "--" should be parsed; no remaining args.
      */
     public void testDoubleDashAsLastToken() throws ParseException {
         String[] args = { "-x", "-v", "--" };
         CommandLine cmd = parser.parse(options, args, true);
         assertTrue("Option -x should be set", cmd.hasOption("x"));
         assertTrue("Option -v should be set", cmd.hasOption("v"));
         assertEquals("No remaining args expected", 0, cmd.getArgs().length);
     }

     /**
      * "--" is the only token in the argument list.
      * No options and no remaining args.
      */
     public void testDoubleDashOnly() throws ParseException {
         String[] args = { "--" };
         CommandLine cmd = parser.parse(options, args, true);
         assertEquals("No remaining args", 0, cmd.getArgs().length);
         assertFalse("No options set", cmd.hasOption("b") || cmd.hasOption("x"));
     }

     /**
      * stopAtNonOption=false must still handle "--" correctly.
      * The value after "--" should be assigned to the preceding option.
      */
     public void testStopAtNonOptionFalseWithDoubleDash() throws ParseException {
         String[] args = { "-b", "--", "foo" };
         CommandLine cmd = parser.parse(options, args, false);
         assertEquals("Option -b value should be 'foo' even with stopAtNonOption=false",
                 "foo", cmd.getOptionValue("b"));
     }

     /**
      * Long option syntax (--foo=bar) must remain unaffected by the "--" fix.
      * Regression test to ensure legitimate long options still work.
      */
     public void testLongOptionWithEqualsUnaffected() throws ParseException {
         Options opts = new Options();
         opts.addOption("f", "file", true, "filename");
         String[] args = { "--file=test.txt", "-x" };
         CommandLine cmd = parser.parse(opts, args, false);
         assertEquals("test.txt", cmd.getOptionValue("file"));
         assertTrue(cmd.hasOption("x"));
     }

     /**
      * Edge case: "--" between two option-value pairs (no option after "--").
      * The option after "--" should be treated as a non-option argument.
      */
     public void testDoubleDashBetweenOptionAndNonOption() throws ParseException {
         String[] args = { "-D", "key=val", "--", "extra" };
         CommandLine cmd = parser.parse(options, args, false);
         assertEquals("Option -D value", "key=val", cmd.getOptionValue("D"));
         assertEquals("One remaining arg", 1, cmd.getArgs().length);
         assertEquals("extra", cmd.getArgs()[0]);
     }

     /**
      * Stress: many options then "--" then many non-options.
      */
     public void testManyOptionsThenManyArgs() throws ParseException {
         String[] args = { "-x", "-v", "-b", "beta", "--",
                 "file1", "file2", "file3", "file4", "file5" };
         CommandLine cmd = parser.parse(options, args, true);
         assertTrue(cmd.hasOption("x"));
         assertTrue(cmd.hasOption("v"));
         assertEquals("beta", cmd.getOptionValue("b"));
         assertEquals("Five remaining args", 5, cmd.getArgs().length);
         assertEquals("file1", cmd.getArgs()[0]);
         assertEquals("file5", cmd.getArgs()[4]);
     }
 }
