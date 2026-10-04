package org.apache.commons.compress.archivers.tar;

import org.junit.*;
import static org.junit.Assert.*;

public class TarUtilsSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void computeCheckSum41() {
		byte[] buf = null;
		TarUtils.computeCheckSum(buf);
	}

	@Test
	public void computeCheckSum42() {
		byte[] buf = {};
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test
	public void computeCheckSum43() {
		byte[] buf = { 0 };
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes44() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes45() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes46() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes47() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes48() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes49() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes50() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes51() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes52() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes53() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes54() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes55() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes56() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes57() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes58() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes59() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes60() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes61() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes62() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes63() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes64() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes65() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes66() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes67() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes68() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes69() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes70() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes71() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes72() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes73() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes74() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes75() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes76() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes77() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes78() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes79() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes80() {
		long value = 1L;
		byte[] buf = null;
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes81() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes82() {
		long value = 1L;
		byte[] buf = {};
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes83() {
		long value = 1L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes84() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatNameBytes85() {
		String name = null;
		byte[] buf = null;
		int offset = -1;
		int length = -2147483648;
		int expected = 2147483647;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatNameBytes86() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		int expected = 0;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes87() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes88() {
		String name = "";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes89() {
		String name = "";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes90() {
		String name = "";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes91() {
		String name = "A";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes92() {
		String name = "A";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes93() {
		String name = "\u00fe";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes94() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes95() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes96() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes97() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes98() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes99() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes100() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes101() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes102() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes103() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes104() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes105() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes106() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes107() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes108() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes109() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes110() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes111() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes112() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes113() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes114() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString115() {
		long value = 0L;
		byte[] buffer = null;
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString116() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString117() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString118() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString119() {
		long value = 0L;
		byte[] buffer = {};
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString120() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString121() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString122() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString123() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = -1;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString124() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString125() {
		long value = 0L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString126() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString127() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString128() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString129() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString130() {
		long value = 1L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString131() {
		long value = 1L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void parseOctal132() {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctal133() {
		byte[] buffer = null;
		int offset = 0;
		int length = 1;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal134() {
		byte[] buffer = null;
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal135() {
		byte[] buffer = {};
		int offset = 0;
		int length = 1;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal136() {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal137() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 1;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal138() {
		byte[] buffer = { 48 };
		int offset = 0;
		int length = 1;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal139() {
		byte[] buffer = { 49 };
		int offset = 0;
		int length = 1;
		long expected = 1L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal140() {
		byte[] buffer = { 0, 0, 50, 32 };
		int offset = 2;
		int length = 2;
		long expected = 2L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal141() {
		byte[] buffer = { 0, 0, 52, 48 };
		int offset = 2;
		int length = 2;
		long expected = 32L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}
}
