package com.fasterxml.jackson.databind.node;

import org.junit.*;
import static org.junit.Assert.*;

public class POJONodeSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void _pojoEquals1() {
		POJONode p = new POJONode(null);
		POJONode other = null;
		p._pojoEquals(other);
	}

	@Test
	public void _pojoEquals2() {
		POJONode p = new POJONode(null);
		POJONode other = new POJONode(null);
		boolean actual = p._pojoEquals(other);

		assertTrue(actual);
	}

	@Test
	public void _pojoEquals3() {
		POJONode p = new POJONode(null);
		POJONode other = new POJONode(new Object());
		boolean actual = p._pojoEquals(other);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void _pojoEquals4() {
		POJONode p = new POJONode(new Object());
		POJONode other = null;
		p._pojoEquals(other);
	}

	@Test
	public void _pojoEquals5() {
		POJONode p = new POJONode(new Object());
		POJONode other = new POJONode(null);
		boolean actual = p._pojoEquals(other);

		assertFalse(actual);
	}

	@Test
	public void _pojoEquals6() {
		POJONode p = new POJONode(new Object());
		POJONode other = new POJONode(new Object());
		boolean actual = p._pojoEquals(other);

		assertTrue(actual);
	}

	@Test
	public void asText7() {
		POJONode p = new POJONode(null);
		String expected = "null";
		String actual = p.asText();

		assertEquals(expected, actual);
	}

	@Test
	public void asText8() {
		POJONode p = new POJONode(null);
		String defaultValue = null;
		String actual = p.asText(defaultValue);

		assertNull(actual);
	}

	@Test
	public void getNodeType9() {
		POJONode p = new POJONode(null);
		JsonNodeType expected = JsonNodeType.POJO;
		JsonNodeType actual = p.getNodeType();

		assertEquals(expected, actual);
	}

	@Test
	public void getPojo10() {
		POJONode p = new POJONode(null);
		Object actual = p.getPojo();

		assertNull(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void hashCode11() {
		POJONode p = new POJONode(null);
		p.hashCode();
	}

	@Test
	public void hashCode12() {
		POJONode p = new POJONode(new Object());
		int expected = 0;
		int actual = p.hashCode();

		assertEquals(expected, actual);
	}
}
