package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CharSequenceTranslatorLang857Test {

    @Test
    public void translatesConsumedSupplementaryCodePointAtEnd() {
        CharSequenceTranslator translator = new ReplacingTranslator(0x1F600, "<grinning>");

        assertEquals("<grinning>", translator.translate("\uD83D\uDE00"));
    }

    @Test
    public void translatesSupplementaryCodePointBetweenUntranslatedCharacters() {
        CharSequenceTranslator translator = new ReplacingTranslator(0x1F600, "<face>");

        assertEquals("before<face>after",
                translator.translate("before\uD83D\uDE00after"));
    }

    @Test
    public void advancesByCodePointsWhenSeveralCodePointsAreConsumed() {
        CharSequenceTranslator translator = new AllCodePointTranslator();

        assertEquals("[1F600][5A]", translator.translate("\uD83D\uDE00Z"));
    }

    @Test
    public void leavesSupplementaryCodePointUnchangedWhenNoTranslationOccurs() {
        RecordingNoOpTranslator translator = new RecordingNoOpTranslator();

        assertEquals("a\uD83D\uDE00b", translator.translate("a\uD83D\uDE00b"));
        assertEquals(Arrays.asList(0, 1, 3), translator.indices);
    }

    @Test
    public void translatesBmpCharacterAndLeavesOtherCharactersUntouched() {
        CharSequenceTranslator translator = new ReplacingTranslator((int) 'a', "<a>");

        assertEquals("<a>bc", translator.translate("abc"));
    }

    @Test
    public void returnsNullForNullInput() {
        CharSequenceTranslator translator = new RecordingNoOpTranslator();

        assertNull(translator.translate((CharSequence) null));
    }

    @Test
    public void rejectsNullWriter() throws Exception {
        CharSequenceTranslator translator = new RecordingNoOpTranslator();

        try {
            translator.translate("text", (Writer) null);
            fail("Expected IllegalArgumentException for a null Writer");
        } catch (IllegalArgumentException expected) {
            assertEquals("The Writer must not be null", expected.getMessage());
        }
    }

    @Test
    public void propagatesIOExceptionFromWriter() throws Exception {
        CharSequenceTranslator translator = new RecordingNoOpTranslator();

        try {
            translator.translate("x", new FailingWriter());
            fail("Expected IOException from Writer");
        } catch (IOException expected) {
            assertEquals("write failure", expected.getMessage());
        }
    }

    private static final class ReplacingTranslator extends CharSequenceTranslator {
        private final int target;
        private final String replacement;

        private ReplacingTranslator(int target, String replacement) {
            this.target = target;
            this.replacement = replacement;
        }

        @Override
        public int translate(CharSequence input, int index, Writer out) throws IOException {
            if (Character.codePointAt(input, index) == target) {
                out.write(replacement);
                return 1;
            }
            return 0;
        }
    }

    private static final class AllCodePointTranslator extends CharSequenceTranslator {
        @Override
        public int translate(CharSequence input, int index, Writer out) throws IOException {
            out.write("[");
            out.write(CharSequenceTranslator.hex(Character.codePointAt(input, index)));
            out.write("]");
            return 1;
        }
    }

    private static final class RecordingNoOpTranslator extends CharSequenceTranslator {
        private final List<Integer> indices = new ArrayList<Integer>();

        @Override
        public int translate(CharSequence input, int index, Writer out) {
            indices.add(index);
            return 0;
        }
    }

    private static final class FailingWriter extends Writer {
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
    }
}