package org.jsoup.parser;

import org.junit.*;
import static org.junit.Assert.*;

public class CharacterReaderSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void rangeEquals1() {
		char[] charBuf = null;
		int start = 0;
		int count = 0;
		String cached = null;
		CharacterReader.rangeEquals(charBuf, start, count, cached);
	}

	@Test
	public void rangeEquals2() {
		char[] charBuf = null;
		int start = 0;
		int count = 0;
		String cached = "";
		boolean actual = CharacterReader.rangeEquals(charBuf, start, count, cached);

		assertTrue(actual);
	}

	@Test
	public void rangeEquals3() {
		char[] charBuf = null;
		int start = 0;
		int count = 0;
		String cached = "A";
		boolean actual = CharacterReader.rangeEquals(charBuf, start, count, cached);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void rangeEquals4() {
		char[] charBuf = {};
		int start = 0;
		int count = 1;
		String cached = "A";
		CharacterReader.rangeEquals(charBuf, start, count, cached);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void rangeEquals5() {
		char[] charBuf = {};
		int start = 2147483647;
		int count = 1;
		String cached = "A";
		CharacterReader.rangeEquals(charBuf, start, count, cached);
	}
}
