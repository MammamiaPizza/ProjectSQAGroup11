package org.apache.commons.cli;

 import junit.framework.TestCase;

 /**
  * Tests the MissingOptionException message produced by Parser.checkRequiredOptions()
  * to ensure that multiple missing required options are separated by ", ".
  * Regression test for CLI-149.
  */
 public class CheckRequiredOptionsMessageTest extends TestCase {

     private Options options;
     private Parser parser;

     protected void setUp() {
         options = new Options();
         parser = new BasicParser();
     }

     /** Single missing required option produces message without "s" and without separator. */
     public void testSingleMissingRequiredOptionMessage() {
         options.addOption(OptionBuilder.isRequired().create('f'));
         try {
             parser.parse(options, new String[0]);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             assertEquals("Missing required option: f", e.getMessage());
         }
     }

     /** Two missing required options must have ", " between them. */
     public void testTwoMissingRequiredOptionsMessageHasSeparator() {
         options.addOption(OptionBuilder.isRequired().create('a'));
         options.addOption(OptionBuilder.isRequired().create('b'));
         try {
             parser.parse(options, new String[0]);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             String msg = e.getMessage();
             assertTrue("Message must contain comma-space separator",
                     msg.startsWith("Missing required options: ") && msg.contains(", "));
         }
     }

     /** Three missing required options also use ", " between each. */
     public void testThreeMissingRequiredOptionsMessageHasSeparators() {
         options.addOption(OptionBuilder.isRequired().create('x'));
         options.addOption(OptionBuilder.isRequired().create('y'));
         options.addOption(OptionBuilder.isRequired().create('z'));
         try {
             parser.parse(options, new String[0]);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             String msg = e.getMessage();
             assertTrue("Message must contain two comma-space separators",
                     msg.indexOf(", ") != msg.lastIndexOf(", "));
         }
     }

     /** No exception when all required options are supplied. */
     public void testNoMissingRequiredOptionsNoException() {
         options.addOption(OptionBuilder.isRequired().create('p'));
         try {
             CommandLine cl = parser.parse(options, new String[] { "-p" });
             assertNotNull(cl);
             assertTrue(cl.hasOption("p"));
         } catch (ParseException e) {
             fail("No exception expected when required option is provided");
         }
     }

     /** Message for a single required option group? (verifies no regression). */
     public void testOptionGroupRequiredMessageFormat() {
         OptionGroup group = new OptionGroup();
         group.addOption(OptionBuilder.create('a'));
         group.addOption(OptionBuilder.create('b'));
         group.setRequired(true);
         options.addOptionGroup(group);
         try {
             parser.parse(options, new String[0]);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             // The group itself becomes a required item; separator logic still applies
             // This test just ensures the code path does not break
             assertNotNull(e.getMessage());
         }
     }
 }