@Test
public void testOptionGroupToStringEmptyAndPopulated() {
    // empty group
    OptionGroup group = new OptionGroup();
    assertEquals("[]", group.toString());

 // single short option with description
 group.addOption(new Option("x", false, "x desc"));
 assertEquals("[-x x desc]", group.toString());

 // add long-only option with description; order is insertion? Actually HashMap doesn't guarantee
order,
 // but we check that both options appear in toString.
 group.addOption(new Option(null, "longOnly", false, "long desc"));
 String str = group.toString();
 assertTrue(str.startsWith("[") && str.endsWith("]"));
 assertTrue(str.contains("-x x desc"));
 assertTrue(str.contains("--longOnly long desc"));
 assertTrue(str.contains(", "));

}

@Test
public void testOptionsAddOptionShortDescription() {
    Options opts = new Options();
    opts.addOption("v", "verbose");
    Option v = opts.getOption("v");
    assertNotNull(v);
    assertEquals("v", v.getOpt());
    assertNull(v.getLongOpt());
    assertEquals("verbose", v.getDescription());
    assertFalse(v.hasArg());
}

@Test
public void testOptionsHasShortAndLongOptionAndMatching() {
    Options opts = new Options();
    opts.addOption("v", "verbose", true, "verbose output");
    opts.addOption("V", "version", false, "print version");
    assertTrue(opts.hasShortOption("v"));
    assertTrue(opts.hasLongOption("verbose"));
    assertFalse(opts.hasShortOption("y"));
    assertFalse(opts.hasLongOption("verb"));
    assertTrue(opts.hasOption("v"));
    assertTrue(opts.hasOption("verbose"));
    assertFalse(opts.hasOption("y"));
    List<String> matches = opts.getMatchingOptions("v");
    assertTrue(matches.contains("verbose"));
    assertTrue(matches.contains("version"));
    assertEquals(0, opts.getMatchingOptions("z").size());
}

@Test
public void testOptionsGetOptionGroupForOptionInGroup() {
    OptionGroup group = new OptionGroup();
    group.setRequired(true);
    Option a = new Option("a", false, "alpha");
    Option b = new Option("b", false, "beta");
    group.addOption(a);
    group.addOption(b);
    Options opts = new Options();
    opts.addOptionGroup(group);
    assertSame(group, opts.getOptionGroup(a));
    assertSame(group, opts.getOptionGroup(b));
    Option c = new Option("c", false, "gamma");
    assertNull(opts.getOptionGroup(c));
}