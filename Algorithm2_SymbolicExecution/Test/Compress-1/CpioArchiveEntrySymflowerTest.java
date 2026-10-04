package org.apache.commons.compress.archivers.cpio;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class CpioArchiveEntrySymflowerTest {
	@Test
	public void CpioArchiveEntry1() throws IllegalAccessException, NoSuchFieldException {
		short format = 1;
		CpioArchiveEntry expected = new CpioArchiveEntry(0);
		expected.setTime(-1L);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(expected, 1);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(expected, 110L);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(expected, -1L);
		CpioArchiveEntry actual = new CpioArchiveEntry(format);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void CpioArchiveEntry2() throws IllegalAccessException, NoSuchFieldException {
		String name = null;
		long size = 0L;
		CpioArchiveEntry expected = new CpioArchiveEntry(null, 0L);
		expected.setTime(-1L);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(expected, 1);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(expected, 110L);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(expected, -1L);
		CpioArchiveEntry actual = new CpioArchiveEntry(name, size);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void CpioArchiveEntry3() throws IllegalAccessException, NoSuchFieldException {
		String name = null;
		CpioArchiveEntry expected = new CpioArchiveEntry(null);
		expected.setTime(-1L);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(expected, 1);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(expected, 110L);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(expected, -1L);
		CpioArchiveEntry actual = new CpioArchiveEntry(name);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void getChksum4() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long expected = 0L;
		long actual = c.getChksum();

		assertEquals(expected, actual);
	}

	@Test
	public void getDevice5() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 4);
		long expected = 0L;
		long actual = c.getDevice();

		assertEquals(expected, actual);
	}

	@Test
	public void getDeviceMaj6() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long expected = 0L;
		long actual = c.getDeviceMaj();

		assertEquals(expected, actual);
	}

	@Test
	public void getDeviceMin7() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long expected = 0L;
		long actual = c.getDeviceMin();

		assertEquals(expected, actual);
	}

	@Test
	public void getFormat8() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		short expected = 0;
		short actual = c.getFormat();

		assertEquals(expected, actual);
	}

	@Test
	public void getGID9() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getGID();

		assertEquals(expected, actual);
	}

	@Test
	public void getHeaderSize10() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getHeaderSize();

		assertEquals(expected, actual);
	}

	@Test
	public void getInode11() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getInode();

		assertEquals(expected, actual);
	}

	@Test
	public void getMode12() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getMode();

		assertEquals(expected, actual);
	}

	@Test
	public void getName13() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		String actual = c.getName();

		assertNull(actual);
	}

	@Test
	public void getNumberOfLinks14() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getNumberOfLinks();

		assertEquals(expected, actual);
	}

	@Test
	public void getRemoteDevice15() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 4);
		long expected = 0L;
		long actual = c.getRemoteDevice();

		assertEquals(expected, actual);
	}

	@Test
	public void getRemoteDeviceMaj16() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long expected = 0L;
		long actual = c.getRemoteDeviceMaj();

		assertEquals(expected, actual);
	}

	@Test
	public void getRemoteDeviceMin17() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long expected = 0L;
		long actual = c.getRemoteDeviceMin();

		assertEquals(expected, actual);
	}

	@Test
	public void getSize18() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getSize();

		assertEquals(expected, actual);
	}

	@Test
	public void getTime19() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getTime();

		assertEquals(expected, actual);
	}

	@Test
	public void getUID20() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long expected = 0L;
		long actual = c.getUID();

		assertEquals(expected, actual);
	}

	@Test
	public void isBlockDevice21() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isBlockDevice();

		assertFalse(actual);
	}

	@Test
	public void isBlockDevice22() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 24576L);
		boolean actual = c.isBlockDevice();

		assertTrue(actual);
	}

	@Test
	public void isCharacterDevice23() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isCharacterDevice();

		assertFalse(actual);
	}

	@Test
	public void isCharacterDevice24() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 8192L);
		boolean actual = c.isCharacterDevice();

		assertTrue(actual);
	}

	@Test
	public void isDirectory25() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isDirectory();

		assertFalse(actual);
	}

	@Test
	public void isDirectory26() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 16384L);
		boolean actual = c.isDirectory();

		assertTrue(actual);
	}

	@Test
	public void isNetwork27() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isNetwork();

		assertFalse(actual);
	}

	@Test
	public void isNetwork28() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 36864L);
		boolean actual = c.isNetwork();

		assertTrue(actual);
	}

	@Test
	public void isPipe29() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isPipe();

		assertFalse(actual);
	}

	@Test
	public void isPipe30() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 4096L);
		boolean actual = c.isPipe();

		assertTrue(actual);
	}

	@Test
	public void isRegularFile31() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isRegularFile();

		assertFalse(actual);
	}

	@Test
	public void isRegularFile32() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 32768L);
		boolean actual = c.isRegularFile();

		assertTrue(actual);
	}

	@Test
	public void isSocket33() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isSocket();

		assertFalse(actual);
	}

	@Test
	public void isSocket34() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 49152L);
		boolean actual = c.isSocket();

		assertTrue(actual);
	}

	@Test
	public void isSymbolicLink35() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		boolean actual = c.isSymbolicLink();

		assertFalse(actual);
	}

	@Test
	public void isSymbolicLink36() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(c, 40960L);
		boolean actual = c.isSymbolicLink();

		assertTrue(actual);
	}

	@Test
	public void setChksum37() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long chksum = 0L;
		c.setChksum(chksum);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setDevice38() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 4);
		long device = 0L;
		c.setDevice(device);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 4);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setDeviceMaj39() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long maj = 0L;
		c.setDeviceMaj(maj);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setDeviceMin40() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long min = 0L;
		c.setDeviceMin(min);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setFormat41() throws IllegalArgumentException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		short format = 0;
		c.setFormat(format);
	}

	@Test
	public void setFormat42() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		short format = 1;
		c.setFormat(format);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(cExpected, 1);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(cExpected, 110L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setFormat43() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		short format = 2;
		c.setFormat(format);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(cExpected, 2);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(cExpected, 110L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setFormat44() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		short format = 4;
		c.setFormat(format);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(cExpected, 4);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(cExpected, 76L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setFormat45() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		short format = 8;
		c.setFormat(format);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(cExpected, 8);
		final Field fieldHeaderSize = CpioArchiveEntry.class.getDeclaredField("headerSize");
		fieldHeaderSize.setAccessible(true);
		fieldHeaderSize.set(cExpected, 26L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setGID46() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long gid = 0L;
		c.setGID(gid);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setInode47() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long inode = 0L;
		c.setInode(inode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode48() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 0L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode49() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 16384L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 16384L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode50() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 24576L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 24576L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode51() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 32768L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 32768L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode52() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 36864L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 36864L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode53() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 4096L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 4096L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode54() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 40960L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 40960L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode55() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 49152L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 49152L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setMode56() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long mode = 8192L;
		c.setMode(mode);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldMode = CpioArchiveEntry.class.getDeclaredField("mode");
		fieldMode.setAccessible(true);
		fieldMode.set(cExpected, 8192L);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setName57() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		String name = null;
		c.setName(name);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setNumberOfLinks58() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long nlink = 0L;
		c.setNumberOfLinks(nlink);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setRemoteDevice59() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 4);
		long device = 0L;
		c.setRemoteDevice(device);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 4);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setRemoteDeviceMaj60() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long rmaj = 0L;
		c.setRemoteDeviceMaj(rmaj);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setRemoteDeviceMin61() throws IllegalAccessException, NoSuchFieldException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		final Field fieldFileFormat = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat.setAccessible(true);
		fieldFileFormat.set(c, 1);
		long rmin = 0L;
		c.setRemoteDeviceMin(rmin);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);
		final Field fieldFileFormat2 = CpioArchiveEntry.class.getDeclaredField("fileFormat");
		fieldFileFormat2.setAccessible(true);
		fieldFileFormat2.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setSize62() throws IllegalArgumentException {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long size = -1L;
		c.setSize(size);
	}

	@Test
	public void setSize63() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long size = 0L;
		c.setSize(size);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setTime64() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long time = 0L;
		c.setTime(time);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void setUID65() {
		CpioArchiveEntry c = new CpioArchiveEntry(null);
		long uid = 0L;
		c.setUID(uid);

		CpioArchiveEntry cExpected = new CpioArchiveEntry(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}
}
