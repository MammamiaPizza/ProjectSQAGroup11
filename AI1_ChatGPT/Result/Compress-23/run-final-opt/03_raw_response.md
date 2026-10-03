package org.apache.commons.compress.archivers.sevenz;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class CodersLZMADecoderRegressionTest {

    @Test
    public void decodesWithStandardDictionarySize() throws Exception {
        InputStream decoded = new Coders.LZMADecoder().decode(
                new ByteArrayInputStream(new byte[0]),
                lzmaCoder(new byte[] { (byte) 0x5d, 0, 0, 16, 0 }),
                null);

        assertNotNull(decoded);
        decoded.close();
    }

    @Test
    public void acceptsNonDefaultDictionarySizeWhoseThirdByteHasHighBitSet() throws Exception {
        InputStream decoded = new Coders.LZMADecoder().decode(
                new ByteArrayInputStream(new byte[0]),
                lzmaCoder(new byte[] { (byte) 0x5d, 0, 0, (byte) 0x80, 0 }),
                null);

        assertNotNull(decoded);
        decoded.close();
    }

    @Test(expected = IOException.class)
    public void rejectsDictionaryLargerThanSupportedMaximum() throws Exception {
        new Coders.LZMADecoder().decode(
                new ByteArrayInputStream(new byte[0]),
                lzmaCoder(new byte[] {
                    (byte) 0x5d, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff
                }),
                null);
    }

    private Coder lzmaCoder(byte[] properties) {
        Coder coder = new Coder();
        coder.decompressionMethodId = SevenZMethod.LZMA.getId();
        coder.properties = properties;
        return coder;
    }
}