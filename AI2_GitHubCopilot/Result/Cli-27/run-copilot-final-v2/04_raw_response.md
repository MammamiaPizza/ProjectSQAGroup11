public void testToStringWithShortOpt() {
        OptionGroup group = new OptionGroup();
        Option opt = new Option("s", false, "short option");
        group.addOption(opt);
        assertNotNull(group.toString());
    }

 public void testToStringMultipleOptions() {
     OptionGroup group = new OptionGroup();
     group.addOption(new Option("a", false, "alpha"));
     group.addOption(new Option("b", false, "beta"));
     assertNotNull(group.toString());
 }