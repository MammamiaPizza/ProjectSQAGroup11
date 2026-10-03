package org.apache.commons.compress.archivers.zip;

 import java.util.zip.ZipException;

 import junit.framework.TestCase;

 public class ZipArchiveEntryTest extends TestCase {

     public void testNotEquals() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.setPlatform(ZipArchiveEntry.PLATFORM_FAT);
         assertFalse(entry1.equals(entry2));
     }

     public void testEqualsSameObject() {
         ZipArchiveEntry entry = new ZipArchiveEntry("foo");
         assertTrue(entry.equals(entry));
     }

     public void testEqualsNull() {
         ZipArchiveEntry entry = new ZipArchiveEntry("foo");
         assertFalse(entry.equals(null));
     }

     public void testEqualsDifferentClass() {
         ZipArchiveEntry entry = new ZipArchiveEntry("foo");
         assertFalse(entry.equals("foo"));
     }

     public void testEqualsSubclass() {
         // subclass of ZipEntry not ZipArchiveEntry is not equal
         java.util.zip.ZipEntry other = new java.util.zip.ZipEntry("foo");
         ZipArchiveEntry entry = new ZipArchiveEntry("foo");
         assertFalse(entry.equals(other));
     }

     public void testEqualsSameAttributes() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.setInternalAttributes(123);
         entry1.setExternalAttributes(456);
         entry1.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.setInternalAttributes(123);
         entry2.setExternalAttributes(456);
         entry2.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
         assertTrue(entry1.equals(entry2));
     }

     public void testEqualsDifferentInternalAttributes() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.setInternalAttributes(1);
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.setInternalAttributes(2);
         assertFalse(entry1.equals(entry2));
     }

     public void testEqualsDifferentExternalAttributes() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.setExternalAttributes(100L);
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.setExternalAttributes(200L);
         assertFalse(entry1.equals(entry2));
     }

     public void testEqualsDifferentUnixMode() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.setUnixMode(0644);
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.setUnixMode(0755);
         assertFalse(entry1.equals(entry2));
     }

     public void testEqualsDifferentExtraFields() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.addExtraField(new DummyExtraField(ZipShort.valueOf(1)));
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.addExtraField(new DummyExtraField(ZipShort.valueOf(2)));
         assertFalse(entry1.equals(entry2));
     }

     public void testHashCodeConsistency() {
         ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
         entry1.setInternalAttributes(123);
         entry1.setExternalAttributes(456);
         entry1.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
         ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
         entry2.setInternalAttributes(123);
         entry2.setExternalAttributes(456);
         entry2.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
         assertTrue(entry1.equals(entry2));
         assertEquals(entry1.hashCode(), entry2.hashCode());
     }

     public void testClone() {
         ZipArchiveEntry original = new ZipArchiveEntry("foo");
         original.setInternalAttributes(42);
         original.setExternalAttributes(1234567890L);
         original.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
         ZipArchiveEntry copy = (ZipArchiveEntry) original.clone();
         assertTrue(original.equals(copy));
         assertEquals(original.hashCode(), copy.hashCode());
     }

     // A minimal ZipExtraField implementation for testing
     private static class DummyExtraField implements ZipExtraField {
         private final ZipShort headerId;

         DummyExtraField(ZipShort headerId) {
             this.headerId = headerId;
         }

         public ZipShort getHeaderId() {
             return headerId;
         }

         public ZipShort getLocalFileDataLength() {
             return ZipShort.valueOf(0);
         }

         public ZipShort getCentralDirectoryLength() {
             return ZipShort.valueOf(0);
         }

         public byte[] getLocalFileDataData() {
             return new byte[0];
         }

         public byte[] getCentralDirectoryData() {
             return new byte[0];
         }

         public void parseFromLocalFileData(byte[] data, int offset, int length)
                 throws ZipException { }

         public void parseFromCentralDirectoryData(byte[] data, int offset, int length)
                 throws ZipException { }
     }
 }