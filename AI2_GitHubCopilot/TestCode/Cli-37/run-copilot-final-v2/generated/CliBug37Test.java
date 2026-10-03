import static org.junit.Assert.*;

 import org.junit.Test;
 import org.apache.commons.cli.DefaultParser;
 import org.apache.commons.cli.CommandLine;
 import org.apache.commons.cli.Options;
 import org.apache.commons.cli.Option;
 import org.apache.commons.cli.ParseException;
 import org.apache.commons.cli.MissingArgumentException;
 import org.apache.commons.cli.UnrecognizedOptionException;

 /**
  * Tests for DefaultParser short option parsing (CLI-265 bug).
  * Focus: short options without arguments must not consume subsequent tokens as values.
  */
 public class CliBug37Test {

     @Test
     public void testTwoSeparateShortOptions() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "first");
         opts.addOption("b", false, "second");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a", "-b"});
         assertTrue("Option a should be present", cmd.hasOption("a"));
         assertTrue("Option b should be present", cmd.hasOption("b"));
         assertNull("Option a should have no argument value", cmd.getOptionValue("a"));
         assertNull("Option b should have no argument value", cmd.getOptionValue("b"));
     }

     @Test
     public void testConcatenatedShortOptions() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "first");
         opts.addOption("b", false, "second");
         opts.addOption("c", false, "third");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-abc"});
         assertTrue(cmd.hasOption("a"));
         assertTrue(cmd.hasOption("b"));
         assertTrue(cmd.hasOption("c"));
         assertNull(cmd.getOptionValue("a"));
         assertNull(cmd.getOptionValue("b"));
         assertNull(cmd.getOptionValue("c"));
     }

     @Test
     public void testShortOptionFollowedByOptionWithArg() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "flag");
         opts.addOption("b", true, "with-arg");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a", "-b", "value"});
         assertTrue(cmd.hasOption("a"));
         assertNull(cmd.getOptionValue("a"));
         assertTrue(cmd.hasOption("b"));
         assertEquals("value", cmd.getOptionValue("b"));
     }

     @Test
     public void testShortOptionFollowedByDoubleDash() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "flag");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a", "--", "file"});
         assertTrue(cmd.hasOption("a"));
         assertNull(cmd.getOptionValue("a"));
         assertEquals(1, cmd.getArgList().size());
         assertEquals("file", cmd.getArgList().get(0));
     }

     @Test(expected = UnrecognizedOptionException.class)
     public void testUnknownShortOption() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "known");
         DefaultParser parser = new DefaultParser();
         parser.parse(opts, new String[]{"-x"});
     }

     @Test(expected = MissingArgumentException.class)
     public void testMissingRequiredArgument() throws ParseException {
         Options opts = new Options();
         opts.addOption(Option.builder("a").hasArg(true).required(false).build());
         DefaultParser parser = new DefaultParser();
         parser.parse(opts, new String[]{"-a"});
     }

     @Test
     public void testShortOptionWithOptionalArgNotProvided() throws ParseException {
         Options opts = new Options();
         opts.addOption(Option.builder("c").hasOptionalArg().build());
         opts.addOption("d", false, "no-arg");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-c", "-d"});
         assertTrue(cmd.hasOption("c"));
         assertNull("Optional arg not provided should be null", cmd.getOptionValue("c"));
         assertTrue(cmd.hasOption("d"));
         assertNull(cmd.getOptionValue("d"));
     }

     @Test
     public void testSingleShortOption() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "flag");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a"});
         assertTrue(cmd.hasOption("a"));
         assertNull(cmd.getOptionValue("a"));
         assertEquals(0, cmd.getArgList().size());
     }

     @Test
     public void testEmptyArguments() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "flag");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{});
         assertFalse(cmd.hasOption("a"));
         assertEquals(0, cmd.getArgList().size());
     }

     @Test
     public void testShortOptionWithEqualSignValue() throws ParseException {
         Options opts = new Options();
         opts.addOption(Option.builder("a").hasArg(true).build());
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a=val"});
         assertTrue(cmd.hasOption("a"));
         assertEquals("val", cmd.getOptionValue("a"));
     }

     @Test
     public void testShortOptionHonorsStopAtNonOption() throws ParseException {
         Options opts = new Options();
         opts.addOption("a", false, "flag");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-a", "nonoption", "-b"}, true);
         assertTrue(cmd.hasOption("a"));
         assertEquals(2, cmd.getArgList().size());
         assertEquals("nonoption", cmd.getArgList().get(0));
         assertEquals("-b", cmd.getArgList().get(1));
     }

     @Test
     public void testShortOptionContainingDigits() throws ParseException {
         // ensure -last where l has no arg does not consume -ast as value
         Options opts = new Options();
         opts.addOption("l", false, "long format");
         opts.addOption("a", false, "alpha");
         opts.addOption("s", false, "sigma");
         opts.addOption("t", false, "tau");
         DefaultParser parser = new DefaultParser();
         CommandLine cmd = parser.parse(opts, new String[]{"-last"});
         assertTrue(cmd.hasOption("l"));
         assertNull(cmd.getOptionValue("l"));
         // the rest should be treated as separate concatenated options: a, s, t
         assertTrue(cmd.hasOption("a"));
         assertTrue(cmd.hasOption("s"));
         assertTrue(cmd.hasOption("t"));
         assertNull(cmd.getOptionValue("a"));
         assertNull(cmd.getOptionValue("s"));
         assertNull(cmd.getOptionValue("t"));
     }
 }
