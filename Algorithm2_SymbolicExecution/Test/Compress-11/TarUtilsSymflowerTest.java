package org.apache.commons.compress.archivers.tar;

import org.junit.*;
import static org.junit.Assert.*;

public class TarUtilsSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void computeCheckSum336() {
		byte[] buf = null;
		TarUtils.computeCheckSum(buf);
	}

	@Test
	public void computeCheckSum337() {
		byte[] buf = {};
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test
	public void computeCheckSum338() {
		byte[] buf = { 0 };
		long expected = 0L;
		long actual = TarUtils.computeCheckSum(buf);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes339() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes340() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes341() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes342() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes343() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes344() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes345() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes346() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes347() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes348() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes349() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes350() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes351() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes352() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes353() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes354() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatCheckSumOctalBytes355() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes356() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatCheckSumOctalBytes357() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatCheckSumOctalBytes358() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatCheckSumOctalBytes359() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatCheckSumOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes360() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes361() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes362() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes363() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes364() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes365() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes366() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes367() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes368() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes369() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes370() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes371() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes372() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes373() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes374() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalBytes375() {
		long value = 1L;
		byte[] buf = null;
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes376() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalBytes377() {
		long value = 1L;
		byte[] buf = {};
		int offset = 3;
		int length = 2147483647;
		TarUtils.formatLongOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalBytes378() {
		long value = 1L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatLongOctalBytes379() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatLongOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes380() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes381() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes382() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes383() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatLongOctalOrBinaryBytes384() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes385() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483647;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes386() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes387() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes388() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes389() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes390() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 3;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes391() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test
	public void formatLongOctalOrBinaryBytes392() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 2;
		int expected = 2;
		int actual = TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes393() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 2;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes394() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = -1;
		int length = -2147483648;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes395() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatLongOctalOrBinaryBytes396() {
		long value = 8589934592L;
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatLongOctalOrBinaryBytes(value, buf, offset, length);
	}

	@Test
	public void formatNameBytes397() {
		String name = null;
		byte[] buf = null;
		int offset = -1;
		int length = -2147483648;
		int expected = 2147483647;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatNameBytes398() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		int expected = 0;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes399() {
		String name = null;
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes400() {
		String name = "";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes401() {
		String name = "";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes402() {
		String name = "";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatNameBytes403() {
		String name = "A";
		byte[] buf = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatNameBytes404() {
		String name = "A";
		byte[] buf = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatNameBytes(name, buf, offset, length);
	}

	@Test
	public void formatNameBytes405() {
		String name = "\u00fe";
		byte[] buf = { 0 };
		int offset = 0;
		int length = 1;
		int expected = 1;
		int actual = TarUtils.formatNameBytes(name, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes406() {
		long value = 0L;
		byte[] buf = null;
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes407() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes408() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes409() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes410() {
		long value = 0L;
		byte[] buf = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes411() {
		long value = 0L;
		byte[] buf = {};
		int offset = -2147483646;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes412() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483645;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes413() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483646;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes414() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes415() {
		long value = 0L;
		byte[] buf = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes416() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = -1;
		int length = 4;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes417() {
		long value = 0L;
		byte[] buf = { 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes418() {
		long value = 0L;
		byte[] buf = { 0, 0 };
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes419() {
		long value = 0L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes420() {
		long value = 0L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes421() {
		long value = 1L;
		byte[] buf = null;
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatOctalBytes422() {
		long value = 1L;
		byte[] buf = null;
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes423() {
		long value = 1L;
		byte[] buf = {};
		int offset = 0;
		int length = 3;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatOctalBytes424() {
		long value = 1L;
		byte[] buf = {};
		int offset = 4;
		int length = 2147483647;
		TarUtils.formatOctalBytes(value, buf, offset, length);
	}

	@Test
	public void formatOctalBytes425() {
		long value = 1L;
		byte[] buf = { 0, 0, 0 };
		int offset = 0;
		int length = 3;
		int expected = 3;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void formatOctalBytes426() {
		long value = 1L;
		byte[] buf = { 0, 0, 0, 0 };
		int offset = 0;
		int length = 4;
		int expected = 4;
		int actual = TarUtils.formatOctalBytes(value, buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString427() {
		long value = 0L;
		byte[] buffer = null;
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString428() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString429() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString430() {
		long value = 0L;
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString431() {
		long value = 0L;
		byte[] buffer = {};
		int offset = -2147483648;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString432() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString433() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = -2147483648;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString434() {
		long value = 0L;
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString435() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = -1;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString436() {
		long value = 0L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString437() {
		long value = 0L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString438() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void formatUnsignedOctalString439() {
		long value = 1L;
		byte[] buffer = null;
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString440() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void formatUnsignedOctalString441() {
		long value = 1L;
		byte[] buffer = {};
		int offset = 2;
		int length = 2147483647;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString442() {
		long value = 1L;
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 1;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test
	public void formatUnsignedOctalString443() {
		long value = 1L;
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		TarUtils.formatUnsignedOctalString(value, buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseBoolean444() {
		byte[] buffer = null;
		int offset = 0;
		TarUtils.parseBoolean(buffer, offset);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseBoolean445() {
		byte[] buffer = {};
		int offset = 0;
		TarUtils.parseBoolean(buffer, offset);
	}

	@Test
	public void parseBoolean446() {
		byte[] buffer = { 0 };
		int offset = 0;
		boolean actual = TarUtils.parseBoolean(buffer, offset);

		assertFalse(actual);
	}

	@Test
	public void parseBoolean447() {
		byte[] buffer = { 0, 1 };
		int offset = 1;
		boolean actual = TarUtils.parseBoolean(buffer, offset);

		assertTrue(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseOctal448() throws IllegalArgumentException {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctal449() {
		byte[] buffer = null;
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal450() {
		byte[] buffer = null;
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal451() {
		byte[] buffer = {};
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal452() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctal453() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 2;
		TarUtils.parseOctal(buffer, offset, length);
	}

	@Test
	public void parseOctal454() {
		byte[] buffer = { 0, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal455() {
		byte[] buffer = { 48, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal456() {
		byte[] buffer = { 48, 0, 0 };
		int offset = 0;
		int length = 3;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctal457() {
		byte[] buffer = { 0, 0, 32, 32 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctal(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void parseOctalOrBinary458() {
		byte[] buffer = null;
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary459() {
		byte[] buffer = {};
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseOctalOrBinary460() throws IllegalArgumentException {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 0;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary461() {
		byte[] buffer = { 0 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary462() {
		byte[] buffer = { 1 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary463() {
		byte[] buffer = { 32 };
		int offset = 0;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test
	public void parseOctalOrBinary464() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 0;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void parseOctalOrBinary465() {
		byte[] buffer = { 0, -128 };
		int offset = 1;
		int length = 2;
		TarUtils.parseOctalOrBinary(buffer, offset, length);
	}

	@Test
	public void parseOctalOrBinary466() {
		byte[] buffer = { 0, 0 };
		int offset = 1;
		int length = 2147483647;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary467() {
		byte[] buffer = { 48, 0 };
		int offset = 0;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary468() {
		byte[] buffer = { 48, 0, 0 };
		int offset = 0;
		int length = 3;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary469() {
		byte[] buffer = { 0, 0, -128, 0 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void parseOctalOrBinary470() {
		byte[] buffer = { 0, 0, 32, 32 };
		int offset = 2;
		int length = 2;
		long expected = 0L;
		long actual = TarUtils.parseOctalOrBinary(buffer, offset, length);

		assertEquals(expected, actual);
	}
}
