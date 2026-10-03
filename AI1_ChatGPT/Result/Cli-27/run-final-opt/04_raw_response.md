public void testSetRequiredCanBeToggled()
{
    OptionGroup group = new OptionGroup();

    assertFalse(group.isRequired());

    group.setRequired(true);
    assertTrue(group.isRequired());

    group.setRequired(false);
    assertFalse(group.isRequired());
}

public void testToStringFormatsShortAndLongOptions()
{
    OptionGroup group = new OptionGroup();
    group.addOption(new Option("f", "first"));
    group.addOption(new Option(null, "bar", false, "second"));

    String value = group.toString();

    assertTrue(value.equals("[-f first, --bar second]")
        || value.equals("[--bar second, -f first]"));
}