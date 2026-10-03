package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class X7875_NewUnixGeneratedTest {

    @Test
    public void parseAndReserializeZeroUidAndGidUsesOneByteZeroComponents() throws Exception {
        byte[] encoded = new byte[] { 1, 1, 0, 1, 0 };

        X7875_NewUnix field = new X7875_NewUnix();
        field.parseFromLocalFileData(encoded, 0, encoded.length);

        assertEquals(0L, field.getUID());
        assertEquals(0L, field.getGID());
        assertSerialization(field, encoded);

        X7875_NewUnix reparsed = new X7875_NewUnix();
        reparsed.parseFromLocalFileData(field.getLocalFileDataData(), 0,
                field.getLocalFileDataData().length);
        assertEquals(0L, reparsed.getUID());
        assertEquals(0L, reparsed.getGID());
    }

    @Test
    public void settersSerializeAndParseTypicalUidAndGidValues() throws Exception {
        X7875_NewUnix field = new X7875_NewUnix();
        field.setUID(1000L);
        field.setGID(2000L);

        byte[] expected = new byte[] {
            1, 2, (byte) 0xE8, 3, 2, (byte) 0xD0, 7
        };
        assertSerialization(field, expected);

        X7875_NewUnix parsed = new X7875_NewUnix();
        parsed.parseFromLocalFileData(expected, 0, expected.length);
        assertEquals(1000L, parsed.getUID());
        assertEquals(2000L, parsed.getGID());
    }

    @Test
    public void valuesWhoseBigEndianFormHasSignPaddingAreSerializedWithoutPadding() throws Exception {
        X7875_NewUnix field = new X7875_NewUnix();
        field.setUID(128L);
        field.setGID(255L);

        byte[] expected = new byte[] {
            1, 1, (byte) 0x80, 1, (byte) 0xFF
        };
        assertSerialization(field, expected);

        X7875_NewUnix parsed = new X7875_NewUnix();
        parsed.parseFromLocalFileData(expected, 0, expected.length);
        assertEquals(128L, parsed.getUID());
        assertEquals(255L, parsed.getGID());
    }

    @Test
    public void multiByteValuesUseLittleEndianVariableLengthEncoding() throws Exception {
        X7875_NewUnix field = new X7875_NewUnix();
        field.setUID(256L);
        field.setGID(65536L);

        byte[] expected = new byte[] {
            1, 2, 0, 1, 3, 0, 0, 1
        };
        assertSerialization(field, expected);

        X7875_NewUnix parsed = new X7875_NewUnix();
        parsed.parseFromLocalFileData(expected, 0, expected.length);
        assertEquals(256L, parsed.getUID());
        assertEquals(65536L, parsed.getGID());
    }

    @Test
    public void parsingHonorsTheSuppliedOffset() throws Exception {
        byte[] buffer = new byte[] {
            99, 98, 1, 1, 42, 1, 43, 97
        };

        X7875_NewUnix field = new X7875_NewUnix();
        field.parseFromLocalFileData(buffer, 2, 5);

        assertEquals(42L, field.getUID());
        assertEquals(43L, field.getGID());
        assertSerialization(field, new byte[] { 1, 1, 42, 1, 43 });
    }

    private static void assertSerialization(X7875_NewUnix field, byte[] expected) {
        byte[] actual = field.getLocalFileDataData();
        assertArrayEquals(expected, actual);
        assertEquals(expected.length, field.getLocalFileDataLength().getValue());
    }
}