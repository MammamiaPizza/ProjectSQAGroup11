package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

public class CharSequenceTranslatorTest {

    private static final class SupplementaryReplacingTranslator extends CharSequenceTranslator {
        @Override
        public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
            if (Character.codePointAt(input, index) == 0x1F600) {
                out.write("[FACE]");
                return 1;
            }
            return 0;
        }
    }

    private static final class TwoCodePointTranslator extends CharSequenceTranslator {
        @Override
        public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
            if (index == 0) {
                out.write("[TWO]");
                return 2;
            }
            return 0;
        }
    }

    private static final class NoOpTranslator extends CharSequenceTranslator {
        @Override
        public int translate(final CharSequence input, final int index, final Writer out) {
            return 0;
        }
    }

    @Test
    public void translateConsumesFinalSupplementaryCodePoint() {
        final CharSequenceTranslator translator = new SupplementaryReplacingTranslator();

        assertEquals("[FACE]", translator.translate("\uD83D\uDE00"));
    }

    @Test
    public void translateConsumesSupplementaryCodePointWithinOrdinaryText() {
        final CharSequenceTranslator translator = new SupplementaryReplacingTranslator();

        assertEquals("before[FACE]after", translator.translate("before\uD83D\uDE00after"));
    }

    @Test
    public void translateCopiesSupplementaryCodePointWhenNoTranslatorConsumesIt() {
        final CharSequenceTranslator translator = new NoOpTranslator();
        final String input = "a\uD83D\uDE00b";

        assertEquals(input, translator.translate(input));
    }

    @Test
    public void translateAdvancesByCodePointsForMultipleConsumedCodePoints() {
        final CharSequenceTranslator translator = new TwoCodePointTranslator();

        assertEquals("[TWO]Z", translator.translate("A\uD83D\uDE00Z"));
    }

    @Test
    public void translateReturnsNullForNullCharSequence() {
        final CharSequenceTranslator translator = new NoOpTranslator();

        assertEquals(null, translator.translate((CharSequence) null));
    }

    @Test
    public void translateRejectsNullWriter() throws IOException {
        final CharSequenceTranslator translator = new NoOpTranslator();

        try {
            translator.translate("text", (Writer) null);
            fail("Expected IllegalArgumentException for a null Writer");
        } catch (final IllegalArgumentException expected) {
            assertEquals("The Writer must not be null", expected.getMessage());
        }
    }

    @Test
    public void translatePropagatesWriterIOException() {
        final CharSequenceTranslator translator = new NoOpTranslator();
        final Writer failingWriter = new Writer() {
            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {
                throw new IOException("write failed");
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };

        try {
            translator.translate("x", failingWriter);
            fail("Expected IOException from the Writer");
        } catch (final IOException expected) {
            assertEquals("write failed", expected.getMessage());
        }
    }

    @Test
    public void translateToWriterLeavesWriterUntouchedForNullInput() throws IOException {
        final CharSequenceTranslator translator = new NoOpTranslator();
        final StringWriter writer = new StringWriter();

        translator.translate(null, writer);

        assertEquals("", writer.toString());
    }
}
