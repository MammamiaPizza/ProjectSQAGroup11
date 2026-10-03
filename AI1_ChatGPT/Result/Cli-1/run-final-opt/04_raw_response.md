public void testMissingOptionCharValuesAndDefaults() {
    org.apache.commons.cli.CommandLine line = new org.apache.commons.cli.CommandLine();

    assertNull(line.getOptionValues('z'));
    assertEquals("default", line.getOptionValue("missing", "default"));
    assertEquals("default", line.getOptionValue('z', "default"));
}