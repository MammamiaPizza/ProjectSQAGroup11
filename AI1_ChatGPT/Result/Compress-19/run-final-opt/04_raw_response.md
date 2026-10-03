@org.junit.Test
public void constructorsInitializeZip64ValuesAndHeaderId() {
    final org.apache.commons.compress.archivers.zip.ZipEightByteInteger size =
        new org.apache.commons.compress.archivers.zip.ZipEightByteInteger(123L);
    final org.apache.commons.compress.archivers.zip.ZipEightByteInteger compressedSize =
        new org.apache.commons.compress.archivers.zip.ZipEightByteInteger(45L);
    final Zip64ExtendedInformationExtraField sizesOnly =
        new Zip64ExtendedInformationExtraField(size, compressedSize);

    org.junit.Assert.assertEquals(1, sizesOnly.getHeaderId().getValue());
    org.junit.Assert.assertArrayEquals(size.getBytes(), sizesOnly.getSize().getBytes());
    org.junit.Assert.assertArrayEquals(compressedSize.getBytes(),
        sizesOnly.getCompressedSize().getBytes());
    org.junit.Assert.assertNull(sizesOnly.getRelativeHeaderOffset());
    org.junit.Assert.assertNull(sizesOnly.getDiskStartNumber());
    org.junit.Assert.assertEquals(16, sizesOnly.getCentralDirectoryLength().getValue());

    final org.apache.commons.compress.archivers.zip.ZipEightByteInteger offset =
        new org.apache.commons.compress.archivers.zip.ZipEightByteInteger(67L);
    final org.apache.commons.compress.archivers.zip.ZipLong disk =
        new org.apache.commons.compress.archivers.zip.ZipLong(2L);
    final Zip64ExtendedInformationExtraField complete =
        new Zip64ExtendedInformationExtraField(size, compressedSize, offset, disk);

    org.junit.Assert.assertArrayEquals(offset.getBytes(),
        complete.getRelativeHeaderOffset().getBytes());
    org.junit.Assert.assertArrayEquals(disk.getBytes(),
        complete.getDiskStartNumber().getBytes());
    org.junit.Assert.assertEquals(28, complete.getCentralDirectoryLength().getValue());
    org.junit.Assert.assertEquals(28, complete.getCentralDirectoryData().length);
}

@org.junit.Test
public void serializesCentralDirectoryDataWhenOnlyCompressedSizeIsPresent() {
    final org.apache.commons.compress.archivers.zip.ZipEightByteInteger compressedSize =
        new org.apache.commons.compress.archivers.zip.ZipEightByteInteger(45L);
    final Zip64ExtendedInformationExtraField field =
        new Zip64ExtendedInformationExtraField();

    field.setCompressedSize(compressedSize);

    org.junit.Assert.assertEquals(8, field.getCentralDirectoryLength().getValue());
    org.junit.Assert.assertArrayEquals(compressedSize.getBytes(),
        field.getCentralDirectoryData());
}

@org.junit.Test
public void serializesCentralDirectoryOffsetAndDiskWithoutSizes() {
    final org.apache.commons.compress.archivers.zip.ZipEightByteInteger offset =
        new org.apache.commons.compress.archivers.zip.ZipEightByteInteger(67L);
    final org.apache.commons.compress.archivers.zip.ZipLong disk =
        new org.apache.commons.compress.archivers.zip.ZipLong(2L);
    final Zip64ExtendedInformationExtraField field =
        new Zip64ExtendedInformationExtraField();

    field.setRelativeHeaderOffset(offset);
    field.setDiskStartNumber(disk);

    final byte[] expected = new byte[12];
    System.arraycopy(offset.getBytes(), 0, expected, 0, 8);
    System.arraycopy(disk.getBytes(), 0, expected, 8, 4);

    org.junit.Assert.assertEquals(12, field.getCentralDirectoryLength().getValue());
    org.junit.Assert.assertArrayEquals(expected, field.getCentralDirectoryData());
}