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
		Character c = '\u00ff';
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
		Character open = null;
		Character close = null;
		t.chompBalanced(open, close);
	}

	@Test
	public void chompBalanced10() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		Character open = null;
		Character close = null;
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
		fieldQueue.set(t, "&");
		Character expected = '&';
		Character actual = t.consume();

		assertEquals(expected, actual);

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(tExpected, 1);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "&");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consume16() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.consume(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consume17() {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.consume(seq);
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void consume18() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "\u00c1";
		t.consume(seq);
	}

	@Test
	public void consume19() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "";
		t.consume(seq);

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.IllegalStateException.class)
	public void consume20() throws IllegalAccessException, NoSuchFieldException, IllegalStateException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "A";
		t.consume(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeAttributeKey21() {
		TokenQueue t = new TokenQueue(null);
		t.consumeAttributeKey();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeCssIdentifier22() {
		TokenQueue t = new TokenQueue(null);
		t.consumeCssIdentifier();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeElementSelector23() {
		TokenQueue t = new TokenQueue(null);
		t.consumeElementSelector();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeTagName24() {
		TokenQueue t = new TokenQueue(null);
		t.consumeTagName();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeTo25() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.consumeTo(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToAny26() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = null;
		t.consumeToAny(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeToAny27() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2146238464);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String[] seq = null;
		t.consumeToAny(seq);
	}

	@Test
	public void consumeToAny28() throws IllegalAccessException, NoSuchFieldException {
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
	public void consumeToAny29() throws IllegalAccessException, NoSuchFieldException {
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
	public void consumeToAny30() throws IllegalAccessException, NoSuchFieldException {
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
	public void consumeToIgnoreCase31() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.consumeToIgnoreCase(seq);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void consumeToIgnoreCase32() throws StringIndexOutOfBoundsException {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.consumeToIgnoreCase(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeWhitespace33() {
		TokenQueue t = new TokenQueue(null);
		t.consumeWhitespace();
	}

	@Test
	public void consumeWhitespace34() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.consumeWhitespace();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void consumeWord35() {
		TokenQueue t = new TokenQueue(null);
		t.consumeWord();
	}

	@Test
	public void consumeWord36() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String expected = "";
		String actual = t.consumeWord();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isEmpty37() {
		TokenQueue t = new TokenQueue(null);
		t.isEmpty();
	}

	@Test
	public void isEmpty38() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.isEmpty();

		assertTrue(actual);
	}

	@Test
	public void isEmpty39() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 1073741824);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.isEmpty();

		assertFalse(actual);
	}

	@Test
	public void isEmpty40() throws IllegalAccessException, NoSuchFieldException {
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
	public void matchChomp41() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.matchChomp(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchChomp42() {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.matchChomp(seq);
	}

	@Test
	public void matchChomp43() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "\u00c1";
		boolean actual = t.matchChomp(seq);

		assertFalse(actual);
	}

	@Test
	public void matchChomp44() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "A";
		boolean actual = t.matchChomp(seq);

		assertFalse(actual);
	}

	@Test
	public void matchChomp45() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String seq = "";
		boolean actual = t.matchChomp(seq);

		assertTrue(actual);

		TokenQueue tExpected = new TokenQueue(null);
		final Field fieldQueue2 = TokenQueue.class.getDeclaredField("queue");
		fieldQueue2.setAccessible(true);
		fieldQueue2.set(tExpected, "A");

		assertTrue(EqualsBuilder.reflectionEquals(tExpected, t, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches46() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.matches(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches47() {
		TokenQueue t = new TokenQueue(null);
		String seq = "";
		t.matches(seq);
	}

	@Test
	public void matches48() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "\u00c1";
		boolean actual = t.matches(seq);

		assertFalse(actual);
	}

	@Test
	public void matches49() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "";
		boolean actual = t.matches(seq);

		assertTrue(actual);
	}

	@Test
	public void matches50() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String seq = "A";
		boolean actual = t.matches(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny51() {
		TokenQueue t = new TokenQueue(null);
		char[] seq = null;
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny52() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -1073741824);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		char[] seq = {};
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test
	public void matchesAny53() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		char[] seq = null;
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test
	public void matchesAny54() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "\u0000");
		char[] seq = { 0x0 };
		boolean actual = t.matchesAny(seq);

		assertTrue(actual);
	}

	@Test
	public void matchesAny55() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "\u0002");
		char[] seq = { 0x0 };
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny56() throws IllegalAccessException, NoSuchFieldException {
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
	public void matchesAny57() throws IllegalAccessException, NoSuchFieldException {
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
	public void matchesAny58() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		char[] seq = null;
		t.matchesAny(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny59() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = null;
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny60() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = {};
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny61() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = { null };
		t.matchesAny(seq);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesAny62() {
		TokenQueue t = new TokenQueue(null);
		String[] seq = { "" };
		t.matchesAny(seq);
	}

	@Test
	public void matchesAny63() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String[] seq = { "A" };
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test
	public void matchesAny64() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String[] seq = { "" };
		boolean actual = t.matchesAny(seq);

		assertTrue(actual);
	}

	@Test
	public void matchesAny65() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, -2147483648);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "B");
		String[] seq = { "" };
		boolean actual = t.matchesAny(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesCS66() {
		TokenQueue t = new TokenQueue(null);
		String seq = null;
		t.matchesCS(seq);
	}

	@Test
	public void matchesCS67() throws IllegalAccessException, NoSuchFieldException {
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
	public void matchesCS68() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 139);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String seq = "";
		boolean actual = t.matchesCS(seq);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesStartTag69() {
		TokenQueue t = new TokenQueue(null);
		t.matchesStartTag();
	}

	@Test
	public void matchesStartTag70() throws IllegalAccessException, NoSuchFieldException {
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
	public void matchesStartTag71() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		boolean actual = t.matchesStartTag();

		assertFalse(actual);
	}

	@Test
	public void matchesStartTag72() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "\u00f2A");
		boolean actual = t.matchesStartTag();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesWhitespace73() {
		TokenQueue t = new TokenQueue(null);
		t.matchesWhitespace();
	}

	@Test
	public void matchesWhitespace74() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.matchesWhitespace();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matchesWord75() {
		TokenQueue t = new TokenQueue(null);
		t.matchesWord();
	}

	@Test
	public void matchesWord76() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		boolean actual = t.matchesWord();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void peek77() {
		TokenQueue t = new TokenQueue(null);
		t.peek();
	}

	@Test
	public void peek78() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		Character actual = t.peek();

		assertNull(actual);
	}

	@Test
	public void peek79() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "4");
		Character expected = '4';
		Character actual = t.peek();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void remainder80() {
		TokenQueue t = new TokenQueue(null);
		t.remainder();
	}

	@Test
	public void remainder81() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "");
		String expected = "";
		String actual = t.remainder();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void toString82() {
		TokenQueue t = new TokenQueue(null);
		t.toString();
	}

	@Test
	public void toString83() throws IllegalAccessException, NoSuchFieldException {
		TokenQueue t = new TokenQueue(null);
		final Field fieldPos = TokenQueue.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(t, 1);
		final Field fieldQueue = TokenQueue.class.getDeclaredField("queue");
		fieldQueue.setAccessible(true);
		fieldQueue.set(t, "A");
		String expected = "";
		String actual = t.toString();

		assertEquals(expected, actual);
	}
}
