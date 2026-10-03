@Test
public void createNumberRejectsDoubleMinusPrefix() {
    try {
        NumberUtils.createNumber("--2.0");
        org.junit.Assert.fail("Expected NumberFormatException");
    } catch (NumberFormatException expected) {
        // expected
    }
}