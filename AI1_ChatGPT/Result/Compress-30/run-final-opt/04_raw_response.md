    @org.junit.Test
    public void truncatedStreamIsRejectedDuringInitialization() {
        try {
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'B', 'Z', 'h', '9' }));
            org.junit.Assert.fail("Expected truncated stream to be rejected");
        } catch (final java.io.IOException expected) {
            // expected
        }
    }

    @org.junit.Test
    public void nonBzip2HeaderIsRejected() {
        try {
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'n', 'o', 'p', 'e' }));
            org.junit.Assert.fail("Expected invalid header to be rejected");
        } catch (final java.io.IOException expected) {
            // expected
        }
    }

    @org.junit.Test
    public void concatenatedModeValidatesFollowingStream() throws java.io.IOException {
        final byte[] firstStream = emptyBZip2Stream();
        final byte[] data = new byte[firstStream.length + 4];
        System.arraycopy(firstStream, 0, data, 0, firstStream.length);
        data[firstStream.length] = 'B';
        data[firstStream.length + 1] = 'Z';
        data[firstStream.length + 2] = 'h';
        data[firstStream.length + 3] = '9';

        final org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream singleStream =
                new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                        new java.io.ByteArrayInputStream(data), false);
        org.junit.Assert.assertEquals(-1, singleStream.read());

        try {
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                    new java.io.ByteArrayInputStream(data), true);
            org.junit.Assert.fail("Expected malformed concatenated stream to be rejected");
        } catch (final java.io.IOException expected) {
            // expected
        }
    }

    @org.junit.Test
    public void matchesRequiresCompleteBzip2Signature() {
        final byte[] signature = new byte[] { 'B', 'Z', 'h' };

        org.junit.Assert.assertTrue(
                org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream.matches(signature, 3));
        org.junit.Assert.assertFalse(
                org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream.matches(signature, 2));
    }

    private byte[] emptyBZip2Stream() {
        return new byte[] {
            'B', 'Z', 'h', '9',
            0x17, 0x72, 0x45, 0x38, 0x50, (byte) 0x90,
            0x00, 0x00, 0x00, 0x00
        };
    }