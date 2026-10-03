package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class X5455_ExtendedTimestampBugTest {

    @Test
    public void zipLongSettersRetainValuesAndSetOnlyTheirPresenceBits() {
        final X5455_ExtendedTimestamp timestamp = new X5455_ExtendedTimestamp();
        final ZipLong modify = new ZipLong(1L);
        final ZipLong access = new ZipLong(2L);
        final ZipLong create = new ZipLong(3L);

        timestamp.setModifyTime(modify);
        assertSame(modify, timestamp.getModifyTime());
        assertEquals(X5455_ExtendedTimestamp.MODIFY_TIME_BIT, timestamp.getFlags());
        assertTrue(timestamp.isBit0_modifyTimePresent());
        assertFalse(timestamp.isBit1_accessTimePresent());
        assertFalse(timestamp.isBit2_createTimePresent());

        timestamp.setAccessTime(access);
        assertSame(access, timestamp.getAccessTime());
        assertEquals((byte) (X5455_ExtendedTimestamp.MODIFY_TIME_BIT
                | X5455_ExtendedTimestamp.ACCESS_TIME_BIT), timestamp.getFlags());
        assertTrue(timestamp.isBit0_modifyTimePresent());
        assertTrue(timestamp.isBit1_accessTimePresent());
        assertFalse(timestamp.isBit2_createTimePresent());

        timestamp.setCreateTime(create);
        assertSame(create, timestamp.getCreateTime());
        assertEquals((byte) (X5455_ExtendedTimestamp.MODIFY_TIME_BIT
                | X5455_ExtendedTimestamp.ACCESS_TIME_BIT
                | X5455_ExtendedTimestamp.CREATE_TIME_BIT), timestamp.getFlags());
        assertTrue(timestamp.isBit0_modifyTimePresent());
        assertTrue(timestamp.isBit1_accessTimePresent());
        assertTrue(timestamp.isBit2_createTimePresent());
    }

    @Test
    public void javaDateSettersConvertMillisecondsToWholeUnixSeconds() {
        final X5455_ExtendedTimestamp timestamp = new X5455_ExtendedTimestamp();
        final long seconds = 123456789L;
        final Date input = new Date(seconds * 1000L + 987L);
        final Date expected = new Date(seconds * 1000L);

        timestamp.setModifyJavaTime(input);
        timestamp.setAccessJavaTime(input);
        timestamp.setCreateJavaTime(input);

        assertEquals(seconds, timestamp.getModifyTime().getValue());
        assertEquals(seconds, timestamp.getAccessTime().getValue());
        assertEquals(seconds, timestamp.getCreateTime().getValue());
        assertEquals(expected, timestamp.getModifyJavaTime());
        assertEquals(expected, timestamp.getAccessJavaTime());
        assertEquals(expected, timestamp.getCreateJavaTime());
    }

    @Test
    public void javaDateSettersSupportUnixEpoch() {
        final X5455_ExtendedTimestamp timestamp = new X5455_ExtendedTimestamp();
        final Date epoch = new Date(0L);

        timestamp.setModifyJavaTime(epoch);
        timestamp.setAccessJavaTime(epoch);
        timestamp.setCreateJavaTime(epoch);

        assertEquals(0L, timestamp.getModifyTime().getValue());
        assertEquals(0L, timestamp.getAccessTime().getValue());
        assertEquals(0L, timestamp.getCreateTime().getValue());
        assertEquals(epoch, timestamp.getModifyJavaTime());
        assertEquals(epoch, timestamp.getAccessJavaTime());
        assertEquals(epoch, timestamp.getCreateJavaTime());
    }

    @Test
    public void javaDateSettersSupportMaximumUnsigned32BitUnixTime() {
        final long maximumUnsigned32BitSeconds = 0xffffffffL;
        final Date maximumDate = new Date(maximumUnsigned32BitSeconds * 1000L);
        final X5455_ExtendedTimestamp timestamp = new X5455_ExtendedTimestamp();

        timestamp.setModifyJavaTime(maximumDate);
        timestamp.setAccessJavaTime(maximumDate);
        timestamp.setCreateJavaTime(maximumDate);

        assertEquals(maximumUnsigned32BitSeconds, timestamp.getModifyTime().getValue());
        assertEquals(maximumUnsigned32BitSeconds, timestamp.getAccessTime().getValue());
        assertEquals(maximumUnsigned32BitSeconds, timestamp.getCreateTime().getValue());
        assertEquals(maximumDate, timestamp.getModifyJavaTime());
        assertEquals(maximumDate, timestamp.getAccessJavaTime());
        assertEquals(maximumDate, timestamp.getCreateJavaTime());
    }

    @Test
    public void nullJavaDateClearsOnlyTheCorrespondingTimestamp() {
        final X5455_ExtendedTimestamp timestamp = new X5455_ExtendedTimestamp();
        final ZipLong access = new ZipLong(42L);
        final ZipLong create = new ZipLong(43L);

        timestamp.setModifyJavaTime(new Date(1000L));
        timestamp.setAccessTime(access);
        timestamp.setCreateTime(create);
        timestamp.setModifyJavaTime(null);

        assertNull(timestamp.getModifyTime());
        assertNull(timestamp.getModifyJavaTime());
        assertFalse(timestamp.isBit0_modifyTimePresent());
        assertSame(access, timestamp.getAccessTime());
        assertSame(create, timestamp.getCreateTime());
        assertTrue(timestamp.isBit1_accessTimePresent());
        assertTrue(timestamp.isBit2_createTimePresent());
        assertEquals((byte) (X5455_ExtendedTimestamp.ACCESS_TIME_BIT
                | X5455_ExtendedTimestamp.CREATE_TIME_BIT), timestamp.getFlags());
    }
}