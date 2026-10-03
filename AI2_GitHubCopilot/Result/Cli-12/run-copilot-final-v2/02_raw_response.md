package org.apache.commons.cli;

import junit.framework.TestCase;

public class GnuParserTest extends TestCase {

 private Options createOptionsWithD() {
     Options options = new Options();
     options.addOption("D", true, "define property");
     return options;
 }

 private Options createOptionsWithFoo() {
     Options options = new Options();
     options.addOption("f", "foo", true, "foo option");
     return options;
 }

 public void testShortWithEqual() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-D=bar"});
     assertEquals("bar", cmd.getOptionValue("D"));
 }

 public void testLongWithEqualSingleDash() throws Exception {
     Options options = createOptionsWithFoo();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-foo=bar"});
     assertEquals("bar", cmd.getOptionValue("foo"));
 }

 public void testLongWithEqualDoubleDash() throws Exception {
     Options options = createOptionsWithFoo();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"--foo=bar"});
     assertEquals("bar", cmd.getOptionValue("foo"));
 }

 public void testShortWithEqualEmptyValue() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-D="});
     assertEquals("", cmd.getOptionValue("D"));
 }

 public void testLongWithEqualEmptyValue() throws Exception {
     Options options = createOptionsWithFoo();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"--foo="});
     assertEquals("", cmd.getOptionValue("foo"));
 }

 public void testShortWithEqualMultipleEquals() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-D=foo=bar"});
     assertEquals("foo=bar", cmd.getOptionValue("D"));
 }

 public void testLongWithEqualMultipleEquals() throws Exception {
     Options options = createOptionsWithFoo();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"--foo=bar=baz"});
     assertEquals("bar=baz", cmd.getOptionValue("foo"));
 }

 public void testShortOptionWithoutEquals() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-D", "bar"});
     assertEquals("bar", cmd.getOptionValue("D"));
 }

 public void testUnrecognizedOptionWithEqual() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     try {
         parser.parse(options, new String[]{"--unknown=bar"});
         fail("Expected UnrecognizedOptionException");
     } catch (UnrecognizedOptionException expected) {
         // expected
     }
 }

 public void testStopAtNonOptionWithEqual() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-D=bar", "nonopt"}, true);
     assertEquals("bar", cmd.getOptionValue("D"));
     assertEquals(1, cmd.getArgs().length);
     assertEquals("nonopt", cmd.getArgs()[0]);
 }

 public void testShortOptionConcatenatedValue() throws Exception {
     Options options = createOptionsWithD();
     GnuParser parser = new GnuParser();
     CommandLine cmd = parser.parse(options, new String[]{"-Dbar"});
     assertEquals("bar", cmd.getOptionValue("D"));
 }

 public void testMissingRequiredValueForOption() throws Exception {
     Options options = new Options();
     Option opt = new Option("D", true, "define property");
     opt.setOptionalArg(false);
     options.addOption(opt);
     GnuParser parser = new GnuParser();
     try {
         parser.parse(options, new String[]{"-D"});
         fail("Expected MissingArgumentException");
     } catch (MissingArgumentException expected) {
         // expected
     }
 }

}