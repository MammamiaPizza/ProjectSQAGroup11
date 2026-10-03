@org.junit.Test
public void convenienceAddOptionOverloadsRegisterShortOptions() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    options.addOption("q", "quiet");
    options.addOption("f", true, "file");

    org.junit.Assert.assertTrue(options.hasShortOption("q"));
    org.junit.Assert.assertTrue(options.hasShortOption("f"));
    org.junit.Assert.assertNotNull(options.getOption("f"));
    org.junit.Assert.assertFalse(options.hasLongOption("q"));
}

@org.junit.Test
public void getOptionResolvesShortOptionWithLeadingHyphen() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("x", "execute", false, "execute");

    org.junit.Assert.assertSame(options.getOption("x"), options.getOption("-x"));
}

@org.junit.Test(expected = java.lang.UnsupportedOperationException.class)
public void getOptionsReturnsAnUnmodifiableCollection() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("q", "quiet");

    options.getOptions().clear();
}

@org.junit.Test(expected = java.lang.UnsupportedOperationException.class)
public void getRequiredOptionsReturnsAnUnmodifiableList() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    options.getRequiredOptions().add("required");
}