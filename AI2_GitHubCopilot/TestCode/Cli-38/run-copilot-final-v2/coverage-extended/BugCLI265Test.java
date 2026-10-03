package org.apache.commons.cli.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

public class BugCLI265Test {

 @Test
 public void shouldParseConcatenatedShortOptions() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", false, "option a"));
     options.addOption(new Option("b", false, "option b"));

     CommandLine cmd = new DefaultParser().parse(options, new String[]{"-ab"});

     assertTrue(cmd.hasOption("a"));
     assertTrue(cmd.hasOption("b"));
     assertTrue(cmd.getArgList().isEmpty());
     assertNull(cmd.getOptionValue("a"));
     assertNull(cmd.getOptionValue("b"));
 }

 @Test
 public void shouldConsumeTrailingClusterAsRequiredArgument() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", true, "option a"));
     options.addOption(new Option("b", false, "option b"));

     CommandLine cmd = new DefaultParser().parse(options, new String[]{"-ab"});

     assertTrue(cmd.hasOption("a"));
     assertFalse(cmd.hasOption("b"));
     assertEquals("b", cmd.getOptionValue("a"));
     assertTrue(cmd.getArgList().isEmpty());
 }

 @Test(expected = UnrecognizedOptionException.class)
 public void shouldRejectUnknownShortOptionInCluster() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", false, "option a"));

     new DefaultParser().parse(options, new String[]{"-ab"});
 }

 @Test
 public void shouldParseClusterFollowedByLongOption() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", false, "option a"));
     options.addOption(new Option("b", false, "option b"));
     options.addOption(new Option("l", "long", false, "option long"));

     CommandLine cmd = new DefaultParser().parse(options, new String[]{"-ab", "--long"});

     assertTrue(cmd.hasOption("a"));
     assertTrue(cmd.hasOption("b"));
     assertTrue(cmd.hasOption("l"));
     assertTrue(cmd.getArgList().isEmpty());
 }

 @Test
 public void shouldKeepEmptyTokenAsArgument() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", false, "option a"));

     CommandLine cmd = new DefaultParser().parse(options, new String[]{""});

     assertEquals(Arrays.asList(""), cmd.getArgList());
 }

 @Test
 public void shouldStopParsingOptionsAfterNonOptionFollowingCluster() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", false, "option a"));
     options.addOption(new Option("b", false, "option b"));

     CommandLine cmd = new DefaultParser().parse(options,
             new String[]{"-ab", "foo", "-b"}, true);

     assertTrue(cmd.hasOption("a"));
     assertTrue(cmd.hasOption("b"));
     assertEquals(Arrays.asList("foo", "-b"), cmd.getArgList());
 }

 @Test
 public void shouldParseSpaceSeparatedOptionValue() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", true, "option a"));

     CommandLine cmd = new DefaultParser().parse(options, new String[]{"-a", "b"});

     assertTrue(cmd.hasOption("a"));
     assertEquals("b", cmd.getOptionValue("a"));
     assertTrue(cmd.getArgList().isEmpty());
 }

 @Test(expected = MissingArgumentException.class)
 public void shouldThrowWhenClusterEndsWithOptionRequiringArgument() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", false, "option a"));
     options.addOption(new Option("b", true, "option b"));

     new DefaultParser().parse(options, new String[]{"-ab"});
 }

@Test(expected = org.apache.commons.cli.MissingOptionException.class)
 public void testMissingRequiredOption() throws Exception {
     Options options = new Options();
     Option requiredOpt = new Option("r", "required", false, "required option");
     requiredOpt.setRequired(true);
     options.addOption(requiredOpt);
     new DefaultParser().parse(options, new String[]{});
 }

 @Test(expected = org.apache.commons.cli.AmbiguousOptionException.class)
 public void testAmbiguousLongOption() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", "alpha", false, "alpha desc"));
     options.addOption(new Option("b", "alphabet", false, "alphabet desc"));
     new DefaultParser().parse(options, new String[]{"--alph"});
 }

 @Test(expected = org.apache.commons.cli.AmbiguousOptionException.class)
 public void testAmbiguousLongOptionWithEqual() throws Exception {
     Options options = new Options();
     options.addOption(new Option("a", "alpha", false, "alpha desc"));
     options.addOption(new Option("b", "alphabet", false, "alphabet desc"));
     new DefaultParser().parse(options, new String[]{"--alph=val"});
 }

 @Test(expected = org.apache.commons.cli.UnrecognizedOptionException.class)
 public void testLongOptionWithEqualNoArg() throws Exception {
     Options options = new Options();
     options.addOption(new Option("v", "verbose", false, "verbose mode"));
     new DefaultParser().parse(options, new String[]{"--verbose=true"});
 }
}
