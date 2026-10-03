package org.apache.commons.compress.archivers.zip;

 import java.util.Date;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link X5455_ExtendedTimestamp}, covering COMPRESS-416
  * (unsigned 32-bit timestamps must not be rejected).
  */
 public class X5455_ExtendedTimestampTest {

     @Test
     public void testNormalModifyTimeRoundTrip() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         Date input = new Date(1_500_000_000_000L); // some normal date
         ext.setModifyJavaTime(input);
         Date result = ext.getModifyJavaTime();
         assertEquals("modifyTime round-trip", input, result);
     }

     @Test
     public void testModifyTimeUnsigned32Safe() {
         // seconds value just inside unsigned 32-bit range (0xFFFFFFFE)
         long millis = 0xFFFFFFFEL * 1000L;
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         ext.setModifyJavaTime(new Date(millis));
         assertEquals(new Date(millis), ext.getModifyJavaTime());
     }

     @Test
     public void testModifyTimeExactlyUnsigned32Max() {
         // maximum unsigned 32-bit seconds
         long millis = 0xFFFFFFFFL * 1000L;
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         ext.setModifyJavaTime(new Date(millis));
         assertEquals(new Date(millis), ext.getModifyJavaTime());
     }

     @Test
     public void testModifyTimeBeyondUnsigned32DoesNotThrow() {
         // date far beyond 32-bit; must not throw IllegalArgumentException
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         try {
             ext.setModifyJavaTime(new Date(Long.MAX_VALUE));
         } catch (IllegalArgumentException e) {
             if (e.getMessage().contains("Time too big for 32 bits")) {
                 fail("Must not reject timestamps > 32-bit maximum");
             }
             // other IAE may be acceptable, but the bug message is forbidden
         }
         // retrieval may wrap, but should not crash
         assertNotNull(ext.getModifyJavaTime());
     }

     @Test
     public void testZeroFlagsThenModifyTime() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         ext.setFlags((byte) 0);
         // flags = 0 -> no modify bit set, modifyTime internal is null
         assertNull(ext.getModifyJavaTime());
         // now set a time; this should auto-set the bit
         Date input = new Date(1_600_000_000_000L);
         ext.setModifyJavaTime(input);
         assertTrue(ext.isBit0_modifyTimePresent());
         assertEquals(input, ext.getModifyJavaTime());
     }

     @Test
     public void testSetModifyTimeNullClearsBit() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         ext.setModifyJavaTime(new Date(1_500_000_000_000L));
         assertTrue(ext.isBit0_modifyTimePresent());
         ext.setModifyTime(null);
         assertFalse(ext.isBit0_modifyTimePresent());
         assertNull(ext.getModifyJavaTime());
     }

     @Test
     public void testAccessTimeRoundTrip() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         Date input = new Date(1_800_000_000_000L);
         ext.setAccessJavaTime(input);
         assertEquals(input, ext.getAccessJavaTime());
     }

     @Test
     public void testCreateTimeRoundTrip() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         Date input = new Date(0x7FFFFFFF_000L); // 2038-01-19
         ext.setCreateJavaTime(input);
         assertEquals(input, ext.getCreateJavaTime());
     }

     @Test
     public void testSetAccessJavaTimeNullClearsAccess() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         ext.setAccessJavaTime(new Date());
         assertNotNull(ext.getAccessJavaTime());
         ext.setAccessJavaTime(null);
         assertNull(ext.getAccessJavaTime());
         assertFalse(ext.isBit1_accessTimePresent());
     }

     @Test
     public void testParseLocalFileDataRoundTrip() throws Exception {
         X5455_ExtendedTimestamp out = new X5455_ExtendedTimestamp();
         Date modify = new Date(0xFFFFFFFFL * 1000L);
         Date access = new Date(0x80000000L * 1000L);  // signed 32-bit boundary
         out.setModifyJavaTime(modify);
         out.setAccessJavaTime(access);

         byte[] data = out.getLocalFileDataData();
         X5455_ExtendedTimestamp in = new X5455_ExtendedTimestamp();
         in.parseFromLocalFileData(data, 0, data.length);

         assertEquals(modify, in.getModifyJavaTime());
         assertEquals(access, in.getAccessJavaTime());
     }

     @Test
     public void testEqualityAndHashCode() throws CloneNotSupportedException {
         X5455_ExtendedTimestamp ext1 = new X5455_ExtendedTimestamp();
         ext1.setModifyJavaTime(new Date(1_500_000_000_000L));
         ext1.setAccessJavaTime(new Date(1_600_000_000_000L));

         X5455_ExtendedTimestamp ext2 = (X5455_ExtendedTimestamp) ext1.clone();
         assertEquals(ext1, ext2);
         assertEquals(ext1.hashCode(), ext2.hashCode());

         // different flags -> not equal
         ext2.setFlags((byte) 0);
         ext2.setModifyJavaTime(new Date(1_500_000_000_000L));
         assertNotEquals(ext1, ext2);

         // null time vs non-null
         ext2.setFlags((byte) X5455_ExtendedTimestamp.MODIFY_TIME_BIT);
         ext2.setModifyTime(null);
         assertNotEquals(ext1, ext2);
     }

     @Test
     public void testHexHeaderId() {
         X5455_ExtendedTimestamp ext = new X5455_ExtendedTimestamp();
         assertEquals(0x5455, ext.getHeaderId().getValue());
     }
 }
