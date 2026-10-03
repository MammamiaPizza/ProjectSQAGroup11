package org.apache.commons.cli;

 import junit.framework.TestCase;

 public class ParserMissingOptionTest extends TestCase {

     public void testMissingOptionException() {
         Options options = new Options();
         Option opt = new Option("f", "force");
         opt.setRequired(true);
         options.addOption(opt);

         Parser parser = new PosixParser();

         try {
             parser.parse(options, new String[0]);
             fail("MissingOptionException not thrown");
         } catch (MissingOptionException e) {
             assertEquals("Missing required option: f", e.getMessage());
         } catch (ParseException e) {
             fail("Unexpected ParseException: " + e.getMessage());
         }
     }

     public void testMissingOptionsException() {
         Options options = new Options();
         Option optF = new Option("f", "force");
         optF.setRequired(true);
         options.addOption(optF);

         Option optX = new Option("x", "extra");
         optX.setRequired(true);
         options.addOption(optX);

         Parser parser = new PosixParser();

         try {
             parser.parse(options, new String[0]);
             fail("MissingOptionException not thrown");
         } catch (MissingOptionException e) {
             assertEquals("Missing required options: fx", e.getMessage());
         } catch (ParseException e) {
             fail("Unexpected ParseException: " + e.getMessage());
         }
     }

     public void testNoMissingOptionExceptionWhenRequiredProvided() {
         Options options = new Options();
         Option opt = new Option("f", "force");
         opt.setRequired(true);
         options.addOption(opt);

         Parser parser = new PosixParser();

         try {
             CommandLine cmd = parser.parse(options, new String[]{"-f"});
             assertTrue(cmd.hasOption("f"));
         } catch (ParseException e) {
             fail("ParseException should not be thrown: " + e.getMessage());
         }
     }

     public void testMissingOptionExceptionMessageForSingle() {
         Options options = new Options();
         Option opt = new Option("a", "alpha");
         opt.setRequired(true);
         options.addOption(opt);

         Parser parser = new PosixParser();

         try {
             parser.parse(options, new String[]{});
             fail("MissingOptionException not thrown");
         } catch (MissingOptionException e) {
             assertEquals("Missing required option: a", e.getMessage());
         } catch (ParseException e) {
             fail("Unexpected ParseException: " + e.getMessage());
         }
     }
 }