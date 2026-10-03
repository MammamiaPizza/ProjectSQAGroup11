@Test
public void testDoubleDashAfterLongOptionIsNotReturnedAsArgumentWhenStoppingAtNonOption() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("b", "beta", false, "beta option");

    org.apache.commons.cli.CommandLine line =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "--beta", "--", "-x" }, true);

    assertTrue(line.hasOption("b"));
    assertEquals(1, line.getArgs().length);
    assertEquals("-x", line.getArgs()[0]);
}

@Test
public void testDoubleDashAtBeginningIsNotReturnedAsArgumentWhenStoppingAtNonOption() throws Exception
{
    org.apache.commons.cli.CommandLine line =
        new org.apache.commons.cli.PosixParser().parse(
            new org.apache.commons.cli.Options(),
            new String[] { "--", "value", "-x" }, true);

    assertEquals(2, line.getArgs().length);
    assertEquals("value", line.getArgs()[0]);
    assertEquals("-x", line.getArgs()[1]);
}

@Test
public void testLongOptionWithEqualsSeparatesItsValue() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("o", "output", true, "output file");

    org.apache.commons.cli.CommandLine line =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "--output=result.txt" });

    assertTrue(line.hasOption("o"));
    assertEquals("result.txt", line.getOptionValue("o"));
}

@Test
public void testSingleHyphenIsPreservedAsAnArgument() throws Exception
{
    org.apache.commons.cli.CommandLine line =
        new org.apache.commons.cli.PosixParser().parse(
            new org.apache.commons.cli.Options(), new String[] { "-" }, true);

    assertEquals(1, line.getArgs().length);
    assertEquals("-", line.getArgs()[0]);
}