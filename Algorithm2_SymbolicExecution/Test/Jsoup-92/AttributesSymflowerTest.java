package org.jsoup.nodes;

import org.junit.*;
import static org.junit.Assert.*;

public class AttributesSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void addAll1() {
		Attributes a = new Attributes();
		a.keys = null;
		a.vals = null;
		Attributes incoming = null;
		a.addAll(incoming);
	}

	@Test
	public void addAll2() {
		Attributes a = new Attributes();
		a.keys = null;
		a.vals = null;
		Attributes incoming = new Attributes();
		incoming.keys = null;
		incoming.vals = null;
		a.addAll(incoming);
	}

	@Test
	public void checkNotNull3() {
		String val = null;
		String expected = "";
		String actual = Attributes.checkNotNull(val);

		assertEquals(expected, actual);
	}

	@Test
	public void checkNotNull4() {
		String val = "";
		String expected = "";
		String actual = Attributes.checkNotNull(val);

		assertEquals(expected, actual);
	}

	@Test
	public void equals5() {
		Attributes a = new Attributes();
		a.keys = null;
		a.vals = null;
		Object o = null;
		boolean actual = a.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals6() {
		Attributes a = new Attributes();
		a.keys = null;
		a.vals = null;
		Object o = new Attributes();
		o.keys = null;
		o.vals = null;
		boolean actual = a.equals(o);

		assertTrue(actual);
	}

	@Test
	public void normalize7() {
		Attributes a = new Attributes();
		a.keys = null;
		a.vals = null;
		a.normalize();
	}

	@Test
	public void size8() {
		Attributes a = new Attributes();
		a.keys = null;
		a.vals = null;
		int expected = 0;
		int actual = a.size();

		assertEquals(expected, actual);
	}
}
