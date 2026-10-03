package org.apache.commons.cli;

 import java.io.PrintWriter;
 import java.io.StringWriter;

 import junit.framework.TestCase;

 public class HelpFormatterTest extends TestCase {

     private HelpFormatter formatter;
     private StringWriter sw;
     private PrintWriter pw;

     protected void setUp() {
         formatter = new HelpFormatter();
         sw = new StringWriter();
         pw = new PrintWriter(sw);
     }

     protected void tearDown() {
         pw.close();
     }

     public void testDefaultArgName() {
         Option option =
                 OptionBuilder.hasArg().withArgName("argument").withDescription("foo").create('f');
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         assertTrue(sw.toString().contains("-f <argument>"));
     }

     public void testDefaultArgNameFallback() {
         Option option = OptionBuilder.hasArg().withDescription("foo").create('f');
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("-f <arg>"));
         assertFalse(out.contains("<argument>"));
     }

     public void testNoArgOption() {
         Option option = OptionBuilder.withDescription("foo").create('f');
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("-f"));
         assertFalse(out.contains("<"));
     }

     public void testOptionalArgWithCustomName() {
         Option option =

OptionBuilder.hasOptionalArg().withArgName("file").withDescription("foo").create('f');
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("["));
         assertTrue(out.contains("<file>"));
     }

     public void testOptionalArgDefaultName() {
         Option option = OptionBuilder.hasOptionalArg().withDescription("foo").create('f');
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("["));
         assertTrue(out.contains("<arg>"));
     }

     public void testNullArgNameFallback() {
         Option option = OptionBuilder.hasArg().withDescription("foo").create('f');
         option.setArgName(null);
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         assertTrue(sw.toString().contains("<arg>"));
     }

     public void testEmptyArgNameFallback() {
         Option option = OptionBuilder.hasArg().withDescription("foo").create('f');
         option.setArgName("");
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         assertFalse(sw.toString().contains("<arg>"));
     }

     public void testLongOptCustomArgName() {
         Option option = new Option("o", "output", true, "foo");
         option.setArgName("file");
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         assertTrue(sw.toString().contains("--output <file>"));
     }

     public void testMultipleOptionsMixed() {
         Options options = new Options();
         options.addOption(OptionBuilder.hasArg().withArgName("file").create('f'));
         options.addOption(OptionBuilder.hasArg().create('x'));
         options.addOption(OptionBuilder.withDescription("verbose").create('v'));
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("<file>"));
         assertTrue(out.contains("<arg>"));
         assertFalse(out.contains("-v <"));
     }

     public void testOptionGroupCustomArgName() {
         OptionGroup group = new OptionGroup();
         group.addOption(OptionBuilder.hasArg().withArgName("file").create('f'));
         Options options = new Options();
         options.addOptionGroup(group);
         formatter.printUsage(pw, 80, "app", options);
         assertTrue(sw.toString().contains("<file>"));
     }

     public void testHasArgsCustomArgName() {
         Option option = OptionBuilder.hasArgs().withArgName("files").create('f');
         Options options = new Options();
         options.addOption(option);
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("<files"));
     }

     public void testOptionBuilderReset() {
         OptionBuilder.withArgName("custom");
         Option option = OptionBuilder.hasArg().create('f');
         Option next = OptionBuilder.hasArg().create('x');
         Options options = new Options();
         options.addOption(option);
         options.addOption(next);
         formatter.printUsage(pw, 80, "app", options);
         String out = sw.toString();
         assertTrue(out.contains("-f <custom>"));
         assertTrue(out.contains("-x <arg>"));
     }
 }
