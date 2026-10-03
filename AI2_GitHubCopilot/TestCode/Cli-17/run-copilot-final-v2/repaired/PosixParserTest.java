package org.apache.commons.cli;

 import junit.framework.TestCase;

 /**
  * Tests for {@link PosixParser} focusing on the stop-at-non-option bursting
  * bug (CLI-163) and other normal/boundary/exception behaviors.
  */
 public class PosixParserTest extends TestCase {

     // ----------------------------------------------------------------
     // 1. Normal bursting without stopAtNonOption
     // ----------------------------------------------------------------
     public void testBurstSimple() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");
         opts.addOption("v", false, "verbose");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-av"}, false);

         assertTrue("Option a must be present", cmd.hasOption("a"));
         assertTrue("Option v must be present", cmd.hasOption("v"));
         assertEquals("No extra arguments expected", 0, cmd.getArgList().size());
     }

     // ----------------------------------------------------------------
     // 2. Bursting with an argument attached to an option
     // ----------------------------------------------------------------
     public void testBurstWithArgument() throws Exception {
         Options opts = new Options();
         opts.addOption("a", true, "alpha");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-afile"}, false);

         assertTrue(cmd.hasOption("a"));
         assertEquals("file", cmd.getOptionValue("a"));
         assertEquals(0, cmd.getArgList().size());
     }

     // ----------------------------------------------------------------
     // 3. stopAtNonOption=true, non-option at the end of a burst token
     // ----------------------------------------------------------------
     public void testStopBurstingNonOptionEnd() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");

         PosixParser parser = new PosixParser();
         // "-ac" : 'a' valid, 'c' not an option; stopAtNonOption=true
         CommandLine cmd = parser.parse(opts, new String[]{"-ac", "file"}, true);

         assertTrue(cmd.hasOption("a"));
         // The rest of the burst token ("c") and the following token ("file")
         // should both be added as arguments.
         assertEquals(2, cmd.getArgList().size());
         assertEquals("c", cmd.getArgList().get(0));
         assertEquals("file", cmd.getArgList().get(1));
     }

     // ----------------------------------------------------------------
     // 4. The CLI-163 duplicate-token bug: non-option char followed by
     //    a valid option char inside a burst token with stopAtNonOption
     // ----------------------------------------------------------------
     public void testStopBurstingDuplicateBug() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");
         opts.addOption("v", false, "verbose");

         PosixParser parser = new PosixParser();
         // "-acv" : 'a' valid, 'c' unknown, 'v' valid (but must not be recognised)
         CommandLine cmd = parser.parse(opts, new String[]{"-acv"}, true);

         // The non-option 'c' should stop bursting; the whole rest "cv" must
         // be treated as a single argument.  The buggy implementation
         // incorrectly continues and adds "-v" as well, producing 2 args.
         assertTrue(cmd.hasOption("a"));
         assertFalse("v must not become an option", cmd.hasOption("v"));
         assertEquals("Expected exactly 1 argument (cv)", 1, cmd.getArgList().size());
         assertEquals("cv", cmd.getArgList().get(0));
     }

     // ----------------------------------------------------------------
     // 5. Non-option as first char in a burst token; no later char may
     //    become an option.
     // ----------------------------------------------------------------
     public void testStopBurstingFirstCharNonOption() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");
         opts.addOption("b", false, "beta");

         PosixParser parser = new PosixParser();
         // "-cab" : 'c' unknown, 'a' and 'b' valid
         CommandLine cmd = parser.parse(opts, new String[]{"-cab"}, true);

         // Everything after '-' must be treated as a single argument "cab".
         assertFalse("a must not become an option", cmd.hasOption("a"));
         assertFalse("b must not become an option", cmd.hasOption("b"));
         assertEquals(1, cmd.getArgList().size());
         assertEquals("cab", cmd.getArgList().get(0));
     }

     // ----------------------------------------------------------------
     // 6. Long option (--opt)
     // ----------------------------------------------------------------
     public void testLongOption() throws Exception {
         Options opts = new Options();
         opts.addOption("a", "alpha", false, "alpha option");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"--alpha"}, false);

         assertTrue(cmd.hasOption("alpha"));
         assertEquals(0, cmd.getArgList().size());
     }

     // ----------------------------------------------------------------
     // 7. Long option with equals sign (--opt=value)
     // ----------------------------------------------------------------
     public void testLongOptionWithEquals() throws Exception {
         Options opts = new Options();
         opts.addOption("a", "alpha", true, "alpha option");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"--alpha=value"}, false);

         assertTrue(cmd.hasOption("alpha"));
         assertEquals("value", cmd.getOptionValue("alpha"));
         assertEquals(0, cmd.getArgList().size());
     }

     // ----------------------------------------------------------------
     // 8. "--" stops option processing unconditionally
     // ----------------------------------------------------------------
     public void testDoubleHyphenStop() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a", "--", "-b", "arg"}, false);

         assertTrue(cmd.hasOption("a"));
         assertFalse("Options after -- must be arguments", cmd.hasOption("b"));
         assertEquals(2, cmd.getArgList().size());
         assertEquals("-b", cmd.getArgList().get(0));
         assertEquals("arg", cmd.getArgList().get(1));
     }

     // ----------------------------------------------------------------
     // 9. Lone hyphen ("-") is treated as a literal argument
     // ----------------------------------------------------------------
     public void testSingleHyphen() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a", "-"}, false);

         assertTrue(cmd.hasOption("a"));
         assertEquals(1, cmd.getArgList().size());
         assertEquals("-", cmd.getArgList().get(0));
     }

     // ----------------------------------------------------------------
     // 10. stopAtNonOption without bursting: first token is non-option
     // ----------------------------------------------------------------
     public void testStopAtNonOptionNoBurst() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");

         PosixParser parser = new PosixParser();
         CommandLine cmd = parser.parse(opts, new String[]{"foo", "-a", "bar"}, true);

         assertFalse(cmd.hasOption("a"));
         assertEquals(3, cmd.getArgList().size());
     }

     // ----------------------------------------------------------------
     // 11. Unrecognized option (stopAtNonOption=false) → ParseException
     // ----------------------------------------------------------------
     public void testUnrecognizedOptionThrows() throws Exception {
         Options opts = new Options();
         opts.addOption("a", false, "alpha");

         PosixParser parser = new PosixParser();
         try {
             parser.parse(opts, new String[]{"-x"}, false);
             fail("Expected ParseException for unrecognized option -x");
         } catch (ParseException e) {
             // expected
         }
     }

     // ----------------------------------------------------------------
     // 12. Missing required option → ParseException
     // ----------------------------------------------------------------
     public void testMissingRequiredOptionThrows() throws Exception {
         Options opts = new Options();
         Option req = new Option("a", "alpha", true, "required alpha");
         req.setRequired(true);
         opts.addOption(req);

         PosixParser parser = new PosixParser();
         try {
             parser.parse(opts, new String[]{}, false);
             fail("Expected ParseException for missing required option");
         } catch (ParseException e) {
             // expected
         }
     }
 }
