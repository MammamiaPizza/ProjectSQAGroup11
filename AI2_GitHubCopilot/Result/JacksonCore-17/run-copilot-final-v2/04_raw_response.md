@Test
     public void testWriteRawStringNoEscaping() throws Exception {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         UTF8JsonGenerator gen = createGenerator(baos);
         String input = "hello world";
         gen.writeRaw(input);
         gen.close();
         byte[] output = baos.toByteArray();
         assertArrayEquals("Raw ASCII should appear verbatim",
                 input.getBytes("UTF-8"), output);
     }

     @Test
     public void testConstructorWithOutputOffset() throws Exception {
         // exercise constructor lines where outputTail = outputOffset,
         // outputBuffer, outputEnd, outputMaxContiguous, charBuffer are set
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buf = new byte[64];
         java.lang.reflect.Constructor<UTF8JsonGenerator> ctor =
                 UTF8JsonGenerator.class.getDeclaredConstructor(
                         com.fasterxml.jackson.core.io.IOContext.class,
                         int.class, com.fasterxml.jackson.core.ObjectCodec.class,
                         java.io.OutputStream.class, boolean.class, byte[].class, int.class);
         ctor.setAccessible(true);
         com.fasterxml.jackson.core.io.IOContext ctxt =
                 new com.fasterxml.jackson.core.io.IOContext(
                         new com.fasterxml.jackson.core.util.BufferRecycler(),
                         /*sourceRef*/ null, /*managedResource*/ false);
         UTF8JsonGenerator gen = ctor.newInstance(ctxt, 0, null, baos, true, buf, 4);
         gen.writeRaw("X");
         gen.close();
         byte[] output = baos.toByteArray();
         assertEquals("Single byte should be written regardless of offset",
                 1, output.length);
     }

     @Test
     public void testWriteRawStringWithSurrogatePairSlice() throws Exception {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         UTF8JsonGenerator gen = createGenerator(baos);
         // U+2070E (D841-DF0E) – a supplementary Unicode character
         String pair = String.valueOf(Character.toChars(0x2070E));
         String text = "!" + pair + "?";
         // write substring that includes the surrogate pair
         gen.writeRaw(text, 1, 3); // slice from index 1, length 3: the pair plus '?'
         gen.close();
         byte[] output = baos.toByteArray();
         // Expected: UTF-8 encoding of U+2070E followed by '?'
         byte[] expected = (new String(Character.toChars(0x2070E)) + "?")
                 .getBytes("UTF-8");
         assertArrayEquals("Slice containing surrogate pair must be serialized correctly",
                 expected, output);
     }

     @Test
     public void testEscapeNonAsciiFeatureEnabled() throws Exception {
         // enables ESCAPE_NON_ASCII in the underlying generator, covering line 119
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         int features = com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII.getMask();
         UTF8JsonGenerator gen = createGenerator(baos, features); // assume overload exists
         gen.writeStartArray();
         gen.writeString("e\u00E9");   // U+00E9 (é)
         gen.writeEndArray();
         gen.close();
         byte[] output = baos.toByteArray();
         // With ESCAPE_NON_ASCII on, 'é' should be output as \u00E9
         assertTrue("Output must contain escaped non-ASCII codepoint",
                 new String(output, "UTF-8").contains("\\u00E9"));
     }