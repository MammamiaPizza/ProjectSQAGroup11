package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.commons.compress.utils.TarUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**

 - Tests targeting the COMPRESS-356 bug: parsing a PAX header with a name ending
 - in slash should not throw IOException, and isDirectory() must return true.
  */
 public class Bug38Test {
  // Tar typeflag constants from TarConstants (implied by TarArchiveEntry)
  private static final byte LF_DIR         = '5';
  private static final byte LF_NORMAL      = '0';
  private static final byte LF_PAX_EXTENDED = 'x';
  // Offsets and field lengths used in a POSIX/USTAR header block
  private static final int HEADER_SIZE   = 512;
  private static final int NAME_LEN      = 100;
  private static final int CHKSUM_OFFSET = 148;
  private static final int CHKSUM_LEN    = 8;
  private static final int LINKFLAG_OFF  = 156;
  private static final int MAGIC_OFF     = 257;
  private static final int MAGIC_LEN     = 6;
  private static final int VERSION_OFF   = 263;
  /**
  - Creates a minimal, checksum-valid tar header with the given name and
  - link flag.  Magic is set to "ustar\0" and version to "00".  All numeric
  - fields are left as zero (valid octal) so they do not interfere.
    */
   private byte[] makeHeader(String name, byte linkFlag) {
   byte[] buf = new byte[HEADER_SIZE];
   // Encode the name into the first NAME_LEN bytes
   byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
   System.arraycopy(nameBytes, 0, buf, 0, Math.min(nameBytes.length, NAME_LEN));
   // Write the link flag at its fixed offset
   buf[LINKFLAG_OFF] = linkFlag;
   // Set POSIX magic (ustar\0) and version (00)
   byte[] magic = "ustar\0".getBytes(StandardCharsets.UTF_8);
   System.arraycopy(magic, 0, buf, MAGIC_OFF, MAGIC_LEN);
   buf[VERSION_OFF]   = '0';
   buf[VERSION_OFF+1] = '0';
   // Fill checksum field with spaces, compute checksum, then format it back
   for (int i = CHKSUM_OFFSET; i < CHKSUM_OFFSET + CHKSUM_LEN; i++) {
   buf[i] = ' ';
   }
   long sum = TarUtils.computeCheckSum(buf);
   TarUtils.formatCheckSumOctalBytes(sum, buf, CHKSUM_OFFSET, CHKSUM_LEN);
   return buf;
  }
  /**
  - Round-trip: write and immediately parse a header, ensuring no exception.
    */
   private TarArchiveEntry roundTrip(String name, byte linkFlag) throws IOException {
   byte[] header = makeHeader(name, linkFlag);
   return new TarArchiveEntry(header);
   }
  // -----------------------------------------------------------------
  //
  1. Name ending with '/' + LF_DIR flag  -> no IOException, isDirectory true
  // -----------------------------------------------------------------
  @Test
  public void parseHeaderTrailingSlashWithDirFlag() throws Exception {
      TarArchiveEntry e = roundTrip("mydir/", LF_DIR);
      assertNotNull(e);
      assertTrue("Name must end with '/'", e.getName().endsWith("/"));
      assertTrue("isDirectory must be true", e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  2. Name ending with '/' + LF_NORMAL flag -> no IOException,
  //    isDirectory() still returns true because name ends with '/'
  // -----------------------------------------------------------------
  @Test
  public void parseHeaderTrailingSlashWithNormalFlag() throws Exception {
      TarArchiveEntry e = roundTrip("weird/", LF_NORMAL);
      assertNotNull(e);
      assertTrue(e.getName().endsWith("/"));
      assertTrue("isDirectory must be true for name ending '/'", e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  3. Name ending with '/' + PAX header flag -> no IOException,
  //    isPaxHeader is true, isDirectory is true
  // -----------------------------------------------------------------
  @Test
  public void parseHeaderTrailingSlashWithPaxFlag() throws Exception {
      TarArchiveEntry e = roundTrip("paxdir/", LF_PAX_EXTENDED);
      assertNotNull(e);
      assertTrue(e.isPaxHeader());
      assertTrue(e.getName().endsWith("/"));
      assertTrue("isDirectory must be true", e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  4. Normal file (no trailing slash) -> isDirectory false, isFile true
  // -----------------------------------------------------------------
  @Test
  public void parseHeaderNormalFile() throws Exception {
      TarArchiveEntry e = roundTrip("readme.txt", LF_NORMAL);
      assertNotNull(e);
      assertEquals("readme.txt", e.getName());
      assertFalse(e.isDirectory());
      assertTrue(e.isFile());
  }
  // -----------------------------------------------------------------
  //
  5. Root directory (name = "/") -> isDirectory true
  // -----------------------------------------------------------------
  @Test
  public void parseHeaderRootDirectory() throws Exception {
      TarArchiveEntry e = roundTrip("/", LF_DIR);
      assertNotNull(e);
      assertEquals("/", e.getName());
      assertTrue(e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  6. Invalid header should throw IOException
  // -----------------------------------------------------------------
  @Test(expected = IOException.class)
  public void parseHeaderInvalidBytesThrowsIOException() throws Exception {
      byte[] garbage = new byte[512]; // all zeros, no checksum/magic
      new TarArchiveEntry(garbage);
  }
  // -----------------------------------------------------------------
  //
  7. String-constructor: trailing slash yields isDirectory true
  // -----------------------------------------------------------------
  @Test
  public void stringConstructorTrailingSlashIsDirectory() {
      TarArchiveEntry e = new TarArchiveEntry("folder/");
      assertTrue(e.getName().endsWith("/"));
      assertTrue(e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  8. String-constructor: normal name yields isDirectory false
  // -----------------------------------------------------------------
  @Test
  public void stringConstructorNormalFileIsNotDirectory() {
      TarArchiveEntry e = new TarArchiveEntry("note.txt");
      assertFalse(e.getName().endsWith("/"));
      assertFalse(e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  9. String-constructor with explicit linkFlag preserves slash behaviour
  // -----------------------------------------------------------------
  @Test
  public void stringConstructorWithLinkFlagAndTrailingSlash() {
      TarArchiveEntry e = new TarArchiveEntry("data/", LF_NORMAL);
      assertTrue(e.getName().endsWith("/"));
      assertTrue("isDirectory must be true due to name ending '/'", e.isDirectory());
  }
  // -----------------------------------------------------------------
  //
  10. Preserve-leading-slashes constructor with absolute path
  // -----------------------------------------------------------------
  @Test
  public void stringConstructorPreserveLeadingSlashes() {
      TarArchiveEntry e = new TarArchiveEntry("/usr/bin/", true);
      assertTrue(e.getName().endsWith("/"));
      assertTrue(e.isDirectory());
      // Leading slash must be kept
      assertTrue(e.getName().startsWith("/"));
  }

}
