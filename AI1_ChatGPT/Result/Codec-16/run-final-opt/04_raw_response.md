@org.junit.Test
public void lineLengthConstructorEncodesChunksUsingDefaultSeparator() {
    final org.apache.commons.codec.binary.Base32 codec = new org.apache.commons.codec.binary.Base32(8);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'N', 'B', 'S', 'W', 'Y', '3', 'D', 'P', '\r', '\n' },
            codec.encode(new byte[] { 'h', 'e', 'l', 'l', 'o' }));
}

@org.junit.Test
public void customLineSeparatorIsUsedWhenEncodingChunks() {
    final org.apache.commons.codec.binary.Base32 codec =
            new org.apache.commons.codec.binary.Base32(8, new byte[] { '!' });

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'N', 'B', 'S', 'W', 'Y', '3', 'D', 'P', '!' },
            codec.encode(new byte[] { 'h', 'e', 'l', 'l', 'o' }));
}

@org.junit.Test
public void base32HexConstructorEncodesAndDecodesUsingHexAlphabet() {
    final org.apache.commons.codec.binary.Base32 codec =
            new org.apache.commons.codec.binary.Base32(0, null, true);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'C', 'P', 'N', 'M', 'U', '=', '=', '=' },
            codec.encode(new byte[] { 'f', 'o', 'o' }));
    org.junit.Assert.assertArrayEquals(
            new byte[] { 'f', 'o', 'o' },
            codec.decode("CPNMU==="));
}

@org.junit.Test
public void customPadIsUsedForEncodingAndDecoding() {
    final org.apache.commons.codec.binary.Base32 codec =
            new org.apache.commons.codec.binary.Base32((byte) '!');

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'M', 'Z', 'X', 'W', '6', '!', '!', '!' },
            codec.encode(new byte[] { 'f', 'o', 'o' }));
    org.junit.Assert.assertArrayEquals(
            new byte[] { 'f', 'o', 'o' },
            codec.decode("MZXW6!!!"));
}