package org.apache.commons.compress.archivers.tar;

import org.junit.*;
import static org.junit.Assert.*;

public class TarUtilsSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void computeCheckSum48() {
		byte[] buf = null;
		TarUtils.computeCheckSum(buf);
	}

	@Test
	public void computeCheckSum49() {
		byte[] buf = {};
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test
	public void computeCheckSum50() {
		byte[] buf = { 0 };
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes51() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes52() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes53() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes54() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes55() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes56() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes57() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes58() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes59() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes60() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes61() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes62() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes63() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes64() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes65() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes66() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes67() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes68() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes69() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes70() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes71() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes72() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes73() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes74() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes75() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes76() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes77() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes78() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes79() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes80() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes81() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes82() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes83() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes84() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes85() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes86() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes87() {
		long value = 1L;
		byte[] buf = null;
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes88() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes89() {
		long value = 1L;
		byte[] buf = {};
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes90() {
		long value = 1L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes91() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes92() {
		long value = -1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes93() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes94() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes95() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes96() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes97() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes98() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes99() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes100() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes101() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes102() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes103() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes104() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalOrBinaryBytes105() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes106() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = -1;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes107() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes108() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test
	public void formatNameBytes109() {
		String name = null;
		byte[] buf = null;
		int offset = -1;
		int length = -2147483648;
		int expected = 2147483647;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatNameBytes110() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		int expected = 0;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes111() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes112() {
		String name = "";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes113() {
		String name = "";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes114() {
		String name = "";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes115() {
		String name = "A";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes116() {
		String name = "A";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes117() {
		String name = "\u00fe";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes118() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes119() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes120() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes121() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes122() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes123() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes124() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes125() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes126() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes127() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes128() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes129() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes130() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes131() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes132() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes133() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes134() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes135() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes136() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes137() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes138() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString139() {
		long value = 0L;
		byte[] buffer = null;
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString140() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString141() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString142() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString143() {
		long value = 0L;
		byte[] buffer = {};
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString144() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString145() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString146() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString147() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = -1;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString148() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString149() {
		long value = 0L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString150() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString151() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString152() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString153() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString154() {
		long value = 1L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString155() {
		long value = 1L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseBoolean156() {
		byte[] buffer = null;
		int offset = 0;
		TarUtils.parseBoolean(buffer, offset);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseBoolean157() {
		byte[] buffer = {};
		int offset = 0;
		TarUtils.parseBoolean(buffer, offset);
	}

	@Test
	public void parseBoolean158() {
		byte[] buffer = { 0 };
		int offset = 0;
		boolean actual = TarUtils.parseBoolean(buffer, offset);

		assertFalse(actual);
	}

	@Test
	public void parseBoolean159() {
		byte[] buffer = { 0, 1 };
		int offset = 1;
		boolean actual = TarUtils.parseBoolean(buffer, offset);

		assertTrue(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseOctal160() throws IllegalArgumentException {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctal161() {
		byte[] buffer = null;
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal162() {
		byte[] buffer = null;
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal163() {
		byte[] buffer = {};
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal164() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal165() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal166() {
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal167() {
		byte[] buffer = { 48, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal168() {
		byte[] buffer = { 48, 0, 0 };
		int offset = 0;
		int length = 3;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal169() {
		byte[] buffer = { 0, 0, 32, 32 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctalOrBinary170() {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary171() {
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseOctalOrBinary172() throws IllegalArgumentException {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary173() {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary174() {
		byte[] buffer = { 1 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary175() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test
	public void parseOctalOrBinary176() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 0;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary177() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test
	public void parseOctalOrBinary178() {
		byte[] buffer = { 0, 0 };
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary179() {
		byte[] buffer = { 48, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary180() {
		byte[] buffer = { 48, 0, 0 };
		int offset = 0;
		int length = 3;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary181() {
		byte[] buffer = { 0, 0, -128, 0 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary182() {
		byte[] buffer = { 0, 0, 32, 32 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}
}
