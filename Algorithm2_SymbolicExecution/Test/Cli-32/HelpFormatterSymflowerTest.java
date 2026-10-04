package org.apache.commons.cli;

import java.util.Comparator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class HelpFormatterSymflowerTest {
	@Test // (expected = NegativeArraySizeException.class)
	public void createPadding1() {
		HelpFormatter h = new HelpFormatter();
		int len = -1;
		h.createPadding(len);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void findWrapPos2() {
		HelpFormatter h = new HelpFormatter();
		String text = null;
		int width = 0;
		int startPos = 0;
		h.findWrapPos(text, width, startPos);
	}

	@Test
	public void findWrapPos3() {
		HelpFormatter h = new HelpFormatter();
		String text = "\t";
		int width = 0;
		int startPos = -1;
		int expected = 1;
		int actual = h.findWrapPos(text, width, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void findWrapPos4() {
		HelpFormatter h = new HelpFormatter();
		String text = "\t\n";
		int width = 0;
		int startPos = -1;
		int expected = 1;
		int actual = h.findWrapPos(text, width, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void findWrapPos5() {
		HelpFormatter h = new HelpFormatter();
		String text = "A";
		int width = 1610612736;
		int startPos = -536870912;
		int expected = -1;
		int actual = h.findWrapPos(text, width, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void getArgName6() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getArgName();

		assertNull(actual);
	}

	@Test
	public void getDescPadding7() {
		HelpFormatter h = new HelpFormatter();
		int expected = 0;
		int actual = h.getDescPadding();

		assertEquals(expected, actual);
	}

	@Test
	public void getLeftPadding8() {
		HelpFormatter h = new HelpFormatter();
		int expected = 0;
		int actual = h.getLeftPadding();

		assertEquals(expected, actual);
	}

	@Test
	public void getLongOptPrefix9() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getLongOptPrefix();

		assertNull(actual);
	}

	@Test
	public void getLongOptSeparator10() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getLongOptSeparator();

		assertNull(actual);
	}

	@Test
	public void getNewLine11() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getNewLine();

		assertNull(actual);
	}

	@Test
	public void getOptPrefix12() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getOptPrefix();

		assertNull(actual);
	}

	@Test
	public void getOptionComparator13() {
		HelpFormatter h = new HelpFormatter();
		Comparator<Object> actual = h.getOptionComparator();

		assertNull(actual);
	}

	@Test
	public void getSyntaxPrefix14() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getSyntaxPrefix();

		assertNull(actual);
	}

	@Test
	public void getWidth15() {
		HelpFormatter h = new HelpFormatter();
		int expected = 0;
		int actual = h.getWidth();

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void renderOptions16() {
		HelpFormatter h = new HelpFormatter();
		StringBuffer sb = null;
		int width = 0;
		Options options = null;
		int leftPad = -1;
		int descPad = 0;
		h.renderOptions(sb, width, options, leftPad, descPad);
	}

	@Test
	public void rtrim17() {
		HelpFormatter h = new HelpFormatter();
		String s = null;
		String actual = h.rtrim(s);

		assertNull(actual);
	}

	@Test
	public void rtrim18() {
		HelpFormatter h = new HelpFormatter();
		String s = "";
		String expected = "";
		String actual = h.rtrim(s);

		assertEquals(expected, actual);
	}

	@Test
	public void setArgName19() {
		HelpFormatter h = new HelpFormatter();
		String name = null;
		h.setArgName(name);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setDescPadding20() {
		HelpFormatter h = new HelpFormatter();
		int padding = 0;
		h.setDescPadding(padding);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setLeftPadding21() {
		HelpFormatter h = new HelpFormatter();
		int padding = 0;
		h.setLeftPadding(padding);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setLongOptPrefix22() {
		HelpFormatter h = new HelpFormatter();
		String prefix = null;
		h.setLongOptPrefix(prefix);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setLongOptSeparator23() {
		HelpFormatter h = new HelpFormatter();
		String longOptSeparator = null;
		h.setLongOptSeparator(longOptSeparator);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setNewLine24() {
		HelpFormatter h = new HelpFormatter();
		String newline = null;
		h.setNewLine(newline);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setOptPrefix25() {
		HelpFormatter h = new HelpFormatter();
		String prefix = null;
		h.setOptPrefix(prefix);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setSyntaxPrefix26() {
		HelpFormatter h = new HelpFormatter();
		String prefix = null;
		h.setSyntaxPrefix(prefix);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setWidth27() {
		HelpFormatter h = new HelpFormatter();
		int width = 0;
		h.setWidth(width);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}
}
