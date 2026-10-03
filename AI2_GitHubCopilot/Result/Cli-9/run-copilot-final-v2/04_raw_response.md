public void testMissingThreeRequiredOptionsMessage() throws Exception {
    Options options = new Options();
    org.apache.commons.cli.Option a = new org.apache.commons.cli.Option("a", true, "a option");
    a.setRequired(true);
    options.addOption(a);
    org.apache.commons.cli.Option b = new org.apache.commons.cli.Option("b", true, "b option");
    b.setRequired(true);
    options.addOption(b);
    org.apache.commons.cli.Option c = new org.apache.commons.cli.Option("c", true, "c option");
    c.setRequired(true);
    options.addOption(c);
    try {
        parser.parse(options, new String[] {});
        fail("MissingOptionException expected");
    } catch (org.apache.commons.cli.MissingOptionException e) {
        assertTrue("Message must contain comma-space separator",
            e.getMessage().indexOf(", ") != -1);
        String msg = e.getMessage();
        assertTrue("Message must contain a", msg.indexOf("a") != -1);
        assertTrue("Message must contain b", msg.indexOf("b") != -1);
        assertTrue("Message must contain c", msg.indexOf("c") != -1);
    }
}

public void testDoubleDashTreatsAllAsArguments() throws Exception {
    Options options = new Options();
    options.addOption(new org.apache.commons.cli.Option("f", false, "flag"));
    org.apache.commons.cli.CommandLine cl = parser.parse(options, new String[]{"--", "-f"});
    assertFalse("-f should be treated as argument not option", cl.hasOption("f"));
}

public void testStopAtNonOptionWithUnknownToken() throws Exception {
    Options options = new Options();
    options.addOption(new org.apache.commons.cli.Option("a", false, "a option"));
    org.apache.commons.cli.CommandLine cl = parser.parse(options, new String[]{"--unknown", "-a"},
true);
    assertFalse("-a should not be parsed when stopAtNonOption and unknown", cl.hasOption("a"));
}

public void testParseWithoutPropertiesUsesDefaults() throws Exception {
    Options options = new Options();
    options.addOption(new org.apache.commons.cli.Option("f", true, "file"));
    org.apache.commons.cli.CommandLine cl = parser.parse(options, new String[]{"-f", "val"});
    assertTrue("Option -f should be recognized", cl.hasOption("f"));
}