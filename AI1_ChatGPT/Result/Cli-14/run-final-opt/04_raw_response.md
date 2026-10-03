public void testAppendUsageDisplaysOptionalCollapsedNamedGroup() {
    final org.apache.commons.cli2.option.Option option =
        new org.apache.commons.cli2.builder.DefaultOptionBuilder()
            .withLongName("verbose")
            .create();
    final org.apache.commons.cli2.option.GroupImpl group =
        (org.apache.commons.cli2.option.GroupImpl) new org.apache.commons.cli2.builder.GroupBuilder()
            .withName("files")
            .withMinimum(0)
            .withOption(option)
            .create();
    final java.util.Set settings = new java.util.HashSet();
    settings.add(org.apache.commons.cli2.util.DisplaySetting.DISPLAY_OPTIONAL);
    final StringBuffer buffer = new StringBuffer();

    group.appendUsage(buffer, settings, groupUsageComparator());

    assertEquals("[files]", buffer.toString());
}

public void testAppendUsageExpandsNamedGroupWhenRequested() {
    final org.apache.commons.cli2.option.Option option =
        new org.apache.commons.cli2.builder.DefaultOptionBuilder()
            .withLongName("verbose")
            .create();
    final org.apache.commons.cli2.option.GroupImpl group =
        (org.apache.commons.cli2.option.GroupImpl) new org.apache.commons.cli2.builder.GroupBuilder()
            .withName("files")
            .withOption(option)
            .create();
    final java.util.Set settings = new java.util.HashSet();
    settings.add(org.apache.commons.cli2.util.DisplaySetting.DISPLAY_GROUP_EXPANDED);
    settings.add(org.apache.commons.cli2.util.DisplaySetting.DISPLAY_GROUP_NAME);
    final StringBuffer buffer = new StringBuffer();

    group.appendUsage(buffer, settings, groupUsageComparator());

    assertEquals("files (" + option.getPreferredName() + ")", buffer.toString());
}

public void testAppendUsageIncludesAnonymousArgumentWhenRequested() {
    final org.apache.commons.cli2.option.Argument argument =
        new org.apache.commons.cli2.builder.ArgumentBuilder()
            .withName("file")
            .create();
    final org.apache.commons.cli2.option.GroupImpl group =
        (org.apache.commons.cli2.option.GroupImpl) new org.apache.commons.cli2.builder.GroupBuilder()
            .withOption(argument)
            .create();
    final StringBuffer hiddenBuffer = new StringBuffer();
    final StringBuffer displayedBuffer = new StringBuffer();
    final java.util.Set settings = new java.util.HashSet();
    settings.add(org.apache.commons.cli2.util.DisplaySetting.DISPLAY_GROUP_ARGUMENT);

    group.appendUsage(hiddenBuffer, org.apache.commons.cli2.util.DisplaySetting.NONE,
        groupUsageComparator());
    group.appendUsage(displayedBuffer, settings, groupUsageComparator());

    assertEquals("", hiddenBuffer.toString());
    assertTrue(displayedBuffer.toString().indexOf(argument.getPreferredName()) >= 0);
}

private java.util.Comparator groupUsageComparator() {
    return new java.util.Comparator() {
        public int compare(final Object first, final Object second) {
            return 0;
        }
    };
}