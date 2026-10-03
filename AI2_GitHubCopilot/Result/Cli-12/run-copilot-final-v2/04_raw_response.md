public void testGnuDashDash() {
    Options opts = new Options();
    opts.addOption("f", false, "f option");
    GnuParser parser = new GnuParser();
    CommandLine cmd = parser.parse(opts, new String[]{"--", "-f"});
    assertFalse(cmd.hasOption("f"));
    assertEquals(1, cmd.getArgs().length);
    assertEquals("-f", cmd.getArgs()[0]);
}

public void testGnuSingleDash() {
    Options opts = new Options();
    opts.addOption("f", false, "f option");
    GnuParser parser = new GnuParser();
    CommandLine cmd = parser.parse(opts, new String[]{"-"});
    assertEquals(1, cmd.getArgs().length);
    assertEquals("-", cmd.getArgs()[0]);
}