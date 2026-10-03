@Test
    public void testParseNullArguments() throws Exception {
        Options options = new Options();
        options.addOption("f", false, "dummy");
        CommandLine cmd = parser.parse(options, (String[]) null);
        assertFalse(cmd.hasOption("f"));
        assertNotNull(cmd.getArgs());
        assertEquals(0, cmd.getArgs().length);
    }

 @Test
 public void testParseWithSingleDash() throws Exception {
     Options options = new Options();
     options.addOption("f", false, "dummy");
     CommandLine cmd = parser.parse(options, new String[]{"-f", "-"});
     assertTrue(cmd.hasOption("f"));
     String[] args = cmd.getArgs();
     assertEquals(1, args.length);
     assertEquals("-", args[0]);
 }

 @Test
 public void testParseWithDoubleDash() throws Exception {
     Options options = new Options();
     options.addOption("x", false, "dummy");
     CommandLine cmd = parser.parse(options, new String[]{"--", "-x", "foo"});
     assertFalse(cmd.hasOption("x"));
     String[] args = cmd.getArgs();
     assertEquals(2, args.length);
     assertEquals("-x", args[0]);
     assertEquals("foo", args[1]);
 }

 @Test
 public void testParseStopAtNonOption() throws Exception {
     org.apache.commons.cli.BasicParser stopParser = new org.apache.commons.cli.BasicParser();
     Options options = new Options();
     options.addOption("f", false, "dummy");
     CommandLine cmd = stopParser.parse(options, new String[]{"-f", "bar"}, true);
     assertTrue(cmd.hasOption("f"));
     String[] args = cmd.getArgs();
     assertEquals(1, args.length);
     assertEquals("bar", args[0]);
 }