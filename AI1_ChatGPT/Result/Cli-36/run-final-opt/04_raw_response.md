@org.junit.Test
public void optionGroupToStringUsesInsertionOrderAndReflectsRequiredFlag() {
    org.apache.commons.cli.OptionGroup group = new org.apache.commons.cli.OptionGroup();
    group.addOption(new org.apache.commons.cli.Option("p", false, "primary"));
    group.addOption(new org.apache.commons.cli.Option(null, "extended", false, null));
    group.addOption(new org.apache.commons.cli.Option("x", false, null));

    org.junit.Assert.assertFalse(group.isRequired());
    group.setRequired(true);
    org.junit.Assert.assertTrue(group.isRequired());
    org.junit.Assert.assertEquals("[-p primary, --extended, -x]", group.toString());

    group.setRequired(false);
    org.junit.Assert.assertFalse(group.isRequired());
}

@org.junit.Test
public void addingRequiredOptionGroupMakesGroupRequiredInsteadOfMemberOption() {
    org.apache.commons.cli.Option option =
            new org.apache.commons.cli.Option("p", false, "primary");
    option.setRequired(true);

    org.apache.commons.cli.OptionGroup group = new org.apache.commons.cli.OptionGroup();
    group.setRequired(true);
    group.addOption(option);

    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOptionGroup(group);

    org.junit.Assert.assertEquals(1, options.getRequiredOptions().size());
    org.junit.Assert.assertSame(group, options.getRequiredOptions().get(0));
    org.junit.Assert.assertFalse(option.isRequired());
}

@org.junit.Test
public void directlyAddedOptionIsAvailableByShortAndLongAliases() {
    org.apache.commons.cli.Option option =
            new org.apache.commons.cli.Option("v", "verbose", false, "verbose output");
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    options.addOption(option);

    org.junit.Assert.assertSame(option, options.getOption("v"));
    org.junit.Assert.assertSame(option, options.getOption("-v"));
    org.junit.Assert.assertSame(option, options.getOption("--verbose"));
    org.junit.Assert.assertTrue(options.hasShortOption("-v"));
    org.junit.Assert.assertTrue(options.hasLongOption("--verbose"));
    org.junit.Assert.assertTrue(options.hasOption("verbose"));
}