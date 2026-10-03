package com.fasterxml.jackson.core.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ReaderAndUtf8LongFloatingPointTest
{
    private final JsonFactory factory = new JsonFactory();

    @Test
    public void readerParserHandlesLongFloatingPointAcrossReaderChunks() throws Exception {
        String number = longFraction();
        JsonParser parser = factory.createParser(new ChunkedReader("[" + number + "]", 37));
        try {
            assertSame(JsonToken.START_ARRAY, parser.nextToken());
            assertSame(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertLongNumberValues(parser, number);
            assertSame(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void utf8ParserHandlesLongFloatingPointAcrossStreamChunks() throws Exception {
        String number = longFraction();
        byte[] input = ("[" + number + "]").getBytes("UTF-8");
        JsonParser parser = factory.createParser(new ChunkedInputStream(input, 29));
        try {
            assertSame(JsonToken.START_ARRAY, parser.nextToken());
            assertSame(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertLongNumberValues(parser, number);
            assertSame(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void ordinaryFloatingPointLiteralIsParsedThroughReaderPath() throws Exception {
        JsonParser parser = factory.createParser(new ChunkedReader("-12.5e2", 2));
        try {
            assertSame(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals("-12.5e2", parser.getText());
            assertEquals(new BigDecimal("-12.5e2"), parser.getDecimalValue());
            assertEquals(-1250.0, parser.getDoubleValue(), 0.0);
            assertEquals(null, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void ordinaryFloatingPointLiteralIsParsedThroughUtf8Path() throws Exception {
        JsonParser parser = factory.createParser(
                new ChunkedInputStream("3.14159".getBytes("UTF-8"), 2));
        try {
            assertSame(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals("3.14159", parser.getText());
            assertEquals(new BigDecimal("3.14159"), parser.getDecimalValue());
            assertEquals(3.14159, parser.getDoubleValue(), 0.0);
            assertEquals(null, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void incompleteExponentIsRejectedByReaderParser() throws Exception {
        assertInvalidFloatingPoint(factory.createParser(new ChunkedReader("[1e+]", 2)));
    }

    @Test
    public void incompleteExponentIsRejectedByUtf8Parser() throws Exception {
        assertInvalidFloatingPoint(factory.createParser(
                new ChunkedInputStream("[1e+]".getBytes("UTF-8"), 2)));
    }

    private void assertLongNumberValues(JsonParser parser, String number) throws Exception {
        assertEquals(number.length(), parser.getTextLength());
        assertEquals(number, parser.getText());
        assertEquals(number, new String(parser.getTextCharacters(),
                parser.getTextOffset(), parser.getTextLength()));

        BigDecimal expected = new BigDecimal(number);
        assertEquals(expected, parser.getDecimalValue());
        assertEquals(expected.doubleValue(), parser.getDoubleValue(), 0.0);
        assertFalse(Double.isNaN(parser.getDoubleValue()));
    }

    private void assertInvalidFloatingPoint(JsonParser parser) throws Exception {
        try {
            assertSame(JsonToken.START_ARRAY, parser.nextToken());
            parser.nextToken();
            fail("An exponent sign must be followed by at least one digit");
        } catch (JsonParseException e) {
            assertNotNull(e.getMessage());
        } finally {
            parser.close();
        }
    }

    private String longFraction() {
        StringBuilder value = new StringBuilder("0.");
        for (int i = 0; i < 1200; ++i) {
            value.append((char) ('0' + ((i % 9) + 1)));
        }
        return value.toString();
    }

    private static final class ChunkedReader extends Reader {
        private final String input;
        private final int chunkSize;
        private int offset;

        ChunkedReader(String input, int chunkSize) {
            this.input = input;
            this.chunkSize = chunkSize;
        }

        @Override
        public int read(char[] buffer, int off, int len) {
            if (offset >= input.length()) {
                return -1;
            }
            int count = Math.min(len, Math.min(chunkSize, input.length() - offset));
            input.getChars(offset, offset + count, buffer, off);
            offset += count;
            return count;
        }

        @Override
        public void close() {
        }
    }

    private static final class ChunkedInputStream extends InputStream {
        private final ByteArrayInputStream delegate;
        private final int chunkSize;

        ChunkedInputStream(byte[] input, int chunkSize) {
            this.delegate = new ByteArrayInputStream(input);
            this.chunkSize = chunkSize;
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] buffer, int off, int len) {
            return delegate.read(buffer, off, Math.min(len, chunkSize));
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}