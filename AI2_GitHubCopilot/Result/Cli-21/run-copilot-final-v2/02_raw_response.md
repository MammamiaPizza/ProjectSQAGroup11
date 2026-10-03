package org.apache.commons.cli2.bug;

 import junit.framework.TestCase;
 import org.apache.commons.cli2.*;
 import org.apache.commons.cli2.builder.*;
 import java.util.List;

 /**
  * Tests for negative-number handling (CLI-150).
  * Verifies that numeric arguments like "-42" are treated as option values,
  * not as option triggers.
  */
 public class BugCLI150Test extends TestCase {

     private DefaultOptionBuilder ob;
     private ArgumentBuilder ab;
     private Parser parser;

     private Option numOption;
     private Option multiNumOption;
     private Option anonArg;
     private Option flagOption;

     private Group numGroup;
     private Group numAnonGroup;
     private Group numFlagGroup;
     private Group multinumGroup;

     protected void setUp() {
         ob = new DefaultOptionBuilder();
         ab = new ArgumentBuilder();
         parser = new Parser();

         // --num with a single value
         numOption = ob.withLongName("num")
                       .withArgument(ab.withName("number")
                                       .withMinimum(1)
                                       .withMaximum(1)
                                       .create())
                       .create();

         // --num allowed to take multiple values
         multiNumOption = ob.withLongName("num")
                            .withArgument(ab.withName("number")
                                            .withMinimum(1)
                                            .withMaximum(Integer.MAX_VALUE)
                                            .create())
                            .create();

         // anonymous trailing argument
         anonArg = ab.withName("anon")
                     .withMinimum(0)
                     .withMaximum(1)
                     .create();

         // boolean flag
         flagOption = ob.withLongName("flag").create();

         // various groups
         numGroup = new GroupBuilder()
                     .withName("num")
                     .withOption(numOption)
                     .create();

         numAnonGroup = new GroupBuilder()
                         .withName("numAnon")
                         .withOption(numOption)
                         .withOption(anonArg)   // becomes anonymous
                         .create();

         numFlagGroup = new GroupBuilder()
                         .withName("numFlag")
                         .withOption(numOption)
                         .withOption(flagOption)
                         .create();

         multinumGroup = new GroupBuilder()
                         .withName("multi")
                         .withOption(multiNumOption)
                         .create();
     }

     private CommandLine safeParse(Group group, String[] args) {
         try {
             return parser.parse(group, args);
         } catch (OptionException e) {
             fail("OptionException should not be thrown for negative values: " + e.getMessage());
             return null; // never reached
         }
     }

     // 1. basic negative integer
     public void testNegativeNumber() {
         CommandLine cl = safeParse(numGroup, new String[] { "--num", "-42" });
         assertTrue(cl.hasOption(numOption));
         assertEquals("-42", cl.getValue(numOption));
     }

     // 2. negative value via equals syntax
     public void testNegativeNumberWithEquals() {
         CommandLine cl = safeParse(numGroup, new String[] { "--num=-42" });
         assertTrue(cl.hasOption(numOption));
         assertEquals("-42", cl.getValue(numOption));
     }

     // 3. negative zero
     public void testNegativeZero() {
         CommandLine cl = safeParse(numGroup, new String[] { "--num", "-0" });
         assertTrue(cl.hasOption(numOption));
         assertEquals("-0", cl.getValue(numOption));
     }

     // 4. negative floating-point value
     public void testNegativeFloat() {
         CommandLine cl = safeParse(numGroup, new String[] { "--num", "-3.14" });
         assertTrue(cl.hasOption(numOption));
         assertEquals("-3.14", cl.getValue(numOption));
     }

     // 5. option accepting multiple negative values
     public void testMultipleNegativeValues() {
         CommandLine cl = safeParse(multinumGroup, new String[] { "--num", "-1", "-2" });
         assertTrue(cl.hasOption(multiNumOption));
         List values = cl.getValues(multiNumOption);
         assertEquals("wrong number of values", 2, values.size());
         assertEquals("-1", values.get(0));
         assertEquals("-2", values.get(1));
     }

     // 6. "--" separator turns following negative numbers into anonymous arguments
     public void testDashDashSeparator() {
         CommandLine cl = safeParse(numAnonGroup, new String[] { "--num", "-1", "--", "-42" });
         assertTrue(cl.hasOption(numOption));
         assertEquals("-1", cl.getValue(numOption));
         assertTrue(cl.hasOption(anonArg));
         assertEquals("-42", cl.getValue(anonArg));
     }

     // 7. single dash as value
     public void testDashAlone() {
         CommandLine cl = safeParse(numGroup, new String[] { "--num", "-" });
         assertTrue(cl.hasOption(numOption));
         assertEquals("-", cl.getValue(numOption));
     }

     // 8. mixing a boolean flag with a negative option value
     public void testMixedOptionFlagNegativeValue() {
         CommandLine cl = safeParse(numFlagGroup, new String[] { "--flag", "--num", "-42" });
         assertTrue(cl.hasOption(flagOption));
         assertEquals(Boolean.TRUE, cl.getValue(flagOption));
         assertTrue(cl.hasOption(numOption));
         assertEquals("-42", cl.getValue(numOption));
     }

     // 9. direct looksLikeOption checks
     public void testLooksLikeOption() {
         CommandLine cl = safeParse(numGroup, new String[] { "--num", "-42" });
         WriteableCommandLine wcl = (WriteableCommandLine) cl;
         // negative numbers must NOT be considered option triggers
         assertFalse("should not look like option: -42", wcl.looksLikeOption("-42"));
         assertFalse("should not look like option: -3.14", wcl.looksLikeOption("-3.14"));
         assertFalse("should not look like option: -0", wcl.looksLikeOption("-0"));
         // well-known option triggers must be recognised
         assertTrue("should recognise --num", wcl.looksLikeOption("--num"));
         assertTrue("should recognise --flag", wcl.looksLikeOption("--flag"));
     }
 }