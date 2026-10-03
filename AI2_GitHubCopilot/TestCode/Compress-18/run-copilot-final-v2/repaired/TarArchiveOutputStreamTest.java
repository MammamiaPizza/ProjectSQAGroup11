package org.apache.commons.compress.archivers.tar;

 import static org.junit.Assert.*;

 import java.io.ByteArrayOutputStream;
 import java.io.IOException;

 import org.junit.Test;

 public class TarArchiveOutputStreamTest {

  private TarArchiveOutputStream newStream(ByteArrayOutputStream baos) {
      return new TarArchiveOutputStream(baos, "UTF-8");
  }

  private void writeEntry(TarArchiveOutputStream tos, TarArchiveEntry entry,
          byte[] content) throws IOException {
      tos.putArchiveEntry(entry);
      if (content != null && content.length > 0) {
          tos.write(content);
      }
      tos.closeArchiveEntry();
  }

  // The trigger test from the bug report.
  // Writing a directory with a non-ASCII name in POSIX mode must not throw
  // an IOException for the PaxHeaders entry.
  @Test
  public void testWriteNonAsciiDirectoryNamePosixMode() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("f\u00f6\u00f6/");
      tos.putArchiveEntry(entry);
      tos.closeArchiveEntry();
      tos.finish();
      tos.close();

      assertTrue("archive must contain at least the tar end-of-file records",
              baos.size() >= 2 * tos.getRecordSize());
  }

  // Pax-headers explicitly enabled - same code path as the trigger.
  @Test
  public void testNonAsciiDirectoryWithPaxHeadersEnabled() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("caf\u00e9/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // ASCII directory name must still work normally.
  @Test
  public void testAsciiDirectoryNamePosixMode() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("simple/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // Non-ASCII file *with* content exercises the pax header + data write path.
  @Test
  public void testNonAsciiFileNameWithContent() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("\u03b1\u03b2\u03b3.txt");
      byte[] payload = "hello world".getBytes("UTF-8");
      writeEntry(tos, entry, payload);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // A nested path with multi-byte UTF-8 code points in directory components.
  @Test
  public void testNestedNonAsciiDirectoryPath() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("a/\u00fc\u00f6\u00e4/b/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // Multiple non-ASCII entries in the same archive - the pax header size bug
  // must be fixed for each entry separately.
  @Test
  public void testMultipleNonAsciiEntries() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry dir = new TarArchiveEntry("\u3042\u3044/");
      writeEntry(tos, dir, null);

      TarArchiveEntry file = new TarArchiveEntry(
              "\u3046\u3048\u304a.txt");
      writeEntry(tos, file, "data".getBytes("UTF-8"));

      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // POSIX mode without PAX headers - the name may be silently stripped or
  // truncated but must not throw the off-by-zero PaxHeaders IOException.
  @Test
  public void testNonAsciiDirectoryNameWithoutPaxHeaders() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(false);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("na\u00efve/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // LONGFILE_GNU mode must also handle non-ASCII directory names.
  @Test
  public void testNonAsciiDirectoryNameLongFileGnu() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

      TarArchiveEntry entry = new TarArchiveEntry("gnu\u00e7\u00e8/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // LONGFILE_TRUNCATE must not crash on non-ASCII names.
  @Test
  public void testNonAsciiDirectoryNameLongFileTruncate() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_TRUNCATE);

      TarArchiveEntry entry = new TarArchiveEntry("trunc\u00e9\u00ea/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // Single multi-byte character as a directory name - boundary case.
  @Test
  public void testSingleMultiByteCharDirectoryName() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry entry = new TarArchiveEntry("\u20ac/");
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // Byte-length vs char-length: 3-byte-per-char Japanese characters.
  // The bug was about the header size being 0 instead of the *byte* size
  // of the pax content.
  @Test
  public void testMultiByteUtf8CharsByteLenBoundary() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      // Each char is 3 bytes in UTF-8
      String name = "\u65e5\u672c\u8a9e\u30c6\u30ad\u30b9\u30c8/";
      TarArchiveEntry entry = new TarArchiveEntry(name);
      writeEntry(tos, entry, null);
      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

  // Directory with non-ASCII name followed by a normal file - both must succeed.
  @Test
  public void testNonAsciiDirThenAsciiFile() throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      TarArchiveOutputStream tos = newStream(baos);
      tos.setAddPaxHeadersForNonAsciiNames(true);
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

      TarArchiveEntry dir = new TarArchiveEntry("d\u00e9j\u00e0/");
      writeEntry(tos, dir, null);

      TarArchiveEntry file = new TarArchiveEntry("d\u00e9j\u00e0/readme.txt");
      writeEntry(tos, file, "content".getBytes("UTF-8"));

      tos.finish();
      tos.close();

      assertTrue(baos.size() > 0);
  }

 }
