package org.jsoup.helper;

import org.junit.*;

public class ValidateSymflowerTest {
	@Test(expected = IllegalArgumentException.class)
	public void fail32() throws IllegalArgumentException {
		String msg = null;
		Validate.fail(msg);
	}

	@Test
	public void isFalse33() {
		boolean val = false;
		String msg = null;
		Validate.isFalse(val, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFalse34() throws IllegalArgumentException {
		boolean val = true;
		String msg = null;
		Validate.isFalse(val, msg);
	}

	@Test
	public void isFalse35() {
		boolean val = false;
		Validate.isFalse(val);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFalse36() throws IllegalArgumentException {
		boolean val = true;
		Validate.isFalse(val);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrue37() throws IllegalArgumentException {
		boolean val = false;
		String msg = null;
		Validate.isTrue(val, msg);
	}

	@Test
	public void isTrue38() {
		boolean val = true;
		String msg = null;
		Validate.isTrue(val, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrue39() throws IllegalArgumentException {
		boolean val = false;
		Validate.isTrue(val);
	}

	@Test
	public void isTrue40() {
		boolean val = true;
		Validate.isTrue(val);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void noNullElements41() {
		Object[] objects = null;
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test
	public void noNullElements42() {
		Object[] objects = {};
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noNullElements43() throws IllegalArgumentException {
		Object[] objects = { null };
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test
	public void noNullElements44() {
		Object[] objects = { new Object() };
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void noNullElements45() {
		Object[] objects = null;
		Validate.noNullElements(objects);
	}

	@Test
	public void noNullElements46() {
		Object[] objects = {};
		Validate.noNullElements(objects);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty47() throws IllegalArgumentException {
		String string = null;
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty48() throws IllegalArgumentException {
		String string = "";
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test
	public void notEmpty49() {
		String string = "A";
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty50() throws IllegalArgumentException {
		String string = null;
		Validate.notEmpty(string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty51() throws IllegalArgumentException {
		String string = "";
		Validate.notEmpty(string);
	}

	@Test
	public void notEmpty52() {
		String string = "A";
		Validate.notEmpty(string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNull53() throws IllegalArgumentException {
		Object obj = null;
		Validate.notNull(obj);
	}

	@Test
	public void notNull54() {
		Object obj = new Object();
		Validate.notNull(obj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNull55() throws IllegalArgumentException {
		Object obj = null;
		String msg = null;
		Validate.notNull(obj, msg);
	}

	@Test
	public void notNull56() {
		Object obj = new Object();
		String msg = null;
		Validate.notNull(obj, msg);
	}
}
