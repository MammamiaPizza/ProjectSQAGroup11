package org.apache.commons.compress.archivers.zip;

import java.lang.reflect.Field;
import java.util.zip.ZipException;
import org.junit.*;
import static org.junit.Assert.*;

public class X5455_ExtendedTimestampSymflowerTest {
	@Test
	public void X5455_ExtendedTimestamp1() {
		X5455_ExtendedTimestamp expected = new X5455_ExtendedTimestamp();
		X5455_ExtendedTimestamp actual = new X5455_ExtendedTimestamp();

		assertEquals(expected, actual);
	}

	@Test
	public void equals2() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		Object o = null;
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals3() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		Object o = new X5455_ExtendedTimestamp();
		boolean actual = x.equals(o);

		assertTrue(actual);
	}

	@Test
	public void equals4() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		Object o = new X5455_ExtendedTimestamp();
		o.setAccessTime(new ZipLong(0L));
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(o, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(o, 0);
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals5() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		Object o = new X5455_ExtendedTimestamp();
		o.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(o, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(o, 0);
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals6() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setCreateTime(new ZipLong(0L));
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals7() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setCreateTime(new ZipLong(0L));
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		o.setCreateTime(new ZipLong(0L));
		final Field fieldBit2_createTimePresent2 = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent2.setAccessible(true);
		fieldBit2_createTimePresent2.set(o, false);
		final Field fieldFlags2 = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags2.setAccessible(true);
		fieldFlags2.set(o, 0);
		boolean actual = x.equals(o);

		assertTrue(actual);
	}

	@Test
	public void equals8() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setAccessTime(new ZipLong(0L));
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals9() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setAccessTime(new ZipLong(0L));
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		o.setAccessTime(new ZipLong(0L));
		final Field fieldBit1_accessTimePresent2 = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent2.setAccessible(true);
		fieldBit1_accessTimePresent2.set(o, false);
		final Field fieldFlags2 = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags2.setAccessible(true);
		fieldFlags2.set(o, 0);
		boolean actual = x.equals(o);

		assertTrue(actual);
	}

	@Test
	public void equals10() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals11() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		o.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent2 = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent2.setAccessible(true);
		fieldBit0_modifyTimePresent2.set(o, false);
		final Field fieldFlags2 = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags2.setAccessible(true);
		fieldFlags2.set(o, 0);
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals12() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setModifyTime(new ZipLong(0L));
		x.setAccessTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, false);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		o.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent2 = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent2.setAccessible(true);
		fieldBit0_modifyTimePresent2.set(o, false);
		final Field fieldFlags2 = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags2.setAccessible(true);
		fieldFlags2.set(o, 0);
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals13() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setModifyTime(new ZipLong(1L));
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		Object o = new X5455_ExtendedTimestamp();
		o.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent2 = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent2.setAccessible(true);
		fieldBit0_modifyTimePresent2.set(o, false);
		final Field fieldFlags2 = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags2.setAccessible(true);
		fieldFlags2.set(o, 0);
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals14() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setFlags(1);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, false);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, false);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, false);
		Object o = new X5455_ExtendedTimestamp();
		boolean actual = x.equals(o);

		assertFalse(actual);
	}

	@Test
	public void getAccessTime15() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong actual = x.getAccessTime();

		assertNull(actual);
	}

	@Test
	public void getCentralDirectoryLength16() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipShort expected = new ZipShort(1);
		ZipShort actual = x.getCentralDirectoryLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getCentralDirectoryLength17() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, true);
		ZipShort expected = new ZipShort(5);
		ZipShort actual = x.getCentralDirectoryLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getCreateTime18() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong actual = x.getCreateTime();

		assertNull(actual);
	}

	@Test
	public void getFlags19() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte expected = 0;
		byte actual = x.getFlags();

		assertEquals(expected, actual);
	}

	@Test
	public void getHeaderId20() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipShort expected = new ZipShort(21589);
		ZipShort actual = x.getHeaderId();

		assertEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataData21() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] expected = { 0 };
		byte[] actual = x.getLocalFileDataData();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataData22() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, true);
		byte[] expected = { 0 };
		byte[] actual = x.getLocalFileDataData();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataData23() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, true);
		byte[] expected = { 0 };
		byte[] actual = x.getLocalFileDataData();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataLength24() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipShort expected = new ZipShort(1);
		ZipShort actual = x.getLocalFileDataLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataLength25() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, true);
		ZipShort expected = new ZipShort(1);
		ZipShort actual = x.getLocalFileDataLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataLength26() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setCreateTime(new ZipLong(0L));
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, true);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		ZipShort expected = new ZipShort(5);
		ZipShort actual = x.getLocalFileDataLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataLength27() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, true);
		ZipShort expected = new ZipShort(1);
		ZipShort actual = x.getLocalFileDataLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataLength28() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setAccessTime(new ZipLong(0L));
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, true);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		ZipShort expected = new ZipShort(5);
		ZipShort actual = x.getLocalFileDataLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getLocalFileDataLength29() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, true);
		ZipShort expected = new ZipShort(5);
		ZipShort actual = x.getLocalFileDataLength();

		assertEquals(expected, actual);
	}

	@Test
	public void getModifyTime30() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong actual = x.getModifyTime();

		assertNull(actual);
	}

	@Test
	public void hashCode31() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		int expected = 0;
		int actual = x.hashCode();

		assertEquals(expected, actual);
	}

	@Test
	public void hashCode32() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setCreateTime(new ZipLong(0L));
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		int expected = 0;
		int actual = x.hashCode();

		assertEquals(expected, actual);
	}

	@Test
	public void hashCode33() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setAccessTime(new ZipLong(0L));
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		int expected = 0;
		int actual = x.hashCode();

		assertEquals(expected, actual);
	}

	@Test
	public void hashCode34() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		x.setModifyTime(new ZipLong(0L));
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(x, false);
		final Field fieldFlags = X5455_ExtendedTimestamp.class.getDeclaredField("flags");
		fieldFlags.setAccessible(true);
		fieldFlags.set(x, 0);
		int expected = 0;
		int actual = x.hashCode();

		assertEquals(expected, actual);
	}

	@Test
	public void isBit0_modifyTimePresent35() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		boolean actual = x.isBit0_modifyTimePresent();

		assertFalse(actual);
	}

	@Test
	public void isBit1_accessTimePresent36() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		boolean actual = x.isBit1_accessTimePresent();

		assertFalse(actual);
	}

	@Test
	public void isBit2_createTimePresent37() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		boolean actual = x.isBit2_createTimePresent();

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseFromCentralDirectoryData38() throws ZipException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] buffer = {};
		int offset = -1;
		int length = -2147483648;
		x.parseFromCentralDirectoryData(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseFromCentralDirectoryData39() throws ZipException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		x.parseFromCentralDirectoryData(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseFromCentralDirectoryData40() throws ZipException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] buffer = {};
		int offset = 2147483647;
		int length = 0;
		x.parseFromCentralDirectoryData(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseFromLocalFileData41() throws ZipException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] data = {};
		int offset = -1;
		int length = -2147483648;
		x.parseFromLocalFileData(data, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseFromLocalFileData42() throws ZipException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] data = {};
		int offset = 0;
		int length = 0;
		x.parseFromLocalFileData(data, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseFromLocalFileData43() throws ZipException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte[] data = {};
		int offset = 2147483647;
		int length = 0;
		x.parseFromLocalFileData(data, offset, length);
	}

	@Test
	public void setAccessTime44() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong l = null;
		x.setAccessTime(l);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();

		assertEquals(xExpected, x);
	}

	@Test
	public void setAccessTime45() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong l = new ZipLong(0L);
		x.setAccessTime(l);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();
		xExpected.setAccessTime(new ZipLong(0L));
		xExpected.setFlags(2);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(xExpected, false);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(xExpected, true);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(xExpected, false);

		assertEquals(xExpected, x);
	}

	@Test
	public void setCreateTime46() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong l = null;
		x.setCreateTime(l);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();

		assertEquals(xExpected, x);
	}

	@Test
	public void setCreateTime47() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong l = new ZipLong(0L);
		x.setCreateTime(l);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();
		xExpected.setCreateTime(new ZipLong(0L));
		xExpected.setFlags(4);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(xExpected, false);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(xExpected, false);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(xExpected, true);

		assertEquals(xExpected, x);
	}

	@Test
	public void setFlags48() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte flags = 0;
		x.setFlags(flags);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();

		assertEquals(xExpected, x);
	}

	@Test
	public void setFlags49() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte flags = 1;
		x.setFlags(flags);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();
		xExpected.setFlags(1);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(xExpected, true);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(xExpected, false);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(xExpected, false);

		assertEquals(xExpected, x);
	}

	@Test
	public void setFlags50() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte flags = 2;
		x.setFlags(flags);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();
		xExpected.setFlags(2);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(xExpected, false);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(xExpected, true);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(xExpected, false);

		assertEquals(xExpected, x);
	}

	@Test
	public void setFlags51() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		byte flags = 4;
		x.setFlags(flags);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();
		xExpected.setFlags(4);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(xExpected, false);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(xExpected, false);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(xExpected, true);

		assertEquals(xExpected, x);
	}

	@Test
	public void setModifyTime52() {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong l = null;
		x.setModifyTime(l);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();

		assertEquals(xExpected, x);
	}

	@Test
	public void setModifyTime53() throws IllegalAccessException, NoSuchFieldException {
		X5455_ExtendedTimestamp x = new X5455_ExtendedTimestamp();
		ZipLong l = new ZipLong(0L);
		x.setModifyTime(l);

		X5455_ExtendedTimestamp xExpected = new X5455_ExtendedTimestamp();
		xExpected.setModifyTime(new ZipLong(0L));
		xExpected.setFlags(1);
		final Field fieldBit0_modifyTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit0_modifyTimePresent");
		fieldBit0_modifyTimePresent.setAccessible(true);
		fieldBit0_modifyTimePresent.set(xExpected, true);
		final Field fieldBit1_accessTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit1_accessTimePresent");
		fieldBit1_accessTimePresent.setAccessible(true);
		fieldBit1_accessTimePresent.set(xExpected, false);
		final Field fieldBit2_createTimePresent = X5455_ExtendedTimestamp.class.getDeclaredField("bit2_createTimePresent");
		fieldBit2_createTimePresent.setAccessible(true);
		fieldBit2_createTimePresent.set(xExpected, false);

		assertEquals(xExpected, x);
	}
}
