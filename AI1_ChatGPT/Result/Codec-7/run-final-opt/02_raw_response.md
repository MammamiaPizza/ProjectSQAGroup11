package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base64Codec7BugTest {

    @Test
    public void encodeBase64StringDoesNotAppendLineSeparatorForSingleByte() {
        assertEquals("Zg==", Base64.encodeBase64String(new byte[] { 'f' }));
    }

    @Test
    public void encodeBase64StringDoesNotAppendLineSeparatorForHelloWorld() {
        byte[] input = new byte[] {
            'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'
        };

        assertEquals("SGVsbG8gV29ybGQ=", Base64.encodeBase64String(input));
    }

    @Test
    public void unchunkedByteArrayEncodingOfSingleByteHasOnlyPaddingAtEnd() {
        assertArrayEquals(
                new byte[] { 'Z', 'g', '=', '=' },
                Base64.encodeBase64(new byte[] { 'f' }));
    }

    @Test
    public void unchunkedEncodingHandlesCompleteAndPartialThreeByteGroups() {
        assertEquals("TWFu", Base64.encodeBase64String(new byte[] { 'M', 'a', 'n' }));
        assertEquals("TWE=", Base64.encodeBase64String(new byte[] { 'M', 'a' }));
    }

    @Test
    public void defaultInstanceEncodeToStringIsUnchunked() {
        Base64 base64 = new Base64();

        assertEquals("SGVsbG8gV29ybGQ=",
                base64.encodeToString(new byte[] {
                    'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'
                }));
    }

    @Test
    public void explicitlyChunkedEncodingRetainsItsLineSeparator() {
        assertEquals("Zg==\r\n",
                new String(Base64.encodeBase64Chunked(new byte[] { 'f' })));
    }
}