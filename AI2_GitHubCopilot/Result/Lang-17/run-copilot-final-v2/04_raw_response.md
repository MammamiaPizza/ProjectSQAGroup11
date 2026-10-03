@Test
 public void testHexReturnsHexStringForCodepoints() {
     String hexBMP = CharSequenceTranslator.hex(0x41);
     assertNotNull(hexBMP);
     assertTrue(hexBMP.contains("41"));
     assertEquals(hexBMP, hexBMP.toUpperCase());
     String hexSupp = CharSequenceTranslator.hex(0x20BB7);
     assertNotNull(hexSupp);
     assertTrue(hexSupp.contains("20BB7"));
     assertEquals(hexSupp, hexSupp.toUpperCase());
 }

 @Test
 public void testTranslateStringWrapsIOExceptionInRuntimeException() {
     CharSequenceTranslator throwingTranslator = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, java.io.Writer out) throws
java.io.IOException {
             throw new java.io.IOException("test");
         }
     };
     try {
         throwingTranslator.translate("anything");
         fail("Expected RuntimeException");
     } catch (RuntimeException ex) {
         assertTrue(ex.getCause() instanceof java.io.IOException);
         assertEquals("test", ex.getCause().getMessage());
     }
 }

 @Test
 public void testWithCombinesTranslators() throws java.io.IOException {
     CharSequenceTranslator aToB = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, java.io.Writer out) throws
java.io.IOException {
             if (input.charAt(index) == 'a') {
                 out.write('b');
                 return 1;
             }
             return 0;
         }
     };
     CharSequenceTranslator bToC = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, java.io.Writer out) throws
java.io.IOException {
             if (input.charAt(index) == 'b') {
                 out.write('c');
                 return 1;
             }
             return 0;
         }
     };
     CharSequenceTranslator combined = aToB.with(bToC);
     assertEquals("c", combined.translate("a"));
     assertEquals("x", combined.translate("x"));
     assertEquals("cbc", combined.translate("aba"));
 }

 @Test
 public void testWithMultipleTranslators() throws java.io.IOException {
     CharSequenceTranslator aToB = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, java.io.Writer out) throws
java.io.IOException {
             if (input.charAt(index) == 'a') {
                 out.write('b');
                 return 1;
             }
             return 0;
         }
     };
     CharSequenceTranslator bToC = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, java.io.Writer out) throws
java.io.IOException {
             if (input.charAt(index) == 'b') {
                 out.write('c');
                 return 1;
             }
             return 0;
         }
     };
     CharSequenceTranslator cToD = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, java.io.Writer out) throws
java.io.IOException {
             if (input.charAt(index) == 'c') {
                 out.write('d');
                 return 1;
             }
             return 0;
         }
     };
     CharSequenceTranslator combined = aToB.with(bToC, cToD);
     assertEquals("d", combined.translate("a"));
 }