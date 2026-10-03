@org.junit.Test
public void localAndCentralDirectoryDataContainExpectedTimestampFields() {
    final org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp timestamp =
            new org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp();
    timestamp.setModifyTime(new org.apache.commons.compress.archivers.zip.ZipLong(0x04030201L));
    timestamp.setAccessTime(new org.apache.commons.compress.archivers.zip.ZipLong(0x08070605L));
    timestamp.setCreateTime(new org.apache.commons.compress.archivers.zip.ZipLong(0x0c0b0a09L));

    org.junit.Assert.assertEquals(13, timestamp.getLocalFileDataLength().getValue());
    org.junit.Assert.assertEquals(5, timestamp.getCentralDirectoryLength().getValue());
    org.junit.Assert.assertArrayEquals(
            new byte[] { 7, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
            timestamp.getLocalFileDataData());
    org.junit.Assert.assertArrayEquals(
            new byte[] { 1, 1, 2, 3, 4 },
            timestamp.getCentralDirectoryData());
}

@org.junit.Test
public void cloneCreatesDistinctEqualTimestamp() throws CloneNotSupportedException {
    final org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp timestamp =
            new org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp();
    timestamp.setModifyTime(new org.apache.commons.compress.archivers.zip.ZipLong(1234L));
    timestamp.setAccessTime(new org.apache.commons.compress.archivers.zip.ZipLong(5678L));

    final org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp copy =
            (org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp) timestamp.clone();

    org.junit.Assert.assertNotSame(timestamp, copy);
    org.junit.Assert.assertEquals(timestamp, copy);
    org.junit.Assert.assertEquals(copy, timestamp);
}

@org.junit.Test
public void equalityIgnoresNonTimestampFlagBitsAndRejectsOtherTypes() {
    final org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp withReservedBit =
            new org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp();
    final org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp withoutReservedBit =
            new org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp();

    withReservedBit.setFlags((byte) 0x81);
    withoutReservedBit.setFlags(
            org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp.MODIFY_TIME_BIT);

    org.junit.Assert.assertEquals(withReservedBit, withoutReservedBit);
    org.junit.Assert.assertFalse(withReservedBit.equals("not a timestamp"));
    org.junit.Assert.assertFalse(withReservedBit.equals(null));
}