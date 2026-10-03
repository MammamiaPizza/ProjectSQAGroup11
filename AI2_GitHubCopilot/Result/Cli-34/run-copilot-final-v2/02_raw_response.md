package org.apache.commons.cli;

 import junit.framework.TestCase;

 public class OptionBuilderTest extends TestCase {

     public OptionBuilderTest(String testName) {
         super(testName);
     }

     public void testTwoCompleteOptions() {
         Option opt1 = OptionBuilder.withLongOpt("complete1")
                 .withDescription("desc")
                 .hasArg()
                 .withType(String.class)
                 .create();

         Option opt2 = OptionBuilder.withLongOpt("complete2")
                 .withDescription("desc")
                 .hasArg()
                 .withType(String.class)
                 .create();

         assertEquals(String.class, opt1.getType());
         assertEquals(String.class, opt2.getType());
     }

     public void testTypeCarryoverAcrossMultipleCreates() {
         Option opt1 = OptionBuilder.withLongOpt("t1")
                 .withDescription("d1")
                 .hasArg()
                 .withType(String.class)
                 .create();

         Option opt2 = OptionBuilder.withLongOpt("t2")
                 .withDescription("d2")
                 .hasArg()
                 .withType(String.class)
                 .create();

         Option opt3 = OptionBuilder.withLongOpt("t3")
                 .withDescription("d3")
                 .hasArg()
                 .withType(String.class)
                 .create();

         assertEquals(String.class, opt1.getType());
         assertEquals(String.class, opt2.getType());
         assertEquals(String.class, opt3.getType());
     }

     public void testOptionSetTypeGetType() {
         Option option = new Option("f", "desc");
         option.setType(Integer.class);
         assertEquals(Integer.class, option.getType());
     }

     public void testCreateWithType() {
         Option option = OptionBuilder.withLongOpt("typed")
                 .withDescription("desc")
                 .hasArg()
                 .withType(Integer.class)
                 .create();
         assertEquals(Integer.class, option.getType());
     }

     public void testCreateWithoutTypeReturnsNull() {
         Option option = OptionBuilder.withLongOpt("notyped")
                 .withDescription("desc")
                 .hasArg()
                 .create();
         assertNull(option.getType());
     }

     public void testResetAfterCreateThrowsOnBareCreate() {
         OptionBuilder.withLongOpt("before")
                 .withDescription("desc")
                 .create();

         try {
             OptionBuilder.create();
             fail("Expected IllegalArgumentException because longopt is null after reset");
         } catch (IllegalArgumentException expected) {
             // expected
         }
     }

     public void testCreateWithLongOpt() {
         String longOpt = "my-long-opt";
         Option option = OptionBuilder.withLongOpt(longOpt)
                 .withDescription("desc")
                 .create();
         assertTrue(option.hasLongOpt());
         assertEquals(longOpt, option.getLongOpt());
     }

     public void testOptionalArg() {
         Option option = OptionBuilder.withLongOpt("optarg")
                 .withDescription("desc")
                 .hasOptionalArg()
                 .create();
         assertTrue(option.hasOptionalArg());
         assertEquals(1, option.getArgs());
         assertTrue(option.hasArg());
     }

     public void testArgName() {
         String argName = "input-file";
         Option option = OptionBuilder.withLongOpt("opt")
                 .withDescription("desc")
                 .withArgName(argName)
                 .create();
         assertEquals(argName, option.getArgName());
         assertTrue(option.hasArgName());
     }

     public void testValueSeparator() {
         char separator = ':';
         Option option = OptionBuilder.withLongOpt("opt")
                 .withDescription("desc")
                 .withValueSeparator(separator)
                 .create();
         assertEquals(separator, option.getValueSeparator());
         assertTrue(option.hasValueSeparator());
     }

     public void testDescription() {
         String description = "This is a test option";
         Option option = OptionBuilder.withLongOpt("opt")
                 .withDescription(description)
                 .create();
         assertEquals(description, option.getDescription());
     }

     public void testMultipleCreateObjectsAreDistinct() {
         Option opt1 = OptionBuilder.withLongOpt("first")
                 .withDescription("desc")
                 .hasArg()
                 .withType(String.class)
                 .create();

         Option opt2 = OptionBuilder.withLongOpt("second")
                 .withDescription("desc")
                 .hasArg()
                 .withType(String.class)
                 .create();

         assertNotSame(opt1, opt2);
         assertEquals(String.class, opt1.getType());
         assertEquals(String.class, opt2.getType());
     }
 }