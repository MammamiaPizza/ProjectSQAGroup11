package org.jsoup.nodes;

import java.lang.reflect.Field;
import org.junit.*;
import static org.junit.Assert.*;

public class AttributeSymflowerTest {
	@Test
	public void equals1() {
		Attribute a = new Attribute(null, null);
		Object o = null;
		boolean actual = a.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals2() {
		Attribute a = new Attribute(null, null);
		Object o = new Attribute(null, null);
		boolean actual = a.equals(o);

		assertTrue(actual);
	}

	@Test
	public void getKey3() {
		Attribute a = new Attribute(null, null);
		String actual = a.getKey();

		assertNull(actual);
	}

	@Test
	public void getValue4() {
		Attribute a = new Attribute(null, null);
		String actual = a.getValue();

		assertNull(actual);
	}

	@Test
	public void hashCode5() {
		Attribute a = new Attribute(null, null);
		int expected = 0;
		int actual = a.hashCode();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isDataAttribute6() {
		Attribute a = new Attribute(null, null);
		a.isDataAttribute();
	}

	@Test
	public void isDataAttribute7() throws IllegalAccessException, NoSuchFieldException {
		Attribute a = new Attribute(null, null);
		final Field fieldKey = Attribute.class.getDeclaredField("key");
		fieldKey.setAccessible(true);
		fieldKey.set(a, "");
		boolean actual = a.isDataAttribute();

		assertFalse(actual);
	}

	@Test
	public void isDataAttribute8() throws IllegalAccessException, NoSuchFieldException {
		Attribute a = new Attribute(null, null);
		final Field fieldKey = Attribute.class.getDeclaredField("key");
		fieldKey.setAccessible(true);
		fieldKey.set(a, "daAGFH");
		boolean actual = a.isDataAttribute();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isDataAttribute9() {
		String key = null;
		Attribute.isDataAttribute(key);
	}

	@Test
	public void isDataAttribute10() {
		String key = "";
		boolean actual = Attribute.isDataAttribute(key);

		assertFalse(actual);
	}

	@Test
	public void isDataAttribute11() {
		String key = "AHGIJ";
		boolean actual = Attribute.isDataAttribute(key);

		assertFalse(actual);
	}

	@Test
	public void isDataAttribute12() {
		String key = "data-";
		boolean actual = Attribute.isDataAttribute(key);

		assertFalse(actual);
	}

	@Test
	public void isDataAttribute13() {
		String key = "data-A";
		boolean actual = Attribute.isDataAttribute(key);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void setValue14() {
		Attribute a = new Attribute(null, null);
		String val = null;
		a.setValue(val);
	}
}
