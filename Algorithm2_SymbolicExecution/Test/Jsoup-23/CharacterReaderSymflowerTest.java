package org.jsoup.parser;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class CharacterReaderSymflowerTest {
	@Test
	public void advance1() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		c.advance();

		CharacterReader cExpected = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void advance2() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, 2147483647);
		c.advance();

		CharacterReader cExpected = new CharacterReader(null);
		final Field fieldPos2 = CharacterReader.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(cExpected, -2147483648);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consume3() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.consume();
	}

	@Test
	public void consume4() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		char expected = 0xFFFF;
		char actual = c.consume();

		assertEquals(expected, actual);

		CharacterReader cExpected = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(cExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void consume5() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, 2147483647);
		char expected = 0xFFFF;
		char actual = c.consume();

		assertEquals(expected, actual);

		CharacterReader cExpected = new CharacterReader(null);
		final Field fieldPos2 = CharacterReader.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(cExpected, -2147483648);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeAsString6() {
		CharacterReader c = new CharacterReader(null);
		c.consumeAsString();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeDigitSequence7() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.consumeDigitSequence();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeDigitSequence8() {
		CharacterReader c = new CharacterReader(null);
		c.consumeDigitSequence();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeHexSequence9() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.consumeHexSequence();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeHexSequence10() {
		CharacterReader c = new CharacterReader(null);
		c.consumeHexSequence();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeLetterSequence11() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.consumeLetterSequence();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeLetterSequence12() {
		CharacterReader c = new CharacterReader(null);
		c.consumeLetterSequence();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeTo13() {
		CharacterReader c2 = new CharacterReader(null);
		char c = 0x0;
		c2.consumeTo(c);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeTo14() {
		CharacterReader c = new CharacterReader(null);
		String seq = null;
		c.consumeTo(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToEnd15() {
		CharacterReader c = new CharacterReader(null);
		c.consumeToEnd();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void containsIgnoreCase16() {
		CharacterReader c = new CharacterReader(null);
		String seq = null;
		c.containsIgnoreCase(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void current17() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.current();
	}

	@Test
	public void current18() {
		CharacterReader c = new CharacterReader(null);
		char expected = 0xFFFF;
		char actual = c.current();

		assertEquals(expected, actual);
	}

	@Test
	public void isEmpty19() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		boolean actual = c.isEmpty();

		assertFalse(actual);
	}

	@Test
	public void isEmpty20() {
		CharacterReader c = new CharacterReader(null);
		boolean actual = c.isEmpty();

		assertTrue(actual);
	}

	@Test
	public void mark21() {
		CharacterReader c = new CharacterReader(null);
		c.mark();

		CharacterReader cExpected = new CharacterReader(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchConsume22() {
		CharacterReader c = new CharacterReader(null);
		String seq = null;
		c.matchConsume(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchConsumeIgnoreCase23() {
		CharacterReader c = new CharacterReader(null);
		String seq = null;
		c.matchConsumeIgnoreCase(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches24() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c2 = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c2, -2147483648);
		char c = 0x0;
		c2.matches(c);
	}

	@Test
	public void matches25() {
		CharacterReader c2 = new CharacterReader(null);
		char c = 0x0;
		boolean actual = c2.matches(c);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches26() {
		CharacterReader c = new CharacterReader(null);
		String seq = null;
		c.matches(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny27() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		char[] seq = null;
		c.matchesAny(seq);
	}

	@Test
	public void matchesAny28() {
		CharacterReader c = new CharacterReader(null);
		char[] seq = null;
		boolean actual = c.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesDigit29() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.matchesDigit();
	}

	@Test
	public void matchesDigit30() {
		CharacterReader c = new CharacterReader(null);
		boolean actual = c.matchesDigit();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesIgnoreCase31() {
		CharacterReader c = new CharacterReader(null);
		String seq = null;
		c.matchesIgnoreCase(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesLetter32() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.matchesLetter();
	}

	@Test
	public void matchesLetter33() {
		CharacterReader c = new CharacterReader(null);
		boolean actual = c.matchesLetter();

		assertFalse(actual);
	}

	@Test
	public void pos34() {
		CharacterReader c = new CharacterReader(null);
		int expected = 0;
		int actual = c.pos();

		assertEquals(expected, actual);
	}

	@Test
	public void rewindToMark35() {
		CharacterReader c = new CharacterReader(null);
		c.rewindToMark();

		CharacterReader cExpected = new CharacterReader(null);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void toString36() {
		CharacterReader c = new CharacterReader(null);
		c.toString();
	}

	@Test
	public void unconsume37() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(c, -2147483648);
		c.unconsume();

		CharacterReader cExpected = new CharacterReader(null);
		final Field fieldPos2 = CharacterReader.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(cExpected, 2147483647);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void unconsume38() throws IllegalAccessException, NoSuchFieldException {
		CharacterReader c = new CharacterReader(null);
		c.unconsume();

		CharacterReader cExpected = new CharacterReader(null);
		final Field fieldPos = CharacterReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(cExpected, -1);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}
}
