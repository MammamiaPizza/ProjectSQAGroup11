@Test
public void testNullEscapeFollowedByDigit() {
    assertEquals(""\0001"", CodeGenerator.escapeToDoubleQuotedJsString("\0" + "1"));
}

@Test
public void testNullEscapeFollowedByMultipleDigits() {
    assertEquals(""\00012"", CodeGenerator.escapeToDoubleQuotedJsString("\0" + "12"));
}

@Test
public void testNullEscapeFollowedByInvalidOctalDigit() {
    assertEquals(""\0008"", CodeGenerator.escapeToDoubleQuotedJsString("\0" + "8"));
}

@Test
public void testNullEscapeInMiddleWithDigits() {
    assertEquals(""a1\000b2"", CodeGenerator.escapeToDoubleQuotedJsString("a1\0b2"));
}