package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TarArchiveInputStreamTest {

 private static final int HEADER_SIZE = 512;
 private static final int UID_OFFSET = 108;
 private static final int GID_OFFSET = 116;
 private static final int SIZE_OFFSET = 124;
 private static final int CHKSUM_OFFSET = 148;
 private static final int TYPEFLAG_OFFSET = 156;
 private static final int MAGIC_OFFSET = 257;
 private static final int VERION_OFFSET = 263;

 @Test
 public void testNormalUidGid() throws Exception {
     byte[] tar = createSimpleTar("test.txt", 1000L, 1000L, 0L);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("uid", 1000L, e.getLongUserId());
     assertEquals("gid", 1000L, e.getLongGroupId());
 }

 @Test
 public void testLargeGidPax() throws Exception {
     byte[] tar = createPaxTar("test.txt", "4294967294", null, null);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("gid", 4294967294L, e.getLongGroupId());
 }

 @Test
 public void testUidBeyondIntegerMax() throws Exception {
     long largeUid = Integer.MAX_VALUE + 1L;
     byte[] tar = createPaxTar("test.txt", null, String.valueOf(largeUid), null);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("uid", largeUid, e.getLongUserId());
 }

 @Test
 public void testZeroUidGid() throws Exception {
     byte[] tar = createSimpleTar("test.txt", 0L, 0L, 0L);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("uid", 0L, e.getLongUserId());
     assertEquals("gid", 0L, e.getLongGroupId());
 }

 @Test
 public void testMaxOctalUid() throws Exception {
     long maxOctal = 02000000L - 1; // max 7 octal digits = 2097151
     byte[] tar = createSimpleTar("test.txt", maxOctal, maxOctal, 0L);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("uid", maxOctal, e.getLongUserId());
     assertEquals("gid", maxOctal, e.getLongGroupId());
 }

 @Test
 public void testEmptyNumericFieldDefaultsToZero() throws Exception {
     byte[] tar = new byte[HEADER_SIZE];
     // craft a valid header but with uid field all spaces
     fillName(tar, "test.txt");
     fillOctal(tar,100,8,0644);
     // uid all spaces -> should parse as 0
     for(int i=0;i<8;i++) tar[UID_OFFSET+i] = ' ';
     fillOctal(tar,116,8,0); // normal gid
     fillOctal(tar,124,12,0);
     fillOctal(tar,136,12,System.currentTimeMillis()/1000);
     tar[TYPEFLAG_OFFSET] = '0';
     fillMagic(tar);
     fillVersion(tar);
     computeCheckum(tar);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("uid defaults to 0", 0L, e.getLongUserId());
     assertEquals("gid", 0L, e.getLongGroupId());
 }

 @Test
 public void testBothLargePax() throws Exception {
     long largeGid = 4294967294L;
     long largeUid = (long)Integer.MAX_VALUE + 2;
     byte[] tar = createPaxTar("test.txt", String.valueOf(largeGid), String.valueOf(largeUid),
null);
     TarArchiveEntry e = readSingleEntry(tar);
     assertEquals("gid", largeGid, e.getLongGroupId());
     assertEquals("uid", largeUid, e.getLongUserId());
 }

 private TarArchiveEntry readSingleEntry(byte[] tar) throws IOException {
     TarArchiveInputStream tin = new TarArchiveInputStream(new ByteArrayInputStream(tar));
     TarArchiveEntry e = tin.getNextTarEntry();
     assertNotNull("entry is null", e);
     tin.close();
     return e;
 }

 private byte[] createSimpleTar(String name, long uid, long gid, long size) throws IOException {
     byte[] header = new byte[HEADER_SIZE];
     fillName(header, name);
     fillOctal(header,100,8,0644); // mode
     fillOctal(header,108,8,uid);
     fillOctal(header,116,8,gid);
     fillOctal(header,124,12,size);
     fillOctal(header,136,12,System.currentTimeMillis()/1000);
     header[TYPEFLAG_OFFSET] = '0';
     fillMagic(header);
     fillVersion(header);
     computeCheckum(header);
     // no data for size==0
     return header;
 }

 private byte[] createPaxTar(String fileName, String gid, String uid, String size) throws
IOException {
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     StringBuilder sb = new StringBuilder();
     if(gid != null) sb.append(generatePaxLine("gid", gid));
     if(uid != null) sb.append(generatePaxLine("uid", uid));
     if(size != null) sb.append(generatePaxLine("size", size));
     byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
     // pax header entry with type 'x'
     byte[] paxHeader = createSimpleTar("PaxHeaders.X/" + fileName,0,0, body.length);
     paxHeader[TYPEFLAG_OFFSET] = 'x'; // set type to extended
     computeCheckum(paxHeader);
     bos.write(paxHeader);
     bos.write(body);
     // pad body to 512
     int pad = (HEADER_SIZE - (body.length % HEADER_SIZE)) % HEADER_SIZE;
     if(pad >0) bos.write(new byte[pad]);
     // actual file entry
     byte[] fileHeader = createSimpleTar(fileName,0,0,0);
     bos.write(fileHeader);
     // pad final record if needed (don't need here)
     return bos.toByteArray();
 }

 private String generatePaxLine(String key, String value) {
     String suffix = " " + key + "=" + value + "\n";
     int len = suffix.length();
     while(true) {
         String prefix = String.valueOf(len);
         int newLen = prefix.length() + suffix.length();
         if(newLen == len) return len + suffix;
         len = newLen;
     }
 }

 private void fillName(byte[] header, String name) {
     byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
     int len = Math.min(nameBytes.length, 100);
     System.arraycopy(nameBytes,0,header,0,len);
 }

 private void fillOctal(byte[] header, int offset, int length, long value) {
     String format = "%0" + (length-1) + "o\0";
     String s = String.format(format, value);
     byte[] b = s.getBytes(StandardCharsets.UTF_8);
     System.arraycopy(b,0,header,offset, length);
 }

 private void fillMagic(byte[] header) {
     byte[] magic = "ustar\0".getBytes(StandardCharsets.UTF_8);
     System.arraycopy(magic,0,header,MAGIC_OFFSET,6);
 }

 private void fillVersion(byte[] header) {
     header[VERSION_OFFSET] = '0';
     header[VERSION_OFFSET+1] = '0';
 }

 private void computeCheckum(byte[] header) {
     // set checksum field to spaces
     for(int i=CHKSUM_OFFSET;i<CHKSUM_OFFSET+8;i++) header[i] = ' ';
     int sum =0;
     for(int i=0;i<HEADER_SIZE;i++) sum += header[i] & 0xFF;
     String s = String.format("%06o\0 ", sum);
     byte[] c = s.getBytes(StandardCharsets.UTF_8);
     System.arraycopy(c,0,header,CHKSUM_OFFSET,8);
 }

}