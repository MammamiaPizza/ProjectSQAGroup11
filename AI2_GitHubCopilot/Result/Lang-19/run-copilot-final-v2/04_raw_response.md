@Test
 public void testHexUpperCaseEntity() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&#X41;", 0, sw);
     org.junit.Assert.assertEquals("A", sw.toString());
     org.junit.Assert.assertEquals(6, consumed);
 }

 @Test
 public void testAmpersandAsLastCharacter() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&", 0, sw);
     org.junit.Assert.assertEquals("", sw.toString());
     org.junit.Assert.assertEquals(0, consumed);
 }

 @Test
 public void testDecimalEntityAtVeryEndNoSemicolon() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&#97", 0, sw);
     org.junit.Assert.assertEquals("a", sw.toString());
     org.junit.Assert.assertEquals(4, consumed);
 }

 @Test
 public void testEntityWithTrailingNonSemicolon() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&#98X", 0, sw);
     org.junit.Assert.assertEquals("b", sw.toString());
     org.junit.Assert.assertEquals(4, consumed);
 }