package org.apache.commons.cli;

import java.util.Comparator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class HelpFormatterSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void findWrapPos1() {
		HelpFormatter h = new HelpFormatter();
		String text = null;
		int width = 0;
		int startPos = 0;
		h.findWrapPos(text, width, startPos);
	}

	@Test
	public void findWrapPos2() {
		HelpFormatter h = new HelpFormatter();
		String text = "\t";
		int width = 0;
		int startPos = -1;
		int expected = 1;
		int actual = h.findWrapPos(text, width, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void findWrapPos3() {
		HelpFormatter h = new HelpFormatter();
		String text = "\t\n";
		int width = 0;
		int startPos = -1;
		int expected = 1;
		int actual = h.findWrapPos(text, width, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void findWrapPos4() {
		HelpFormatter h = new HelpFormatter();
		String text = "A";
		int width = 1610612736;
		int startPos = -536870912;
		int expected = -1;
		int actual = h.findWrapPos(text, width, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void getArgName5() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getArgName();

		assertNull(actual);
	}

	@Test
	public void getDescPadding6() {
		HelpFormatter h = new HelpFormatter();
		int expected = 0;
		int actual = h.getDescPadding();

		assertEquals(expected, actual);
	}

	@Test
	public void getLeftPadding7() {
		HelpFormatter h = new HelpFormatter();
		int expected = 0;
		int actual = h.getLeftPadding();

		assertEquals(expected, actual);
	}

	@Test
	public void getLongOptPrefix8() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getLongOptPrefix();

		assertNull(actual);
	}

	@Test
	public void getLongOptSeparator9() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getLongOptSeparator();

		assertNull(actual);
	}

	@Test
	public void getNewLine10() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getNewLine();

		assertNull(actual);
	}

	@Test
	public void getOptPrefix11() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getOptPrefix();

		assertNull(actual);
	}

	@Test
	public void getOptionComparator12() {
		HelpFormatter h = new HelpFormatter();
		Comparator<Object> actual = h.getOptionComparator();

		assertNull(actual);
	}

	@Test
	public void getSyntaxPrefix13() {
		HelpFormatter h = new HelpFormatter();
		String actual = h.getSyntaxPrefix();

		assertNull(actual);
	}

	@Test
	public void getWidth14() {
		HelpFormatter h = new HelpFormatter();
		int expected = 0;
		int actual = h.getWidth();

		assertEquals(expected, actual);
	}

	@Test
	public void rtrim15() {
		HelpFormatter h = new HelpFormatter();
		String s = null;
		String actual = h.rtrim(s);

		assertNull(actual);
	}

	@Test
	public void rtrim16() {
		HelpFormatter h = new HelpFormatter();
		String s = "";
		String expected = "";
		String actual = h.rtrim(s);

		assertEquals(expected, actual);
	}

	@Test
	public void setArgName17() {
		HelpFormatter h = new HelpFormatter();
		String name = null;
		h.setArgName(name);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setDescPadding18() {
		HelpFormatter h = new HelpFormatter();
		int padding = 0;
		h.setDescPadding(padding);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setLeftPadding19() {
		HelpFormatter h = new HelpFormatter();
		int padding = 0;
		h.setLeftPadding(padding);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setLongOptPrefix20() {
		HelpFormatter h = new HelpFormatter();
		String prefix = null;
		h.setLongOptPrefix(prefix);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setLongOptSeparator21() {
		HelpFormatter h = new HelpFormatter();
		String longOptSeparator = null;
		h.setLongOptSeparator(longOptSeparator);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setNewLine22() {
		HelpFormatter h = new HelpFormatter();
		String newline = null;
		h.setNewLine(newline);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setOptPrefix23() {
		HelpFormatter h = new HelpFormatter();
		String prefix = null;
		h.setOptPrefix(prefix);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setSyntaxPrefix24() {
		HelpFormatter h = new HelpFormatter();
		String prefix = null;
		h.setSyntaxPrefix(prefix);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}

	@Test
	public void setWidth25() {
		HelpFormatter h = new HelpFormatter();
		int width = 0;
		h.setWidth(width);

		HelpFormatter hExpected = new HelpFormatter();

		assertTrue(EqualsBuilder.reflectionEquals(hExpected, h, false, null, true));
	}
}
