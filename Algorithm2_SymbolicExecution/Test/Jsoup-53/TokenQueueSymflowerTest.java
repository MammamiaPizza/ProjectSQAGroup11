package org.jsoup.parser;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class TokenQueueSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void addFirst1() {
		TokenQueue t = new TokenQueue(null);
		Character c = null;
		t.addFirst(c);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void addFirst2() {
		TokenQueue t = new TokenQueue(null);
		Character c = 0x1;
		t.addFirst(c);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void addFirst3() {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.addFirst(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void advance4() {
		TokenQueue t = new TokenQueue(null);
		t.advance();
	}

	@Test
	public void advance5() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -1073741824);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		t.advance();

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos2 = TokenQueue.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(tExpected, -1073741823);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test
	public void advance6() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		t.advance();
	}

	@Test
	public void advance7() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		t.advance();

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos2 = TokenQueue.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(tExpected, -2147483647);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "A");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test
	public void advance8() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 2147483647);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		t.advance();

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos2 = TokenQueue.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(tExpected, -2147483648);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "A");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chompBalanced9() {
		TokenQueue t = new TokenQueue(null);
		char open = 0x0;
		char close = 0x0;
		t.chompBalanced(open, close);
	}

	@Test
	public void chompBalanced10() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		char open = 0x0;
		char close = 0x0;
		String expected = "";
		String actual = t.chompBalanced(open, close);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chompTo11() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.chompTo(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chompToIgnoreCase12() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.chompToIgnoreCase(seq);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void chompToIgnoreCase13() throws StringIndexOutOfBoundsException {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.chompToIgnoreCase(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consume14() {
		TokenQueue t = new TokenQueue(null);
		t.consume();
	}

	@Test
	public void consume15() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char expected = 'A';
		char actual = t.consume();

		assertEquals(expected, actual);

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(tExpected, 1);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "A");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consume16() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.consume(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consume17() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = null;
		t.consume(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeAttributeKey18() {
		TokenQueue t = new TokenQueue(null);
		t.consumeAttributeKey();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeCssIdentifier19() {
		TokenQueue t = new TokenQueue(null);
		t.consumeCssIdentifier();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeElementSelector20() {
		TokenQueue t = new TokenQueue(null);
		t.consumeElementSelector();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeTagName21() {
		TokenQueue t = new TokenQueue(null);
		t.consumeTagName();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeTo22() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.consumeTo(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToAny23() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = null;
		t.consumeToAny(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToAny24() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -1073741824);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String[] seq = null;
		t.consumeToAny(seq);
	}

	@Test
	public void consumeToAny25() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String[] seq = null;
		String expected = "";
		String actual = t.consumeToAny(seq);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToAny26() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String[] seq = null;
		t.consumeToAny(seq);
	}

	@Test
	public void consumeToAny27() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String[] seq = {};
		String expected = "A";
		String actual = t.consumeToAny(seq);

		assertEquals(expected, actual);

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(tExpected, 1);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "A");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToIgnoreCase28() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.consumeToIgnoreCase(seq);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void consumeToIgnoreCase29() throws StringIndexOutOfBoundsException {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.consumeToIgnoreCase(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeWhitespace30() {
		TokenQueue t = new TokenQueue(null);
		t.consumeWhitespace();
	}

	@Test
	public void consumeWhitespace31() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.consumeWhitespace();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeWord32() {
		TokenQueue t = new TokenQueue(null);
		t.consumeWord();
	}

	@Test
	public void consumeWord33() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String expected = "";
		String actual = t.consumeWord();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEmpty34() {
		TokenQueue t = new TokenQueue(null);
		t.isEmpty();
	}

	@Test
	public void isEmpty35() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.isEmpty();

		assertTrue(actual);
	}

	@Test
	public void isEmpty36() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 536870912);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.isEmpty();

		assertFalse(actual);
	}

	@Test
	public void isEmpty37() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		boolean actual = t.isEmpty();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchChomp38() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.matchChomp(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchChomp39() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = null;
		t.matchChomp(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches40() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.matches(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches41() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = null;
		t.matches(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny42() {
		TokenQueue t = new TokenQueue(null);
		char[] seq = null;
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny43() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "\u0000");
		char[] seq = { 0x0 };
		boolean actual = t.matchesAny(seq);

		assertTrue(actual);
	}

	@Test
	public void matchesAny44() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "\u0001");
		char[] seq = { 0x0 };
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny45() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char[] seq = null;
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny46() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char[] seq = {};
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny47() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char[] seq = null;
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny48() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char[] seq = {};
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test
	public void matchesAny49() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 1);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char[] seq = null;
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny50() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = null;
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny51() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = {};
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny52() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = { null };
		t.matchesAny(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny53() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String[] seq = { null };
		t.matchesAny(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesCS54() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.matchesCS(seq);
	}

	@Test
	public void matchesCS55() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = null;
		boolean actual = t.matchesCS(seq);

		assertFalse(actual);
	}

	@Test
	public void matchesCS56() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 268435456);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String seq = "";
		boolean actual = t.matchesCS(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesStartTag57() {
		TokenQueue t = new TokenQueue(null);
		t.matchesStartTag();
	}

	@Test
	public void matchesStartTag58() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.matchesStartTag();

		assertFalse(actual);
	}

	@Test
	public void matchesStartTag59() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "\u0000A");
		boolean actual = t.matchesStartTag();

		assertFalse(actual);
	}

	@Test
	public void matchesStartTag60() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 1);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		boolean actual = t.matchesStartTag();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesWhitespace61() {
		TokenQueue t = new TokenQueue(null);
		t.matchesWhitespace();
	}

	@Test
	public void matchesWhitespace62() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.matchesWhitespace();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesWord63() {
		TokenQueue t = new TokenQueue(null);
		t.matchesWord();
	}

	@Test
	public void matchesWord64() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.matchesWord();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void remainder65() {
		TokenQueue t = new TokenQueue(null);
		t.remainder();
	}

	@Test
	public void remainder66() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String expected = "A";
		String actual = t.remainder();

		assertEquals(expected, actual);

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(tExpected, 1);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "A");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void toString67() {
		TokenQueue t = new TokenQueue(null);
		t.toString();
	}

	@Test
	public void toString68() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String expected = "A";
		String actual = t.toString();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void unescape69() {
		String in = null;
		TokenQueue.unescape(in);
	}
}
