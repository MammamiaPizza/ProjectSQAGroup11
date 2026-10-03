@org.junit.Test
public void testContainsUnicodeEscapeIgnoresEscapedUnicodeMarker() {
  org.junit.Assert.assertFalse(
      PeepholeSubstituteAlternateSyntax.containsUnicodeEscape("\\\\u2028"));
}