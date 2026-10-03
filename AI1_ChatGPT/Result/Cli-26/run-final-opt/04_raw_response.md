public void testHasArgBooleanConfiguresAndClearsArgumentSupport()
{
    org.apache.commons.cli.Option withoutArgument =
        org.apache.commons.cli.OptionBuilder.hasArg(false).create("n");
    assertFalse(withoutArgument.hasArg());

    org.apache.commons.cli.Option withArgument =
        org.apache.commons.cli.OptionBuilder.hasArg(true).create("y");
    assertTrue(withArgument.hasArg());
}

public void testHasArgsConfiguresUnlimitedAndFixedArgumentCounts()
{
    org.apache.commons.cli.Option unlimited =
        org.apache.commons.cli.OptionBuilder.hasArgs().create("u");
    assertEquals(org.apache.commons.cli.Option.UNLIMITED_VALUES, unlimited.getArgs());

    org.apache.commons.cli.Option fixed =
        org.apache.commons.cli.OptionBuilder.hasArgs(2).create("f");
    assertEquals(2, fixed.getArgs());
}

public void testOptionalArgumentBuildersMarkArgumentsOptional()
{
    org.apache.commons.cli.Option singleOptional =
        org.apache.commons.cli.OptionBuilder.hasOptionalArg().create("s");
    assertEquals(1, singleOptional.getArgs());
    assertTrue(singleOptional.hasOptionalArg());

    org.apache.commons.cli.Option unlimitedOptional =
        org.apache.commons.cli.OptionBuilder.hasOptionalArgs().create("m");
    assertEquals(org.apache.commons.cli.Option.UNLIMITED_VALUES, unlimitedOptional.getArgs());
    assertTrue(unlimitedOptional.hasOptionalArg());
}

public void testBooleanRequiredAndDefaultValueSeparatorAreApplied()
{
    org.apache.commons.cli.Option required =
        org.apache.commons.cli.OptionBuilder.isRequired().create("r");
    assertTrue(required.isRequired());

    org.apache.commons.cli.Option optional =
        org.apache.commons.cli.OptionBuilder.isRequired(false)
            .withValueSeparator()
            .create("o");
    assertFalse(optional.isRequired());
    assertEquals((int) '=', (int) optional.getValueSeparator());
}