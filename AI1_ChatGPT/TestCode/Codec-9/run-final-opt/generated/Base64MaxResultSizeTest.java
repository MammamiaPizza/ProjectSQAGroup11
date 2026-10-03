package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base64MaxResultSizeTest {

    @Test
    public void unchunkedThreeByteInputIsAllowedWhenMaximumEqualsEncodedSize() {
        byte[] encoded = Base64.encodeBase64(new byte[] { 'a', 'b', 'c' }, false, false, 4);

        assertArrayEquals(new byte[] { 'Y', 'W', 'J', 'j' }, encoded);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unchunkedThreeByteInputIsRejectedWhenMaximumIsOneLessThanEncodedSize() {
        Base64.encodeBase64(new byte[] { 'a', 'b', 'c' }, false, false, 3);
    }

    @Test
    public void unchunkedMimeBoundaryDoesNotRequireChunkSeparatorCapacity() {
        byte[] encoded = Base64.encodeBase64(new byte[57], false, false, 76);

        assertEquals(76, encoded.length);
        assertEquals((byte) 'A', encoded[0]);
        assertEquals((byte) 'A', encoded[75]);
    }

    @Test
    public void chunkedMimeBoundaryIncludesTrailingChunkSeparatorInMaximum() {
        byte[] encoded = Base64.encodeBase64(new byte[57], true, false, 78);

        assertEquals(78, encoded.length);
        assertEquals((byte) 'A', encoded[0]);
        assertEquals((byte) 'A', encoded[75]);
        assertEquals((byte) '\r', encoded[76]);
        assertEquals((byte) '\n', encoded[77]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unchunkedMimeBoundaryIsRejectedBelowItsEncodedSize() {
        Base64.encodeBase64(new byte[57], false, false, 75);
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunkedMimeBoundaryIsRejectedBelowItsEncodedSizeIncludingSeparator() {
        Base64.encodeBase64(new byte[57], true, false, 77);
    }
}
