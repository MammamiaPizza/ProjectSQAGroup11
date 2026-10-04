package org.apache.commons.compress.archivers.tar;

import org.junit.*;
import static org.junit.Assert.*;

public class TarUtilsSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void computeCheckSum19() {
		byte[] buf = null;
		TarUtils.computeCheckSum(buf);
	}

	@Test
	public void computeCheckSum20() {
		byte[] buf = {};
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test
	public void computeCheckSum21() {
		byte[] buf = { 0 };
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes22() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes23() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes24() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes25() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes26() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes27() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes28() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes29() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes30() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes31() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes32() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes33() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes34() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes35() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes36() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes37() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes38() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes39() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes40() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes41() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes42() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes43() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes44() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes45() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes46() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes47() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes48() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes49() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes50() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes51() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes52() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes53() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes54() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes55() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes56() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes57() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes58() {
		long value = 1L;
		byte[] buf = null;
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes59() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes60() {
		long value = 1L;
		byte[] buf = {};
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes61() {
		long value = 1L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes62() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes63() {
		long value = -1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes64() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes65() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes66() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes67() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes68() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes69() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes70() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes71() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes72() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes73() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes74() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes75() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalOrBinaryBytes76() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes77() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = -1;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes78() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes79() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test
	public void formatNameBytes80() {
		String name = null;
		byte[] buf = null;
		int offset = -1;
		int length = -2147483648;
		int expected = 2147483647;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatNameBytes81() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		int expected = 0;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes82() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes83() {
		String name = "";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes84() {
		String name = "";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes85() {
		String name = "";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes86() {
		String name = "A";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes87() {
		String name = "A";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes88() {
		String name = "\u00fe";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes89() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes90() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes91() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes92() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes93() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes94() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes95() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes96() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes97() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes98() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes99() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes100() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes101() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes102() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes103() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes104() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes105() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes106() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes107() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes108() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes109() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString110() {
		long value = 0L;
		byte[] buffer = null;
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString111() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString112() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString113() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString114() {
		long value = 0L;
		byte[] buffer = {};
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString115() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString116() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString117() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString118() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = -1;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString119() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString120() {
		long value = 0L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString121() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString122() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString123() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString124() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString125() {
		long value = 1L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString126() {
		long value = 1L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseBoolean127() {
		byte[] buffer = null;
		int offset = 0;
		TarUtils.parseBoolean(buffer, offset);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseBoolean128() {
		byte[] buffer = {};
		int offset = 0;
		TarUtils.parseBoolean(buffer, offset);
	}

	@Test
	public void parseBoolean129() {
		byte[] buffer = { 0 };
		int offset = 0;
		boolean actual = TarUtils.parseBoolean(buffer, offset);

		assertFalse(actual);
	}

	@Test
	public void parseBoolean130() {
		byte[] buffer = { 0, 1 };
		int offset = 1;
		boolean actual = TarUtils.parseBoolean(buffer, offset);

		assertTrue(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseOctal131() throws IllegalArgumentException {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctal132() {
		byte[] buffer = null;
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal133() {
		byte[] buffer = null;
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal134() {
		byte[] buffer = {};
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal135() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal136() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal137() {
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal138() {
		byte[] buffer = { 48, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal139() {
		byte[] buffer = { 48, 0, 0 };
		int offset = 0;
		int length = 3;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal140() {
		byte[] buffer = { 0, 0, 32, 32 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctalOrBinary141() {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary142() {
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseOctalOrBinary143() throws IllegalArgumentException {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary144() {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary145() {
		byte[] buffer = { 1 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary146() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test
	public void parseOctalOrBinary147() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 0;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary148() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test
	public void parseOctalOrBinary149() {
		byte[] buffer = { 0, 0 };
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary150() {
		byte[] buffer = { 48, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary151() {
		byte[] buffer = { 48, 0, 0 };
		int offset = 0;
		int length = 3;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary152() {
		byte[] buffer = { 0, 0, -128, 0 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary153() {
		byte[] buffer = { 0, 0, 32, 32 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}
}
