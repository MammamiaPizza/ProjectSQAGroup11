package org.apache.commons.cli;

 import junit.framework.TestCase;

 public class ParseRequiredTest extends TestCase {

     private Options options;
     private Parser parser;

     protected void setUp() {
         options = new Options();
         Option requiredOpt = new Option("r", "required", true, "required option");
         requiredOpt.setRequired(true);
         options.addOption(requiredOpt);
         parser = new BasicParser();
     }

     public void testReuseOptionsTwice() throws Exception {
         // First parse: required option provided
         String[] args = {"-r", "value1"};
         parser.parse(options, args);

         // Second parse: reusing same Options, but required option missing
         String[] argsMissing = {};
         try {
             parser.parse(options, argsMissing);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             assertTrue(e.getMessage().indexOf("Missing required option") >= 0);
         }
     }

     public void testParseMissingRequiredFirstCall() throws Exception {
         String[] args = {};
         try {
             parser.parse(options, args);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             assertTrue(e.getMessage().indexOf("Missing required option") >= 0);
         }
     }

     public void testReuseOptionsAllRequiredPresent() throws Exception {
         String[] args1 = {"-r", "a"};
         parser.parse(options, args1);

         String[] args2 = {"-r", "b"};
         CommandLine cmd = parser.parse(options, args2);
         assertEquals("b", cmd.getOptionValue("r"));
     }

     public void testReuseWithMultipleRequired() throws Exception {
         Options localOptions = new Options();
         Option a = new Option("a", true, "");
         a.setRequired(true);
         Option b = new Option("b", true, "");
         b.setRequired(true);
         localOptions.addOption(a);
         localOptions.addOption(b);

         Parser p = new BasicParser();
         // first parse: both present
         p.parse(localOptions, new String[]{"-a", "1", "-b", "2"});

         // second parse: only one present
         try {
             p.parse(localOptions, new String[]{"-a", "3"});
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             assertTrue(e.getMessage().indexOf("Missing required option") >= 0);
         }
     }

     public void testRequiredOptionRemovedFromOptionsAfterFirstParse() throws Exception {
         // After first parse, the Options' internal required list is corrupted (bug)
         parser.parse(options, new String[]{"-r", "x"});

         // The same Options instance should still report required options but won't
         // This test documents the bug: getRequiredOptions() may be empty
         // But we cannot check Options internals; instead, second parse with missing arg should
throw
         try {
             parser.parse(options, new String[]{});
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             // expected
         }
     }

     public void testOptionGroupRequiredReuse() throws Exception {
         Options groupOpts = new Options();
         Option x = new Option("x", false, "");
         Option y = new Option("y", false, "");
         OptionGroup group = new OptionGroup();
         group.addOption(x);
         group.addOption(y);
         group.setRequired(true);
         groupOpts.addOptionGroup(group);

         Parser p = new BasicParser();
         p.parse(groupOpts, new String[]{"-x"});

         try {
             p.parse(groupOpts, new String[]{});
             fail("MissingOptionException expected for group");
         } catch (MissingOptionException e) {
             assertTrue(e.getMessage().indexOf("Missing required option") >= 0);
         }
     }

     public void testStopAtNonOptionWithRequiredMissing() throws Exception {
         String[] args = {"-r", "val", "--nonoption"};
         parser.parse(options, args, true);

         // Second parse with stopAtNonOption but required option missing entirely
         try {
             parser.parse(options, new String[]{"--nonoption"}, true);
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             assertTrue(e.getMessage() != null);
         }
     }

     public void testPropertiesWithRequiredMissing() throws Exception {
         Options propsOpts = new Options();
         Option req = new Option("d", true, "");
         req.setRequired(true);
         propsOpts.addOption(req);

         Parser p = new BasicParser();
         Properties props = new Properties();
         props.setProperty("d", "fromProps");

         // Provide option via properties, no args
         p.parse(propsOpts, new String[]{}, props);
         // required satisfied via properties

         // Reuse: no properties, missing required
         try {
             p.parse(propsOpts, new String[]{});
             fail("MissingOptionException expected");
         } catch (MissingOptionException e) {
             assertTrue(e.getMessage().indexOf("Missing required option") >= 0);
         }
     }
 }
