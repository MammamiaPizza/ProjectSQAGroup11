@org.junit.Test
public void translateToWriterWithNullInputLeavesWriterUnchanged() throws java.io.IOException {
    org.apache.commons.lang3.text.translate.CharSequenceTranslator translator =
            new org.apache.commons.lang3.text.translate.CharSequenceTranslator() {
                @Override
                public int translate(final CharSequence input, final int index,
                        final java.io.Writer out) {
                    return 0;
                }
            };
    java.io.StringWriter writer = new java.io.StringWriter();

    translator.translate((CharSequence) null, writer);

    org.junit.Assert.assertEquals("", writer.toString());
}

@org.junit.Test
public void translateWrapsIOExceptionThrownByTranslator() {
    org.apache.commons.lang3.text.translate.CharSequenceTranslator translator =
            new org.apache.commons.lang3.text.translate.CharSequenceTranslator() {
                @Override
                public int translate(final CharSequence input, final int index,
                        final java.io.Writer out) throws java.io.IOException {
                    throw new java.io.IOException("synthetic failure");
                }
            };

    try {
        translator.translate("input");
        org.junit.Assert.fail("Expected RuntimeException");
    } catch (RuntimeException ex) {
        org.junit.Assert.assertTrue(ex.getCause() instanceof java.io.IOException);
        org.junit.Assert.assertEquals("synthetic failure", ex.getCause().getMessage());
    }
}

@org.junit.Test
public void withCombinesThisTranslatorWithAdditionalTranslators() {
    org.apache.commons.lang3.text.translate.CharSequenceTranslator first =
            new org.apache.commons.lang3.text.translate.CharSequenceTranslator() {
                @Override
                public int translate(final CharSequence input, final int index,
                        final java.io.Writer out) throws java.io.IOException {
                    if (input.charAt(index) == 'x') {
                        out.write('X');
                        return 1;
                    }
                    return 0;
                }
            };
    org.apache.commons.lang3.text.translate.CharSequenceTranslator second =
            new org.apache.commons.lang3.text.translate.CharSequenceTranslator() {
                @Override
                public int translate(final CharSequence input, final int index,
                        final java.io.Writer out) throws java.io.IOException {
                    if (input.charAt(index) == 'y') {
                        out.write('Y');
                        return 1;
                    }
                    return 0;
                }
            };

    org.junit.Assert.assertEquals("XY", first.with(second).translate("xy"));
}

@org.junit.Test
public void hexReturnsUpperCaseHexadecimalForSupplementaryCodePoint() {
    org.junit.Assert.assertEquals("1F600",
            org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(0x1F600));
}