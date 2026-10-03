@org.junit.Test
public void cloneIsEqualButIndependentAndHasMatchingHashCode() throws CloneNotSupportedException {
    X7875_NewUnix original = new X7875_NewUnix();
    original.setUID(1234L);
    original.setGID(5678L);

    X7875_NewUnix copy = (X7875_NewUnix) original.clone();

    org.junit.Assert.assertNotSame(original, copy);
    org.junit.Assert.assertEquals(original, copy);
    org.junit.Assert.assertEquals(original.hashCode(), copy.hashCode());
    org.junit.Assert.assertFalse(original.equals("not an extra field"));

    copy.setUID(9999L);
    org.junit.Assert.assertEquals(1234L, original.getUID());
    org.junit.Assert.assertEquals(9999L, copy.getUID());
}

@org.junit.Test
public void exposesHeaderAndCentralDirectoryRepresentation() {
    X7875_NewUnix field = new X7875_NewUnix();
    field.setUID(12L);
    field.setGID(34L);

    org.junit.Assert.assertEquals(
            new org.apache.commons.compress.archivers.zip.ZipShort(0x7875),
            field.getHeaderId());
    org.junit.Assert.assertEquals(field.getLocalFileDataLength(), field.getCentralDirectoryLength());
    org.junit.Assert.assertArrayEquals(new byte[0], field.getCentralDirectoryData());
    org.junit.Assert.assertEquals("0x7875 Zip Extra Field: UID=12 GID=34", field.toString());
}

@org.junit.Test
public void trimLeadingZeroesAcceptsNullInput() {
    org.junit.Assert.assertNull(X7875_NewUnix.trimLeadingZeroesForceMinLength(null));
}