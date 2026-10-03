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