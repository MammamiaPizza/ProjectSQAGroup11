package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.zip.ZipException;

import org.junit.Test;

public class Zip64ExtendedInformationExtraFieldTest {

    @Test
    public void acceptsExcessCentralDirectoryDataWhenOnlySizesAreRequired() throws Exception {
        ZipEightByteInteger size = new ZipEightByteInteger(123456789L);
        ZipEightByteInteger compressedSize = new ZipEightByteInteger(987654321L);
        ZipEightByteInteger offset = new ZipEightByteInteger(42L);
        ZipLong disk = new ZipLong(3L);

        byte[] data = concat(size.getBytes(), compressedSize.getBytes(),
                offset.getBytes(), disk.getBytes());

        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.parseFromCentralDirectoryData(data, 0, data.length);

        field.reparseCentralDirectoryData(true, true, false, false);

        assertArrayEquals(size.getBytes(), field.getSize().getBytes());
        assertArrayEquals(compressedSize.getBytes(),
                field.getCompressedSize().getBytes());
    }

    @Test
    public void reparsesExactCentralDirectorySizes() throws Exception {
        ZipEightByteInteger size = new ZipEightByteInteger(11L);
        ZipEightByteInteger compressedSize = new ZipEightByteInteger(22L);
        byte[] data = concat(size.getBytes(), compressedSize.getBytes());

        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.parseFromCentralDirectoryData(data, 0, data.length);
        field.reparseCentralDirectoryData(true, true, false, false);

        assertArrayEquals(size.getBytes(), field.getSize().getBytes());
        assertArrayEquals(compressedSize.getBytes(),
                field.getCompressedSize().getBytes());
        assertEquals(16, field.getCentralDirectoryLength().getValue());
    }

    @Test
    public void parsesSixteenByteLocalDataAsBothSizes() throws Exception {
        ZipEightByteInteger size = new ZipEightByteInteger(100L);
        ZipEightByteInteger compressedSize = new ZipEightByteInteger(90L);

        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.parseFromLocalFileData(
                concat(size.getBytes(), compressedSize.getBytes()), 0, 16);

        assertArrayEquals(size.getBytes(), field.getSize().getBytes());
        assertArrayEquals(compressedSize.getBytes(),
                field.getCompressedSize().getBytes());
        assertNull(field.getRelativeHeaderOffset());
        assertNull(field.getDiskStartNumber());
        assertEquals(16, field.getLocalFileDataLength().getValue());
    }

    @Test
    public void parsesTwentyFourByteLocalDataIncludingOffset() throws Exception {
        ZipEightByteInteger size = new ZipEightByteInteger(100L);
        ZipEightByteInteger compressedSize = new ZipEightByteInteger(90L);
        ZipEightByteInteger offset = new ZipEightByteInteger(1234L);

        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.parseFromLocalFileData(
                concat(size.getBytes(), compressedSize.getBytes(), offset.getBytes()),
                0, 24);

        assertArrayEquals(offset.getBytes(),
                field.getRelativeHeaderOffset().getBytes());
        assertNull(field.getDiskStartNumber());
        assertEquals(24, field.getCentralDirectoryLength().getValue());
    }

    @Test
    public void parsesTwentyEightByteLocalDataIncludingDiskStart() throws Exception {
        ZipEightByteInteger size = new ZipEightByteInteger(100L);
        ZipEightByteInteger compressedSize = new ZipEightByteInteger(90L);
        ZipEightByteInteger offset = new ZipEightByteInteger(1234L);
        ZipLong disk = new ZipLong(7L);

        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.parseFromLocalFileData(concat(size.getBytes(), compressedSize.getBytes(),
                offset.getBytes(), disk.getBytes()), 0, 28);

        assertArrayEquals(size.getBytes(), field.getSize().getBytes());
        assertArrayEquals(compressedSize.getBytes(),
                field.getCompressedSize().getBytes());
        assertArrayEquals(offset.getBytes(),
                field.getRelativeHeaderOffset().getBytes());
        assertArrayEquals(disk.getBytes(), field.getDiskStartNumber().getBytes());
    }

    @Test
    public void acceptsEmptyLocalData() throws Exception {
        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();

        field.parseFromLocalFileData(new byte[0], 0, 0);

        assertNull(field.getSize());
        assertNull(field.getCompressedSize());
        assertEquals(0, field.getLocalFileDataLength().getValue());
        assertArrayEquals(new byte[0], field.getLocalFileDataData());
    }

    @Test(expected = ZipException.class)
    public void rejectsUndersizedNonEmptyLocalData() throws Exception {
        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();

        field.parseFromLocalFileData(new byte[15], 0, 15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsLocalDataSerializationWithOnlyOneSize() {
        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.setSize(new ZipEightByteInteger(1L));

        field.getLocalFileDataData();
    }

    @Test
    public void settersProduceConsistentCentralAndLocalData() {
        ZipEightByteInteger size = new ZipEightByteInteger(123L);
        ZipEightByteInteger compressedSize = new ZipEightByteInteger(45L);
        ZipEightByteInteger offset = new ZipEightByteInteger(678L);
        ZipLong disk = new ZipLong(2L);

        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();
        field.setSize(size);
        field.setCompressedSize(compressedSize);
        field.setRelativeHeaderOffset(offset);
        field.setDiskStartNumber(disk);

        assertEquals(28, field.getCentralDirectoryLength().getValue());
        assertEquals(16, field.getLocalFileDataLength().getValue());
        assertArrayEquals(concat(size.getBytes(), compressedSize.getBytes(),
                offset.getBytes(), disk.getBytes()),
                field.getCentralDirectoryData());
        assertArrayEquals(concat(size.getBytes(), compressedSize.getBytes()),
                field.getLocalFileDataData());
    }

    @Test
    public void recognizesDiskStartOnlyCentralDirectoryData() throws Exception {
        ZipLong disk = new ZipLong(9L);
        Zip64ExtendedInformationExtraField field =
                new Zip64ExtendedInformationExtraField();

        field.parseFromCentralDirectoryData(disk.getBytes(), 0, 4);
        field.reparseCentralDirectoryData(false, false, false, true);

        assertArrayEquals(disk.getBytes(), field.getDiskStartNumber().getBytes());
        assertEquals(4, field.getCentralDirectoryLength().getValue());
    }

    private static byte[] concat(byte[]... parts) {
        int length = 0;
        for (byte[] part : parts) {
            length += part.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }
}
