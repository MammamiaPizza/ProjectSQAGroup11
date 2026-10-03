@Test
public void returnsWithoutInvokingTranslatorWhenInputIsNull() throws Exception {
    final boolean[] invoked = new boolean[] { false };
    CharSequenceTranslator translator = new CharSequenceTranslator() {
        @Override
        public int translate(final CharSequence input, final int index, final java.io.Writer out)
                throws java.io.IOException {
            invoked[0] = true;
            return 0;
        }
    };
    java.io.StringWriter writer = new java.io.StringWriter();

    translator.translate((CharSequence) null, writer);

    assertEquals("", writer.toString());
    assertEquals(Boolean.FALSE, Boolean.valueOf(invoked[0]));
}

@Test
public void wrapsIOExceptionThrownDuringStringTranslation() {
    CharSequenceTranslator translator = new CharSequenceTranslator() {
        @Override
        public int translate(final CharSequence input, final int index, final java.io.Writer out)
                throws java.io.IOException {
            throw new java.io.IOException("translation failure");
        }
    };

    try {
        translator.translate("x");
        fail("Expected RuntimeException");
    } catch (RuntimeException expected) {
        assertEquals(java.io.IOException.class, expected.getCause().getClass());
        assertEquals("translation failure", expected.getCause().getMessage());
    }
}

@Test
public void withCombinesThisTranslatorBeforeAdditionalTranslators() {
    CharSequenceTranslator first = new CharSequenceTranslator() {
        @Override
        public int translate(final CharSequence input, final int index, final java.io.Writer out)
                throws java.io.IOException {
            if (input.charAt(index) == 'a') {
                out.write("A");
                return 1;
            }
            return 0;
        }
    };
    CharSequenceTranslator second = new CharSequenceTranslator() {
        @Override
        public int translate(final CharSequence input, final int index, final java.io.Writer out)
                throws java.io.IOException {
            if (input.charAt(index) == 'b') {
                out.write("B");
                return 1;
            }
            return 0;
        }
    };

    assertEquals("ABc", first.with(second).translate("abc"));
}