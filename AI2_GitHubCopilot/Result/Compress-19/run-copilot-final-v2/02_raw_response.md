package org.apache.commons.compress.archivers.zip;

 import static org.junit.Assert.*;
 import static org.apache.commons.compress.archivers.zip.ZipConstants.DWORD;
 import static org.apache.commons.compress.archivers.zip.ZipConstants.WORD;

 import java.util.zip.ZipException;
 import org.junit.Test;

 /**
  * Tests for {@link Zip64ExtendedInformationExtraField} that target the
  * bug COMPRESS-228 where excess bytes in a central directory zip64 extra
  * field are not tolerated.
  */
 public class Zip64ExtendedInformationExtraFieldTest {

     private static final ZipEightByteInteger NULL_SIZE = null;
     private static final ZipEightByteInteger SIZE_VAL = new ZipEightByteInteger(0xFFFFFFFFL);
     private static final ZipEightByteInteger COMP_VAL = new ZipEightByteInteger(0xFFFFFF00L);
     private static final ZipEightByteInteger OFFSET_VAL = new ZipEightByteInteger(0x100000000L);
     private static final ZipLong DISK_VAL = new ZipLong(0xABCDEF12L);

     // -----------------------------------------------------------------------
     // getHeaderId
     // -----------------------------------------------------------------------
     @Test
     public void testGetHeaderId() {
         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         assertEquals(new ZipShort(0x0001), field.getHeaderId());
     }

     // -----------------------------------------------------------------------
     // Default constructor – empty state
     // -----------------------------------------------------------------------
     @Test
     public void testDefaultConstructorEmptyFields() {
         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         assertNull(field.getSize());
         assertNull(field.getCompressedSize());
         assertNull(field.getRelativeHeaderOffset());
         assertNull(field.getDiskStartNumber());

         assertEquals(0, field.getLocalFileDataLength().getValue());
         assertEquals(0, field.getCentralDirectoryLength().getValue());

         assertArrayEquals(new byte[0], field.getLocalFileDataData());
         assertArrayEquals(new byte[0], field.getCentralDirectoryData());
     }

     // -----------------------------------------------------------------------
     // Constructor with sizes only
     // -----------------------------------------------------------------------
     @Test
     public void testConstructorWithSizesOnlyRoundTrip() throws Exception {
         Zip64ExtendedInformationExtraField field =
             new Zip64ExtendedInformationExtraField(SIZE_VAL, COMP_VAL);

         assertEquals(SIZE_VAL, field.getSize());
         assertEquals(COMP_VAL, field.getCompressedSize());
         assertNull(field.getRelativeHeaderOffset());
         assertNull(field.getDiskStartNumber());

         assertEquals(2 * DWORD, field.getLocalFileDataLength().getValue());
         assertEquals(2 * DWORD, field.getCentralDirectoryLength().getValue());

         // local file data round-trip
         byte[] localData = field.getLocalFileDataData();
         assertEquals(2 * DWORD, localData.length);

         Zip64ExtendedInformationExtraField parsed = new Zip64ExtendedInformationExtraField();
         parsed.parseFromLocalFileData(localData, 0, localData.length);
         assertEquals(SIZE_VAL, parsed.getSize());
         assertEquals(COMP_VAL, parsed.getCompressedSize());
         assertNull(parsed.getRelativeHeaderOffset());
         assertNull(parsed.getDiskStartNumber());

         // central directory data round-trip
         byte[] centralData = field.getCentralDirectoryData();
         assertEquals(2 * DWORD, centralData.length);

         Zip64ExtendedInformationExtraField centralParsed = new
Zip64ExtendedInformationExtraField();
         centralParsed.parseFromCentralDirectoryData(centralData, 0, centralData.length);
         assertEquals(SIZE_VAL, centralParsed.getSize());
         assertEquals(COMP_VAL, centralParsed.getCompressedSize());
         assertNull(centralParsed.getRelativeHeaderOffset());
         assertNull(centralParsed.getDiskStartNumber());
     }

     // -----------------------------------------------------------------------
     // Constructor with all four fields
     // -----------------------------------------------------------------------
     @Test
     public void testConstructorWithAllFieldsRoundTrip() throws Exception {
         Zip64ExtendedInformationExtraField field =
             new Zip64ExtendedInformationExtraField(SIZE_VAL, COMP_VAL, OFFSET_VAL, DISK_VAL);

         assertEquals(SIZE_VAL, field.getSize());
         assertEquals(COMP_VAL, field.getCompressedSize());
         assertEquals(OFFSET_VAL, field.getRelativeHeaderOffset());
         assertEquals(DISK_VAL, field.getDiskStartNumber());

         assertEquals(2 * DWORD, field.getLocalFileDataLength().getValue());
         assertEquals(3 * DWORD + WORD, field.getCentralDirectoryLength().getValue()); // 28

         // local file data round-trip – offset/diskNr are NOT in local output
         byte[] localData = field.getLocalFileDataData();
         assertEquals(2 * DWORD, localData.length);

         Zip64ExtendedInformationExtraField parsedLocal = new Zip64ExtendedInformationExtraField();
         parsedLocal.parseFromLocalFileData(localData, 0, localData.length);
         assertEquals(SIZE_VAL, parsedLocal.getSize());
         assertEquals(COMP_VAL, parsedLocal.getCompressedSize());
         assertNull(parsedLocal.getRelativeHeaderOffset());
         assertNull(parsedLocal.getDiskStartNumber());

         // central directory data round-trip
         byte[] centralData = field.getCentralDirectoryData();
         assertEquals(3 * DWORD + WORD, centralData.length);

         Zip64ExtendedInformationExtraField centralParsed = new
Zip64ExtendedInformationExtraField();
         centralParsed.parseFromCentralDirectoryData(centralData, 0, centralData.length);
         assertEquals(SIZE_VAL, centralParsed.getSize());
         assertEquals(COMP_VAL, centralParsed.getCompressedSize());
         assertEquals(OFFSET_VAL, centralParsed.getRelativeHeaderOffset());
         assertEquals(DISK_VAL, centralParsed.getDiskStartNumber());
     }

     // -----------------------------------------------------------------------
     // Setters – length contributions and serialised output
     // -----------------------------------------------------------------------
     @Test
     public void testSetSizesOnlyAndLengthProperties() throws Exception {
         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.setSize(SIZE_VAL);
         field.setCompressedSize(COMP_VAL);

         assertEquals(2 * DWORD, field.getLocalFileDataLength().getValue());
         assertEquals(2 * DWORD, field.getCentralDirectoryLength().getValue());

         byte[] central = field.getCentralDirectoryData();
         assertEquals(2 * DWORD, central.length);
     }

     @Test
     public void testSetAllFieldsAndLengthProperties() throws Exception {
         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.setSize(SIZE_VAL);
         field.setCompressedSize(COMP_VAL);
         field.setRelativeHeaderOffset(OFFSET_VAL);
         field.setDiskStartNumber(DISK_VAL);

         assertEquals(2 * DWORD, field.getLocalFileDataLength().getValue());
         assertEquals(3 * DWORD + WORD, field.getCentralDirectoryLength().getValue());

         byte[] central = field.getCentralDirectoryData();
         assertEquals(3 * DWORD + WORD, central.length);
     }

     // -----------------------------------------------------------------------
     // parseFromLocalFileData
     // -----------------------------------------------------------------------
     @Test
     public void testParseLocalFileDataWithOnlySizes() throws Exception {
         byte[] data = new byte[2 * DWORD];
         System.arraycopy(SIZE_VAL.getBytes(), 0, data, 0, DWORD);
         System.arraycopy(COMP_VAL.getBytes(), 0, data, DWORD, DWORD);

         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.parseFromLocalFileData(data, 0, data.length);
         assertEquals(SIZE_VAL, field.getSize());
         assertEquals(COMP_VAL, field.getCompressedSize());
         assertNull(field.getRelativeHeaderOffset());
         assertNull(field.getDiskStartNumber());
     }

     @Test
     public void testParseLocalFileDataWithAllFields() throws Exception {
         byte[] data = new byte[3 * DWORD + WORD]; // 28
         System.arraycopy(SIZE_VAL.getBytes(), 0, data, 0, DWORD);
         System.arraycopy(COMP_VAL.getBytes(), 0, data, DWORD, DWORD);
         System.arraycopy(OFFSET_VAL.getBytes(), 0, data, 2 * DWORD, DWORD);
         System.arraycopy(DISK_VAL.getBytes(), 0, data, 3 * DWORD, WORD);

         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.parseFromLocalFileData(data, 0, data.length);
         assertEquals(SIZE_VAL, field.getSize());
         assertEquals(COMP_VAL, field.getCompressedSize());
         assertEquals(OFFSET_VAL, field.getRelativeHeaderOffset());
         assertEquals(DISK_VAL, field.getDiskStartNumber());
     }

     @Test
     public void testParseLocalFileDataZeroLengthDoesNothing() throws Exception {
         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.parseFromLocalFileData(new byte[10], 0, 0);
         assertNull(field.getSize());
         assertNull(field.getCompressedSize());
         assertNull(field.getRelativeHeaderOffset());
         assertNull(field.getDiskStartNumber());
     }

     @Test(expected = ZipException.class)
     public void testParseLocalFileDataTooShortThrows() throws Exception {
         byte[] buf = new byte[DWORD]; // 8 bytes, need at least 16
         new Zip64ExtendedInformationExtraField().parseFromLocalFileData(buf, 0, buf.length);
     }

     // -----------------------------------------------------------------------
     // parseFromCentralDirectoryData / reparseCentralDirectoryData
     // -----------------------------------------------------------------------
     @Test
     public void testReparseCentralDirectoryMatchingLengthDoesNotThrow() throws Exception {
         // construct central data with only sizes (16 bytes)
         byte[] centralData = new byte[2 * DWORD];
         System.arraycopy(SIZE_VAL.getBytes(), 0, centralData, 0, DWORD);
         System.arraycopy(COMP_VAL.getBytes(), 0, centralData, DWORD, DWORD);

         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.parseFromCentralDirectoryData(centralData, 0, centralData.length);

         // should not throw when reparse flags match the actual data
         field.reparseCentralDirectoryData(true, true, false, false);
         assertEquals(SIZE_VAL, field.getSize());
         assertEquals(COMP_VAL, field.getCompressedSize());
     }

     @Test
     public void testReparseCentralDirectoryExcessDataThrows() throws Exception {
         // central data with *all* four fields present (28 bytes)
         byte[] centralData = new byte[3 * DWORD + WORD];
         System.arraycopy(SIZE_VAL.getBytes(), 0, centralData, 0, DWORD);
         System.arraycopy(COMP_VAL.getBytes(), 0, centralData, DWORD, DWORD);
         System.arraycopy(OFFSET_VAL.getBytes(), 0, centralData, 2 * DWORD, DWORD);
         System.arraycopy(DISK_VAL.getBytes(), 0, centralData, 3 * DWORD, WORD);

         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.parseFromCentralDirectoryData(centralData, 0, centralData.length);

         // The header may indicate that only sizes are present, so the caller
         // asks to reparse expecting 16 bytes – the raw 28-byte data causes
         // the exception described in COMPRESS-228.
         try {
             field.reparseCentralDirectoryData(true, true, false, false);
             fail("Expected ZipException for excess central directory data");
         } catch (ZipException e) {
             assertTrue(e.getMessage().contains("Expected length 16 but is 28"));
         }
     }

     // -----------------------------------------------------------------------
     // getLocalFileDataData() – must have both sizes
     // -----------------------------------------------------------------------
     @Test(expected = IllegalArgumentException.class)
     public void testLocalFileDataDataThrowsWhenOnlyOneSizeSet() {
         Zip64ExtendedInformationExtraField field = new Zip64ExtendedInformationExtraField();
         field.setSize(SIZE_VAL);  // compressedSize still null
         field.getLocalFileDataData();
     }
 }