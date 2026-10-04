package org.jsoup.helper;

import org.junit.*;

public class ValidateSymflowerTest {
	@Test(expected = IllegalArgumentException.class)
	public void fail28() throws IllegalArgumentException {
		String msg = null;
		Validate.fail(msg);
	}

	@Test
	public void isFalse29() {
		boolean val = false;
		String msg = null;
		Validate.isFalse(val, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFalse30() throws IllegalArgumentException {
		boolean val = true;
		String msg = null;
		Validate.isFalse(val, msg);
	}

	@Test
	public void isFalse31() {
		boolean val = false;
		Validate.isFalse(val);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFalse32() throws IllegalArgumentException {
		boolean val = true;
		Validate.isFalse(val);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrue33() throws IllegalArgumentException {
		boolean val = false;
		String msg = null;
		Validate.isTrue(val, msg);
	}

	@Test
	public void isTrue34() {
		boolean val = true;
		String msg = null;
		Validate.isTrue(val, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrue35() throws IllegalArgumentException {
		boolean val = false;
		Validate.isTrue(val);
	}

	@Test
	public void isTrue36() {
		boolean val = true;
		Validate.isTrue(val);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void noNullElements37() {
		Object[] objects = null;
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test
	public void noNullElements38() {
		Object[] objects = {};
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noNullElements39() throws IllegalArgumentException {
		Object[] objects = { null };
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test
	public void noNullElements40() {
		Object[] objects = { new Object() };
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void noNullElements41() {
		Object[] objects = null;
		Validate.noNullElements(objects);
	}

	@Test
	public void noNullElements42() {
		Object[] objects = {};
		Validate.noNullElements(objects);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty43() throws IllegalArgumentException {
		String string = null;
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty44() throws IllegalArgumentException {
		String string = "";
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test
	public void notEmpty45() {
		String string = "A";
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty46() throws IllegalArgumentException {
		String string = null;
		Validate.notEmpty(string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty47() throws IllegalArgumentException {
		String string = "";
		Validate.notEmpty(string);
	}

	@Test
	public void notEmpty48() {
		String string = "A";
		Validate.notEmpty(string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNull49() throws IllegalArgumentException {
		Object obj = null;
		Validate.notNull(obj);
	}

	@Test
	public void notNull50() {
		Object obj = new Object();
		Validate.notNull(obj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNull51() throws IllegalArgumentException {
		Object obj = null;
		String msg = null;
		Validate.notNull(obj, msg);
	}

	@Test
	public void notNull52() {
		Object obj = new Object();
		String msg = null;
		Validate.notNull(obj, msg);
	}
}
