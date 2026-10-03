package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class Base64Codec98Test {

    private byte[] ascii(String value) throws Exception {
        return value.getBytes("US-ASCII");
    }

    private byte[] decodeWithInputStream(String encoded) throws Exception {
        Base64InputStream input = new Base64InputStream(
                new ByteArrayInputStream(ascii(encoded)), false);
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        int value;
        while ((value = input.read()) != -1) {
            decoded.write(value);
        }
        input.close();
        return decoded.toByteArray();
    }

    @Test
    public void inputStreamDecodesSingleBytePaddedValue() throws Exception {
        assertArrayEquals(ascii("M"), decodeWithInputStream("TQ=="));
    }

    @Test
    public void outputStreamDecodesSingleBytePaddedValue() throws Exception {
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        Base64OutputStream output = new Base64OutputStream(decoded, false);

        output.write('T');
        output.write('Q');
        output.write('=');
        output.write('=');
        output.close();

        assertArrayEquals(ascii("M"), decoded.toByteArray());
    }

    @Test
    public void inputStreamDecodesTwoBytePaddedValue() throws Exception {
        assertArrayEquals(ascii("Ma"), decodeWithInputStream("TWE="));
    }

    @Test
    public void outputStreamDecodesTwoBytePaddedValueAcrossWrites() throws Exception {
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        Base64OutputStream output = new Base64OutputStream(decoded, false);
        byte[] encoded = ascii("TWE=");

        output.write(encoded, 0, 2);
        output.write(encoded, 2, 2);
        output.close();

        assertArrayEquals(ascii("Ma"), decoded.toByteArray());
    }

    @Test
    public void inputStreamDecodesCompleteQuartet() throws Exception {
        assertArrayEquals(ascii("Man"), decodeWithInputStream("TWFu"));
    }

    @Test
    public void outputStreamDecodesCompleteQuartet() throws Exception {
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        Base64OutputStream output = new Base64OutputStream(decoded, false);

        output.write(ascii("TWFu"));
        output.close();

        assertArrayEquals(ascii("Man"), decoded.toByteArray());
    }

    @Test
    public void inputStreamIgnoresWhitespaceAroundPaddedData() throws Exception {
        assertArrayEquals(ascii("Ma"), decodeWithInputStream("T W\nE=\r"));
    }

    @Test
    public void emptyDecodingStreamsProduceNoData() throws Exception {
        Base64InputStream input = new Base64InputStream(
                new ByteArrayInputStream(new byte[0]), false);
        assertEquals(-1, input.read());
        input.close();

        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        Base64OutputStream output = new Base64OutputStream(decoded, false);
        output.close();

        assertArrayEquals(new byte[0], decoded.toByteArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBase64CharactersInLineSeparator() {
        new Base64(4, new byte[] { 'A' });
    }
}
