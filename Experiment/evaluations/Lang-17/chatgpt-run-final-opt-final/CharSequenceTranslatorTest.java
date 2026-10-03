package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

public class CharSequenceTranslatorTest {

    private static final String SUPPLEMENTARY = "\uD842\uDFB7";

    private static class IdentityTranslator extends CharSequenceTranslator {
        @Override
        public int translate(final CharSequence input, final int index, final Writer out) {
            return 0;
        }
    }

    private static class SupplementaryReplacingTranslator extends CharSequenceTranslator {
        @Override
        public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
            if (Character.codePointAt(input, index) == 0x20BB7) {
                out.write("[SUP]");
                return 1;
            }
            return 0;
        }
    }

    @Test
    public void translatePreservesSupplementaryCharacterBeforeTrailingBmpCharacter() {
        CharSequenceTranslator translator = new IdentityTranslator();

        assertEquals(SUPPLEMENTARY + "A", translator.translate(SUPPLEMENTARY + "A"));
    }

    @Test
    public void translateToWriterPreservesSupplementaryCharacterBeforeTrailingBmpCharacter() throws IOException {
        CharSequenceTranslator translator = new IdentityTranslator();
        StringWriter writer = new StringWriter();

        translator.translate(SUPPLEMENTARY + "A", writer);

        assertEquals(SUPPLEMENTARY + "A", writer.toString());
    }

    @Test
    public void translateAdvancesPastConsumedSupplementaryCodePoint() {
        CharSequenceTranslator translator = new SupplementaryReplacingTranslator();

        assertEquals("[SUP]A", translator.translate(SUPPLEMENTARY + "A"));
    }

    @Test
    public void translateFindsSupplementaryCodePointAfterBmpPrefixAndContinuesAfterIt() {
        CharSequenceTranslator translator = new SupplementaryReplacingTranslator();

        assertEquals("x[SUP]A", translator.translate("x" + SUPPLEMENTARY + "A"));
    }

    @Test
    public void translateHandlesBmpOnlyAndEmptyInputs() {
        CharSequenceTranslator translator = new IdentityTranslator();

        assertEquals("plain text", translator.translate("plain text"));
        assertEquals("", translator.translate(""));
    }

    @Test
    public void translatePreservesSupplementaryCharacterAtEndOfInput() {
        CharSequenceTranslator translator = new IdentityTranslator();

        assertEquals("A" + SUPPLEMENTARY, translator.translate("A" + SUPPLEMENTARY));
    }

    @Test
    public void translateReturnsNullForNullCharSequence() {
        CharSequenceTranslator translator = new IdentityTranslator();

        assertEquals(null, translator.translate((CharSequence) null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void translateToWriterRejectsNullWriter() throws IOException {
        new IdentityTranslator().translate("value", (Writer) null);
    }

    @Test(expected = IOException.class)
    public void translateToWriterPropagatesWriterIOException() throws IOException {
        Writer failingWriter = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("write failure");
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };

        new IdentityTranslator().translate("A", failingWriter);
    }

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
}
