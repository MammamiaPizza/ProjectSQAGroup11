package org.apache.commons.lang3;

import java.util.Random;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class RandomStringUtilsSymflowerTest {
	@Test
	public void RandomStringUtils1() {
		RandomStringUtils expected = new RandomStringUtils();
		RandomStringUtils actual = new RandomStringUtils();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void random2() throws IllegalArgumentException {
		int count = -1;
		int start = 0;
		int end = 0;
		boolean letters = false;
		boolean numbers = false;
		char[] chars = null;
		Random random = null;
		RandomStringUtils.random(count, start, end, letters, numbers, chars, random);
	}

	@Test
	public void random3() {
		int count = 0;
		int start = 0;
		int end = 0;
		boolean letters = false;
		boolean numbers = false;
		char[] chars = null;
		Random random = null;
		String expected = "";
		String actual = RandomStringUtils.random(count, start, end, letters, numbers, chars, random);

		assertEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void random4() throws IllegalArgumentException {
		int count = 1;
		int start = 0;
		int end = 0;
		boolean letters = false;
		boolean numbers = false;
		char[] chars = {};
		Random random = null;
		RandomStringUtils.random(count, start, end, letters, numbers, chars, random);
	}

	@Test
	public void random5() {
		int count = 0;
		boolean letters = false;
		boolean numbers = false;
		String expected = "";
		String actual = RandomStringUtils.random(count, letters, numbers);

		assertEquals(expected, actual);
	}

	@Test
	public void random6() {
		int count = 0;
		char[] chars = null;
		String expected = "";
		String actual = RandomStringUtils.random(count, chars);

		assertEquals(expected, actual);
	}

	@Test
	public void random7() {
		int count = 0;
		char[] chars = {};
		String expected = "";
		String actual = RandomStringUtils.random(count, chars);

		assertEquals(expected, actual);
	}

	@Test
	public void random8() {
		int count = 0;
		int start = 0;
		int end = 0;
		boolean letters = false;
		boolean numbers = false;
		char[] chars = null;
		String expected = "";
		String actual = RandomStringUtils.random(count, start, end, letters, numbers, chars);

		assertEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void random9() throws IllegalArgumentException {
		int count = -1;
		String chars = null;
		RandomStringUtils.random(count, chars);
	}

	@Test
	public void random10() {
		int count = 0;
		String chars = null;
		String expected = "";
		String actual = RandomStringUtils.random(count, chars);

		assertEquals(expected, actual);
	}

	@Test
	public void random11() {
		int count = 0;
		String expected = "";
		String actual = RandomStringUtils.random(count);

		assertEquals(expected, actual);
	}

	@Test
	public void random12() {
		int count = 0;
		int start = 0;
		int end = 0;
		boolean letters = false;
		boolean numbers = false;
		String expected = "";
		String actual = RandomStringUtils.random(count, start, end, letters, numbers);

		assertEquals(expected, actual);
	}

	@Test
	public void randomAlphabetic13() {
		int count = 0;
		String expected = "";
		String actual = RandomStringUtils.randomAlphabetic(count);

		assertEquals(expected, actual);
	}

	@Test
	public void randomAlphanumeric14() {
		int count = 0;
		String expected = "";
		String actual = RandomStringUtils.randomAlphanumeric(count);

		assertEquals(expected, actual);
	}

	@Test
	public void randomAscii15() {
		int count = 0;
		String expected = "";
		String actual = RandomStringUtils.randomAscii(count);

		assertEquals(expected, actual);
	}

	@Test
	public void randomNumeric16() {
		int count = 0;
		String expected = "";
		String actual = RandomStringUtils.randomNumeric(count);

		assertEquals(expected, actual);
	}
}
