package org.apache.commons.compress.utils;

import org.junit.*;
import static org.junit.Assert.*;

public class ArchiveUtilsSymflowerTest {
	@Test
	public void isArrayZero1() {
		byte[] a = null;
		int size = 0;
		boolean actual = ArchiveUtils.isArrayZero(a, size);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isArrayZero2() {
		byte[] a = null;
		int size = 1;
		ArchiveUtils.isArrayZero(a, size);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isArrayZero3() {
		byte[] a = {};
		int size = 1;
		ArchiveUtils.isArrayZero(a, size);
	}

	@Test
	public void isArrayZero4() {
		byte[] a = { -1 };
		int size = 1;
		boolean actual = ArchiveUtils.isArrayZero(a, size);

		assertFalse(actual);
	}

	@Test
	public void isArrayZero5() {
		byte[] a = { 0 };
		int size = 1;
		boolean actual = ArchiveUtils.isArrayZero(a, size);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual6() {
		byte[] buffer1 = null;
		int offset1 = -1;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual7() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = -1;
		byte[] buffer2 = null;
		int offset2 = -2147483648;
		int length2 = 0;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual8() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = -1;
		byte[] buffer2 = {};
		int offset2 = -2147483648;
		int length2 = 0;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test
	public void isEqual9() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		boolean ignoreTrailingNulls = false;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertFalse(actual);
	}

	@Test
	public void isEqual10() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean ignoreTrailingNulls = false;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertTrue(actual);
	}

	@Test
	public void isEqual11() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual12() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual13() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = {};
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test
	public void isEqual14() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = { 0 };
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = true;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertTrue(actual);
	}

	@Test
	public void isEqual15() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = { 0, 64 };
		int offset2 = 1;
		int length2 = 1;
		boolean ignoreTrailingNulls = true;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual16() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual17() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual18() {
		byte[] buffer1 = {};
		int offset1 = -1;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual19() {
		byte[] buffer1 = {};
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		boolean ignoreTrailingNulls = true;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual20() {
		byte[] buffer1 = {};
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test
	public void isEqual21() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean ignoreTrailingNulls = true;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual22() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual23() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = {};
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);
	}

	@Test
	public void isEqual24() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = { 0 };
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertTrue(actual);
	}

	@Test
	public void isEqual25() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = { 1 };
		int offset2 = 0;
		int length2 = 1;
		boolean ignoreTrailingNulls = false;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertFalse(actual);
	}

	@Test
	public void isEqual26() {
		byte[] buffer1 = { 0, -128 };
		int offset1 = 1;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean ignoreTrailingNulls = true;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2, ignoreTrailingNulls);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual27() {
		byte[] buffer1 = null;
		byte[] buffer2 = null;
		ArchiveUtils.isEqual(buffer1, buffer2);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual28() {
		byte[] buffer1 = {};
		byte[] buffer2 = null;
		ArchiveUtils.isEqual(buffer1, buffer2);
	}

	@Test
	public void isEqual29() {
		byte[] buffer1 = {};
		byte[] buffer2 = {};
		boolean actual = ArchiveUtils.isEqual(buffer1, buffer2);

		assertTrue(actual);
	}

	@Test
	public void isEqual30() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);

		assertFalse(actual);
	}

	@Test
	public void isEqual31() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);

		assertTrue(actual);
	}

	@Test
	public void isEqual32() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual33() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual34() {
		byte[] buffer1 = {};
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual35() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqual36() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = {};
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test
	public void isEqual37() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = { 0 };
		int offset2 = 0;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);

		assertTrue(actual);
	}

	@Test
	public void isEqual38() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = { 1 };
		int offset2 = 0;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqual(buffer1, offset1, length1, buffer2, offset2, length2);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual39() {
		byte[] buffer1 = null;
		byte[] buffer2 = null;
		boolean ignoreTrailingNulls = false;
		ArchiveUtils.isEqual(buffer1, buffer2, ignoreTrailingNulls);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqual40() {
		byte[] buffer1 = {};
		byte[] buffer2 = null;
		boolean ignoreTrailingNulls = false;
		ArchiveUtils.isEqual(buffer1, buffer2, ignoreTrailingNulls);
	}

	@Test
	public void isEqual41() {
		byte[] buffer1 = {};
		byte[] buffer2 = {};
		boolean ignoreTrailingNulls = false;
		boolean actual = ArchiveUtils.isEqual(buffer1, buffer2, ignoreTrailingNulls);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqualWithNull42() {
		byte[] buffer1 = null;
		int offset1 = -1;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqualWithNull43() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = -1;
		byte[] buffer2 = null;
		int offset2 = -2147483648;
		int length2 = 0;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqualWithNull44() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = -1;
		byte[] buffer2 = {};
		int offset2 = -2147483648;
		int length2 = 0;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test
	public void isEqualWithNull45() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqualWithNull46() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqualWithNull47() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = {};
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test
	public void isEqualWithNull48() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = { 0 };
		int offset2 = 0;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertTrue(actual);
	}

	@Test
	public void isEqualWithNull49() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = { 0, 64 };
		int offset2 = 1;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqualWithNull50() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqualWithNull51() {
		byte[] buffer1 = null;
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqualWithNull52() {
		byte[] buffer1 = {};
		int offset1 = -1;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqualWithNull53() {
		byte[] buffer1 = {};
		int offset1 = 0;
		int length1 = 0;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = -2147483648;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqualWithNull54() {
		byte[] buffer1 = {};
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test
	public void isEqualWithNull55() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEqualWithNull56() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isEqualWithNull57() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = {};
		int offset2 = 0;
		int length2 = 1;
		ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);
	}

	@Test
	public void isEqualWithNull58() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = { 0 };
		int offset2 = 0;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertTrue(actual);
	}

	@Test
	public void isEqualWithNull59() {
		byte[] buffer1 = { 0 };
		int offset1 = 0;
		int length1 = 1;
		byte[] buffer2 = { 1 };
		int offset2 = 0;
		int length2 = 1;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertFalse(actual);
	}

	@Test
	public void isEqualWithNull60() {
		byte[] buffer1 = { 0, -128 };
		int offset1 = 1;
		int length1 = 1;
		byte[] buffer2 = null;
		int offset2 = 0;
		int length2 = 0;
		boolean actual = ArchiveUtils.isEqualWithNull(buffer1, offset1, length1, buffer2, offset2, length2);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchAsciiBuffer61() {
		String expected = null;
		byte[] buffer = null;
		ArchiveUtils.matchAsciiBuffer(expected, buffer);
	}
}
