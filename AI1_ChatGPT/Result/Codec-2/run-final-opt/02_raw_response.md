package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class Base64StreamingRegressionTest {

    @Test
    public void emptyBase64InputStreamReturnsEofForSingleAndBufferedReads() throws Exception {
        Base64InputStream singleByteStream =
                new Base64InputStream(new ByteArrayInputStream(new byte[0]));
        assertEquals(-1, singleByteStream.read());
        assertEquals(-1, singleByteStream.read());
        singleByteStream.close();

        Base64InputStream bufferedStream =
                new Base64InputStream(new ByteArrayInputStream(new byte[0]));
        assertEquals(-1, bufferedStream.read(new byte[8]));
        assertEquals(-1, bufferedStream.read(new byte[8]));
        bufferedStream.close();
    }

    @Test
    public void emptyBase64OutputStreamDoesNotEmitLineSeparatorOnFlushOrClose() throws Exception {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        Base64OutputStream stream = new Base64OutputStream(target);

        stream.flush();
        assertEquals(0, target.size());

        stream.close();
        assertArrayEquals(new byte[0], target.toByteArray());
    }

    @Test
    public void base64OutputStreamEncodesFinalPartialQuantumAndTerminatesChunk() throws Exception {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        Base64OutputStream stream = new Base64OutputStream(target);

        stream.write(new byte[] { 'M', 'a' });
        stream.close();

        assertArrayEquals(
                new byte[] { 'T', 'W', 'E', '=', '\r', '\n' },
                target.toByteArray());
    }

    @Test
    public void base64OutputStreamDoesNotAddExtraSeparatorAfterExactChunkBoundary() throws Exception {
        byte[] input = new byte[57];
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        Base64OutputStream stream = new Base64OutputStream(target);

        stream.write(input);
        stream.close();

        byte[] expected = new byte[78];
        for (int i = 0; i < 76; i++) {
            expected[i] = 'A';
        }
        expected[76] = '\r';
        expected[77] = '\n';

        assertArrayEquals(expected, target.toByteArray());
    }

    @Test
    public void base64InputStreamDecodesDataAndReachesEofAfterFinalQuantum() throws Exception {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { 'T', 'W', 'E', '=', '\r', '\n' }));

        byte[] decoded = new byte[4];
        assertEquals(2, stream.read(decoded));
        assertArrayEquals(new byte[] { 'M', 'a', 0, 0 }, decoded);
        assertEquals(-1, stream.read());
        stream.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsLineSeparatorContainingBase64Characters() {
        new Base64(76, new byte[] { 'A' });
    }
}