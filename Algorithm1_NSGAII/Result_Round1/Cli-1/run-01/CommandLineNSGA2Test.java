package org.apache.commons.cli;
import junit.framework.TestCase;
public class CommandLineNSGA2Test extends TestCase {
    public void testCase02MissingValue() throws Exception { Options o=new Options();o.addOption("f",true,"file");CommandLine c=new PosixParser().parse(o,new String[0]);assertNull(c.getOptionValue("f")); }
    public void testCase03Flag() throws Exception { Options o=new Options();o.addOption("v",false,"verbose");CommandLine c=new PosixParser().parse(o,new String[]{"-v"});assertTrue(c.hasOption("v"));assertNull(c.getOptionValue("v")); }
    public void testCase05DefaultPresent() throws Exception { Options o=new Options();o.addOption("f",true,"file");CommandLine c=new PosixParser().parse(o,new String[]{"-f","a"});assertEquals("a",c.getOptionValue("f","fallback")); }
    public void testCase07HyphenLookup() throws Exception { Options o=new Options();o.addOption("f","file",true,"file");CommandLine c=new PosixParser().parse(o,new String[]{"--file","a"});assertEquals("a",c.getOptionValue("--file")); }
    public void testCase08ObjectShort() throws Exception { Options o=new Options();Option n=new Option("n","number",true,"number");n.setType(PatternOptionBuilder.NUMBER_VALUE);o.addOption(n);CommandLine c=new PosixParser().parse(o,new String[]{"-n","42"});assertEquals(42,((Number)c.getOptionObject("n")).intValue()); }
    public void testCase11RemainingArgs() throws Exception { Options o=new Options();CommandLine c=new PosixParser().parse(o,new String[]{"tail"});assertEquals("tail",c.getArgs()[0]); }
    public void testCase12OptionIterator() throws Exception { Options o=new Options();o.addOption("v",false,"verbose");CommandLine c=new PosixParser().parse(o,new String[]{"-v"});assertEquals(1,c.getOptions().length);assertTrue(c.iterator().hasNext()); }
}
