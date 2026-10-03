@Test
public void normalizesMixedCaseUnquotedCharsetToLowercase() {
    org.junit.Assert.assertEquals("utf-8",
        DataUtil.getCharsetFromContentType("text/html; charset=UtF-8"));
}

@Test
public void normalizesMixedCaseQuotedCharsetToLowercase() {
    org.junit.Assert.assertEquals("utf-8",
        DataUtil.getCharsetFromContentType("text/html; charset=\"UtF-8\""));
}

@Test
public void returnsNullWhenContentTypeHasNoCharsetParameter() {
    org.junit.Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; boundary=something"));
}