package org.jsoup.parser;

import org.jsoup.UncheckedIOException;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CharacterReaderIOExceptionTest {

    @Test
    public void uncheckedIOExceptionExposesOriginalIOException() {
        IOException cause = new IOException("binary input");
        UncheckedIOException exception = new UncheckedIOException(cause);

        assertSame(cause, exception.ioException());
        assertSame(cause, exception.getCause());
    }

    @Test
    public void constructorWrapsIOExceptionThrownDuringInitialRead() {
        IOException cause = new IOException("initial read failed");
        CharacterReader reader = null;

        try {
            reader = new CharacterReader(new ThrowingReader(cause, 0), 4);
            fail("Expected an UncheckedIOException");
        } catch (UncheckedIOException exception) {
            assertSame(cause, exception.ioException());
        } finally {
            if (reader != null) {
                assertTrue(reader.isEmpty());
            }
        }
    }

    @Test
    public void refillWrapsIOExceptionThrownAfterInitialBufferIsConsumed() {
        IOException cause = new IOException("refill failed");
        CharacterReader reader = new CharacterReader(new ThrowingReader(cause, 1), 4);

        assertEquals('a', reader.current());
        reader.advance();
        assertEquals('b', reader.current());
        reader.advance();
        assertEquals('c', reader.current());
        reader.advance();
        assertEquals('d', reader.current());
        reader.advance();

        try {
            reader.current();
            fail("Expected an UncheckedIOException when the buffer is refilled");
        } catch (UncheckedIOException exception) {
            assertSame(cause, exception.ioException());
        }
    }

    @Test
    public void readsNormallyAcrossBufferBoundariesAndReachesEof() {
        CharacterReader reader = new CharacterReader(new StringReader("abcdefghi"), 4);
        StringBuilder consumed = new StringBuilder();

        while (!reader.isEmpty()) {
            consumed.append(reader.current());
            reader.advance();
        }

        assertEquals("abcdefghi", consumed.toString());
        assertEquals((char) -1, reader.current());
        assertTrue(reader.isEmpty());
        assertEquals(9, reader.pos());
    }

    @Test
    public void consumeToLeavesDelimiterAsCurrentCharacter() {
        CharacterReader reader = new CharacterReader(new StringReader("abc:def"), 4);

        assertEquals("abc", reader.consumeTo(':'));
        assertEquals(':', reader.current());
        reader.advance();
        assertEquals("def", reader.consumeTo((char) -1));
        assertFalse(reader.isEmpty());
        reader.advance();
        assertTrue(reader.isEmpty());
    }

    private static final class ThrowingReader extends Reader {
        private final IOException failure;
        private final int successfulReadsBeforeFailure;
        private final String data = "abcdef";
        private int position;
        private int mark;
        private int reads;

        private ThrowingReader(IOException failure, int successfulReadsBeforeFailure) {
            this.failure = failure;
            this.successfulReadsBeforeFailure = successfulReadsBeforeFailure;
        }

        @Override
        public int read(char[] buffer, int offset, int length) throws IOException {
            if (reads++ >= successfulReadsBeforeFailure) {
                throw failure;
            }

            if (position >= data.length()) {
                return -1;
            }

            int count = Math.min(length, data.length() - position);
            data.getChars(position, position + count, buffer, offset);
            position += count;
            return count;
        }

        @Override
        public long skip(long count) {
            int skipped = (int) Math.min(count, data.length() - position);
            position += skipped;
            return skipped;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readAheadLimit) {
            mark = position;
        }

        @Override
        public void reset() {
            position = mark;
        }

        @Override
        public void close() {
        }
    }
}