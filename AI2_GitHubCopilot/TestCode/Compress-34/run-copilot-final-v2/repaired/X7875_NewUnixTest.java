package org.apache.commons.compress.archivers.zip;

 import static org.junit.Assert.assertArrayEquals;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;

 import org.junit.Test;

 public class X7875_NewUnixTest {

     private X7875_NewUnix localRoundTrip(long uid, long gid) throws Exception {
         X7875_NewUnix source = new X7875_NewUnix();
         source.setUID(uid);
         source.setGID(gid);
         byte[] data = source.getLocalFileDataData();
         X7875_NewUnix parsed = new X7875_NewUnix();
         parsed.parseFromLocalFileData(data, 0, data.length);
         return parsed;
     }

     private void assertLocalRoundTrip(long uid, long gid) throws Exception {
         X7875_NewUnix parsed = localRoundTrip(uid, gid);
         assertEquals("UID", uid, parsed.getUID());
         assertEquals("GID", gid, parsed.getGID());
     }

     @Test
     public void testDefaultValues() throws Exception {
         X7875_NewUnix xf = new X7875_NewUnix();
         assertEquals(1000L, xf.getUID());
         assertEquals(1000L, xf.getGID());
         assertEquals(0x7875, xf.getHeaderId().getValue());
     }

     @Test
     public void testTrimLeadingZeroesForceMinLength() {
         assertArrayEquals(new byte[] { 0 },
                 X7875_NewUnix.trimLeadingZeroesForceMinLength(new byte[] { 0 }));
         assertArrayEquals(new byte[] { 0 },
                 X7875_NewUnix.trimLeadingZeroesForceMinLength(new byte[] { 0, 0, 0 }));
         assertArrayEquals(new byte[] { 1 },
                 X7875_NewUnix.trimLeadingZeroesForceMinLength(new byte[] { 0, 1 }));
         assertArrayEquals(new byte[] { 1, 0 },
                 X7875_NewUnix.trimLeadingZeroesForceMinLength(new byte[] { 0, 1, 0 }));
         assertArrayEquals(new byte[] { 1, 0 },
                 X7875_NewUnix.trimLeadingZeroesForceMinLength(new byte[] { 1, 0 }));
     }

     @Test
     public void testParseReparse() throws Exception {
         X7875_NewUnix source = new X7875_NewUnix();
         source.setUID(0L);
         source.setGID(0L);
         byte[] data = source.getLocalFileDataData();
         X7875_NewUnix parsed = new X7875_NewUnix();
         parsed.parseFromLocalFileData(data, 0, data.length);
         assertEquals(0L, parsed.getUID());
         assertEquals(0L, parsed.getGID());
     }

     @Test
     public void testParseReparseRepresentativeValues() throws Exception {
         long[][] pairs = {
                 { 0L, 65535L },
                 { 1L, 2L },
                 { 127L, 128L },
                 { 255L, 256L },
                 { 65535L, 65536L },
                 { 8388607L, 8388608L },
                 { 16777215L, 16777216L }
         };
         for (long[] pair : pairs) {
             assertLocalRoundTrip(pair[0], pair[1]);
         }
     }

     @Test
     public void testParseReparseLongMax() throws Exception {
         assertLocalRoundTrip(Long.MAX_VALUE, 0L);
         assertLocalRoundTrip(Long.MAX_VALUE, Long.MAX_VALUE - 1);
     }

     @Test
     public void testLocalFileDataLengthMatchesGeneratedData() throws Exception {
         long[] values = {
                 0L, 1L, 127L, 128L, 255L, 256L,
                 65535L, 65536L, Integer.MAX_VALUE, 1L << 40, Long.MAX_VALUE
         };
         for (long value : values) {
             X7875_NewUnix xf = new X7875_NewUnix();
             xf.setUID(value);
             xf.setGID(value == 0 ? 0L : value - 1);
             assertEquals("local data length for " + value,
                     xf.getLocalFileDataLength().getValue(),
                     xf.getLocalFileDataData().length);
         }
     }

     @Test
     public void testCentralDirectoryLengthSelfConsistent() throws Exception {
         long[] values = { 0L, 1L, 1000L, Long.MAX_VALUE };
         for (long value : values) {
             X7875_NewUnix xf = new X7875_NewUnix();
             xf.setUID(value);
             xf.setGID(value);
             assertEquals("central length for " + value,
                     xf.getCentralDirectoryLength().getValue(),
                     xf.getCentralDirectoryData().length);
         }
     }

     @Test
     public void testCentralDirectoryLengthMatchesLocal() throws Exception {
         long[] values = { 0L, 1L, 1000L, Long.MAX_VALUE };
         for (long value : values) {
             X7875_NewUnix xf = new X7875_NewUnix();
             xf.setUID(value);
             xf.setGID(value);
             assertEquals("central length for " + value,
                     xf.getLocalFileDataLength().getValue(),
                     xf.getCentralDirectoryLength().getValue());
         }
     }

     @Test
     public void testCentralDirectoryDataSelfConsistent() throws Exception {
         long[][] pairs = {
                 { 0L, 0L },
                 { 123456L, 789L },
                 { Long.MAX_VALUE, 1L }
         };
         for (long[] pair : pairs) {
             X7875_NewUnix source = new X7875_NewUnix();
             source.setUID(pair[0]);
             source.setGID(pair[1]);
             byte[] central = source.getCentralDirectoryData();
             X7875_NewUnix parsed = new X7875_NewUnix();
             parsed.parseFromCentralDirectoryData(central, 0, central.length);
             assertEquals("central UID roundtrip", pair[0], parsed.getUID());
             assertEquals("central GID roundtrip", pair[1], parsed.getGID());
         }
     }

     @Test
     public void testCentralDirectoryDataMatchesLocal() throws Exception {
         X7875_NewUnix xf = new X7875_NewUnix();
         xf.setUID(123456789L);
         xf.setGID(987654321L);
         assertArrayEquals(xf.getLocalFileDataData(), xf.getCentralDirectoryData());
     }

     @Test
     public void testCentralDirectoryRoundTrip() throws Exception {
         long[][] pairs = {
                 { 0L, 0L },
                 { 123456L, 789L },
                 { Long.MAX_VALUE, 1L }
         };
         for (long[] pair : pairs) {
             X7875_NewUnix source = new X7875_NewUnix();
             source.setUID(pair[0]);
             source.setGID(pair[1]);
             byte[] central = source.getCentralDirectoryData();
             X7875_NewUnix parsed = new X7875_NewUnix();
             parsed.parseFromCentralDirectoryData(central, 0, central.length);
             assertEquals("central UID", pair[0], parsed.getUID());
             assertEquals("central GID", pair[1], parsed.getGID());
         }
     }

     @Test
     public void testClonePreservesState() throws Exception {
         X7875_NewUnix source = new X7875_NewUnix();
         source.setUID(4242L);
         source.setGID(2424L);
         X7875_NewUnix clone = (X7875_NewUnix) source.clone();
         assertEquals(source.getUID(), clone.getUID());
         assertEquals(source.getGID(), clone.getGID());
         assertEquals(source, clone);
         assertEquals(source.hashCode(), clone.hashCode());
     }

     @Test
     public void testEqualsAndHashCodeAreValueBased() {
         X7875_NewUnix a = new X7875_NewUnix();
         X7875_NewUnix b = new X7875_NewUnix();
         assertEquals(a, b);
         assertEquals(a.hashCode(), b.hashCode());

         a.setUID(13L);
         assertFalse(a.equals(b));
         b.setUID(13L);
         assertEquals(a, b);
         b.setGID(17L);
         assertFalse(a.equals(b));
     }

 }
