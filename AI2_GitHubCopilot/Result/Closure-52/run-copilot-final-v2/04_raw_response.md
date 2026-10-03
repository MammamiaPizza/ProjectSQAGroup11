@Test
 public void testIsSimpleNumberRejectsOctalLikeNumbers() {
     assertFalse(CodeGenerator.isSimpleNumber("010"));
     assertFalse(CodeGenerator.isSimpleNumber("00"));
     assertFalse(CodeGenerator.isSimpleNumber("01"));
     assertFalse(CodeGenerator.isSimpleNumber("0123456789"));
 }

 @Test
 public void testEscapeToDoubleQuotedJsString() {
     assertEquals("\"hello\"", CodeGenerator.escapeToDoubleQuotedJsString("hello"));
     assertEquals("\"line\\nbreak\"", CodeGenerator.escapeToDoubleQuotedJsString("line\nbreak"));
 }

 @Test
 public void testRegexpEscape() {
     assertEquals("abc", CodeGenerator.regexpEscape("abc"));
     assertEquals("\\\\", CodeGenerator.regexpEscape("\\"));
 }