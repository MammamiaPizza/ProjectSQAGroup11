package org.apache.commons.compress.archivers.zip;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests equals/hashCode behavior of ZipArchiveEntry, targeting the bug that
  * entry with null comment should be considered equal to entry with empty comment.
  */
 public class ZipArchiveEntryBug15Test {

     @Test
     public void testNullCommentEqualsEmptyComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         b.setComment("");  // empty string
         // The contract after fix: null and empty comment are considered equal
         assertTrue("Entries with null and empty comment should be equal", a.equals(b));
         assertTrue("Symmetry for null vs empty comment", b.equals(a));
         assertEquals("Hash codes should match for null vs empty comment", a.hashCode(),
b.hashCode());
     }

     @Test
     public void testBothNullComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         assertTrue("Both entries with null comment should be equal", a.equals(b));
         assertEquals("Hash codes should match for both null comment", a.hashCode(), b.hashCode());
     }

     @Test
     public void testBothEmptyComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         b.setComment("");
         assertTrue("Both entries with empty comment should be equal", a.equals(b));
         assertEquals("Hash codes should match for both empty comment", a.hashCode(), b.hashCode());
     }

     @Test
     public void testEqualNonEmptyComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("bar");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         b.setComment("bar");
         assertTrue("Entries with same non-empty comment should be equal", a.equals(b));
         assertEquals("Hash codes should match for same non-empty comment", a.hashCode(),
b.hashCode());
     }

     @Test
     public void testUnequalComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("bar");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         b.setComment("baz");
         assertFalse("Entries with different comments should not be equal", a.equals(b));
     }

     @Test
     public void testDifferentName() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         ZipArchiveEntry b = new ZipArchiveEntry("bar");
         assertFalse("Entries with different names should not be equal", a.equals(b));
     }

     @Test
     public void testSelfEquals() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         assertTrue("Self-comparison should return true", a.equals(a));
     }

     @Test
     public void testNullOther() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         assertFalse("Comparison with null should return false", a.equals(null));
     }

     @Test
     public void testOtherClass() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         assertFalse("Comparison with object of different class should return false", a.equals(new
Object()));
     }

     @Test
     public void testCloneEqualsOriginal() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("some comment");
         ZipArchiveEntry clone = (ZipArchiveEntry) a.clone();
         assertTrue("Clone should be equal to original", a.equals(clone));
         assertEquals("Hash codes should match for clone and original", a.hashCode(),
clone.hashCode());
     }

     @Test
     public void testCopyConstructorEqualsOriginal() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("some comment");
         ZipArchiveEntry b = new ZipArchiveEntry(a);  // copy constructor
         assertTrue("Copy-constructed entry should equal original", a.equals(b));
         assertEquals("Hash codes should match for copy and original", a.hashCode(), b.hashCode());
     }

     @Test
     public void testHashCodeConsistency() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("bar");
         int hash1 = a.hashCode();
         int hash2 = a.hashCode();
         assertEquals("Hash code should be consistent across calls", hash1, hash2);
     }

     @Test
     public void testNullCommentVsNonEmptyComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         b.setComment("bar");
         assertFalse("Entry with null comment should not equal entry with non-empty comment",
a.equals(b));
         assertFalse("Symmetry: entry with non-empty comment should not equal entry with null
comment", b.equals(a));
     }

     @Test
     public void testEmptyCommentVsNonEmptyComment() {
         ZipArchiveEntry a = new ZipArchiveEntry("foo");
         a.setComment("");
         ZipArchiveEntry b = new ZipArchiveEntry("foo");
         b.setComment("bar");
         assertFalse("Entry with empty comment should not equal entry with non-empty comment",
a.equals(b));
         assertFalse("Symmetry for empty vs non-empty comment", b.equals(a));
     }
 }
