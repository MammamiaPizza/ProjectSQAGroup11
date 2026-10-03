public void testSingleDash() {
        Options options = new Options();
        options.addOption("a", false, "some option");
        PosixParser parser = new PosixParser();
        try {
            CommandLine cl = parser.parse(options, new String[]{"-"});
            assertNotNull(cl);
            assertEquals(1, cl.getArgs().length);
            assertEquals("-", cl.getArgs()[0]);
        } catch (ParseException e) {
            fail("Unexpected ParseException: " + e.getMessage());
        }
    }

 public void testLongOptionDirectAdd() {
     Options options = new Options();
     options.addOption("v", "verbose", false, "verbose output");
     PosixParser parser = new PosixParser();
     try {
         CommandLine cl = parser.parse(options, new String[]{"--verbose"});
         assertTrue(cl.hasOption("verbose"));
     } catch (ParseException e) {
         fail("Unexpected ParseException: " + e.getMessage());
     }
 }

 public void testUnrecognizedOptionWithStopAtNonOption() {
     Options options = new Options();
     options.addOption("a", false, "an option");
     PosixParser parser = new PosixParser();
     try {
         CommandLine cl = parser.parse(options, new String[]{"-9"}, true);
         String[] args = cl.getArgs();
         assertEquals(2, args.length);
         assertEquals("--", args[0]);
         assertEquals("-9", args[1]);
     } catch (ParseException e) {
         fail("Unexpected ParseException: " + e.getMessage());
     }
 }

 public void testBurstTokenWithConcatenatedArg() {
     Options options = new Options();
     options.addOption("o", true, "output file");
     PosixParser parser = new PosixParser();
     try {
         CommandLine cl = parser.parse(options, new String[]{"-ooutput.txt"});
         assertTrue(cl.hasOption("o"));
         assertEquals("output.txt", cl.getOptionValue("o"));
     } catch (ParseException e) {
         fail("Unexpected ParseException: " + e.getMessage());
     }
 }