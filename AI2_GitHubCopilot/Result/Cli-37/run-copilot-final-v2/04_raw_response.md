@Test(expected = MissingOptionException.class)
public void testMissingRequiredOption() throws ParseException {
    Options options = new Options();
    Option requiredA = new Option("a", "a-option", false, "required a");
    requiredA.setRequired(true);
    options.addOption(requiredA);
    new DefaultParser().parse(options, new String[]{});
}

@Test(expected = AmbiguousOptionException.class)
public void testAmbiguousLongOption() throws ParseException {
    Options options = new Options();
    options.addOption(new Option(null, "opt1", false, "option 1"));
    options.addOption(new Option(null, "opt2", false, "option 2"));
    new DefaultParser().parse(options, new String[]{"--opt"});
}

@Test
public void testLongOptionWithEqual() throws ParseException {
    Options options = new Options();
    options.addOption(new Option(null, "processor", true, "processor name"));
    CommandLine cmd = new DefaultParser().parse(options, new String[]{"--processor=xml"});
    assertTrue("Option --processor should be present", cmd.hasOption("processor"));
    assertEquals("xml", cmd.getOptionValue("processor"));
}

@Test(expected = UnrecognizedOptionException.class)
public void testStopAtNonOptionConcatenated() throws ParseException {
    Options options = new Options();
    options.addOption(new Option("a", false, "option a"));
    new DefaultParser().parse(options, new String[]{"-a1"}, true);
}