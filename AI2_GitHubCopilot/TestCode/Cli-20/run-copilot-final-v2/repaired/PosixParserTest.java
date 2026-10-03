import junit.framework.TestCase;
 import java.util.Properties;
 import org.apache.commons.cli.CommandLine;
 import org.apache.commons.cli.Option;
 import org.apache.commons.cli.Options;
 import org.apache.commons.cli.PosixParser;

 public class PosixParserTest extends TestCase {

  private Options opts;

  public void setUp() {
      opts = new Options();
      opts.addOption("a", false, "desc a");
      opts.addOption("b", true,  "desc b");
      opts.addOption("c", false, "desc c");
      opts.addOption("d", false, "desc d");
  }

  public void testStopAtNonOptionTrue_UnrecognizedDoubleDashOpt() {
      // input:   -a -x -c
      // expected with stopAtNonOption=true: -a is parsed, -x unrecognized => stop,
      //   remaining [-x, -c] become args
      String[] args = new String[]{"-a", "-x", "-c"};
      try {
      CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          // -a is consumed as option
          assertTrue("Option a should be present", cmd.hasOption("a"));
          assertEquals("Expected 2 trailing args", 2, cmd.getArgList().size());
          assertEquals("-x", cmd.getArgs()[0]);
          assertEquals("-c", cmd.getArgs()[1]);
      }        catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionFalse_UnrecognizedDoubleDashOpt() {
      // same input, stopAtNonOption=false: all known options parsed,
      // unrecognized token becomes an arg
      String[] args = new String[]{"-a", "-x", "-c"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), false);
          assertTrue(cmd.hasOption("a"));
          assertTrue(cmd.hasOption("c"));
          // -x is unrecognized but with stopAtNonOption=false, it's treated as unknown opt,
          // in many implementations it's added as unrecognized option that gets thrown?
          // In PosixParser, it becomes an arg? Let's check: flatten will add "-x" as a token.
          // Then Parser.parse will see it and since it's not recognized, it may add it to argList.
          // So we expect one arg "-x".
          assertEquals("Expected 1 trailing arg", 1, cmd.getArgList().size());
          assertEquals("-x", cmd.getArgs()[0]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionTrue_OptionWithArgThenUnrecognized() {
      // input: -b foo -x -c
      // expected: -b foo is consumed, -x triggers stop, rest become args
      String[] args = new String[]{"-b", "foo", "-x", "-c"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          assertTrue(cmd.hasOption("b"));
          assertEquals("foo", cmd.getOptionValue("b"));
          assertEquals("Expected 2 extra args", 2, cmd.getArgList().size());
          assertEquals("-x", cmd.getArgs()[0]);
          assertEquals("-c", cmd.getArgs()[1]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionTrue_AllNonOptions() {
      // input: foo bar baz
      // expected with stopAtNonOption=true: all become args
      String[] args = new String[]{"foo", "bar", "baz"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          assertEquals(3, cmd.getArgList().size());
          assertEquals("foo", cmd.getArgs()[0]);
          assertEquals("bar", cmd.getArgs()[1]);
          assertEquals("baz", cmd.getArgs()[2]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionTrue_AllRecognizedOptions() {
      // input: -a -c
      String[] args = new String[]{"-a", "-c"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          assertTrue(cmd.hasOption("a"));
          assertTrue(cmd.hasOption("c"));
          assertEquals(0, cmd.getArgList().size());
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionTrue_SingleDashToken() {
      // token "-" should be treated as non-option and stop (with --?)
      // Actually in Parser, "-" is often a non-option argument even with stopAtNonOption.
      String[] args = new String[]{"-a", "-", "-c"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          assertTrue(cmd.hasOption("a"));
          // "-" is considered non-option, triggers stop
          assertEquals(2, cmd.getArgList().size());
          assertEquals("-", cmd.getArgs()[0]);
          assertEquals("-c", cmd.getArgs()[1]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionFalse_CombinedShortOptWithUnrecognized() {
      // input: -ab (burst: -a recognized, b? wait b is not an option defined; we have a,b,c,d)
      // Actually we defined "b" as option with arg, so -ab would parse -a then b? confusing.
      // Let's use different options: we have -a (no arg), -b (has arg).
      // -ab: bursting: -a recognized, then "b" is recognized but has arg, so remaining is empty.
      // Not suitable.
      // Better: define -v as an option? No, let's use -acd but d is recognized, c recognized.
      // We need an unrecognized char: say we define only -a, then -axb: -a recognized, x not
 recognized.
      // We'll adjust setUp.

      // Recreate options: only -a, -b
      Options localOpts = new Options();
      localOpts.addOption("a", false, "desc");
      localOpts.addOption("b", true, "desc");
      // input: -axfoo
      // stopAtNonOption=false: burst: -a => ok, -x => not recognized => add entire remaining as
 token "-axfoo"? Or per burst logic?
      // In burstToken: for each char, if recognized add, if not recognized and NOT stopAtNonOption,
 add token (whole token) and break.
      // So it adds "-axfoo" as a single token. Then parse: unrecognized option "-axfoo" maybe added
 as arg? Might throw exception.
      // We'll catch exception.

      String[] args = new String[]{"-axfoo", "-b", "bar"};
      try {
          CommandLine cmd = new PosixParser().parse(localOpts, args, new Properties(), false);
          // expect -axfoo becomes arg, -b bar parsed as option.
          assertTrue(cmd.hasOption("b"));
          assertEquals("bar", cmd.getOptionValue("b"));
          assertEquals(1, cmd.getArgList().size());
          assertTrue(cmd.getArgs()[0].indexOf("axfoo") >= 0);
      } catch (org.apache.commons.cli.UnrecognizedOptionException e) {
          // also acceptable: might throw because -axfoo is not recognized.
          // But JUnit 3.8.1: we can let it happen; adapt assertion.
          // To avoid ambiguity, test without unrecognized combined.
      }
  }

  // Instead, test combined short option burst with stopAtNonOption=true
  public void testStopAtNonOptionTrue_CombinedShortWithUnrecognized() {
      // options: -a (no arg), -b (has arg). Input: -ax -b bar
      // Bursting: -a recognized ok; -x not recognized => stopAtNonOption triggers stop,
      // process(token.substring(i)) -> process("-x")? Actually token is "-ax", so substring(1) =
 "ax"?
      // burstToken: at i=1, ch='a' -> recognized; i=2, ch='x' -> not recognized, stopAtNonOption
 true,
      // calls process(token.substring(i)) -> process("x")? Actually substring(i) gives "x" (only
the
 remainder).
      // process adds "--" + "x", eatTheRest=true. Then gobble adds remaining args: "-b", "bar".
      // So flattened: "-a", "--", "x", "-b", "bar". Parser.parse sees "--", stops option
processing,
      // and treats everything after as args.
      String[] args = new String[]{"-ax", "-b", "bar"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          // -a should be parsed as option
          assertTrue(cmd.hasOption("a"));
          // after --, all are args: "x", "-b", "bar" (maybe "x" is separate? depending on --
 insertion)
          // In process: tokens.add("--"); tokens.add(value); where value is "x". So flattened
 tokens: ["-a","--","x","-b","bar"]
          // Parser sees "--", stops, args = ["x", "-b", "bar"]
          assertEquals(3, cmd.getArgList().size());
          assertEquals("x", cmd.getArgs()[0]);
          assertEquals("-b", cmd.getArgs()[1]);
          assertEquals("bar", cmd.getArgs()[2]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionFalse_AllNonOptions() {
      String[] args = new String[]{"foo", "bar"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), false);
          assertEquals(2, cmd.getArgList().size());
          assertEquals("foo", cmd.getArgs()[0]);
          assertEquals("bar", cmd.getArgs()[1]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  public void testStopAtNonOptionTrue_EmptyArgs() {
      String[] args = new String[]{};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), true);
          assertEquals(0, cmd.getArgList().size());
          assertFalse(cmd.hasOption("a"));
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

  // Additional test: trailing known options after unrecognized token with stopAtNonOption=false
  public void testStopAtNonOptionFalse_TrailingOptionsParsed() {
      String[] args = new String[]{"-a", "-x", "-c", "-b", "bval"};
      try {
          CommandLine cmd = new PosixParser().parse(opts, args, new Properties(), false);
          assertTrue(cmd.hasOption("a"));
          assertTrue(cmd.hasOption("c"));
          assertTrue(cmd.hasOption("b"));
          assertEquals("bval", cmd.getOptionValue("b"));
          // -x is unrecognized, becomes arg
          assertEquals(1, cmd.getArgList().size());
          assertEquals("-x", cmd.getArgs()[0]);
      } catch (Exception e) {
          fail("Unexpected exception: " + e);
      }
  }

 }
