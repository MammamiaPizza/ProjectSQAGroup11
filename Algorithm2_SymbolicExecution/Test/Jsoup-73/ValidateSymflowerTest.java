package org.jsoup.helper;

import org.junit.*;

public class ValidateSymflowerTest {
	@Test(expected = IllegalArgumentException.class)
	public void fail11() throws IllegalArgumentException {
		String msg = null;
		Validate.fail(msg);
	}

	@Test
	public void isFalse12() {
		boolean val = false;
		String msg = null;
		Validate.isFalse(val, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFalse13() throws IllegalArgumentException {
		boolean val = true;
		String msg = null;
		Validate.isFalse(val, msg);
	}

	@Test
	public void isFalse14() {
		boolean val = false;
		Validate.isFalse(val);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isFalse15() throws IllegalArgumentException {
		boolean val = true;
		Validate.isFalse(val);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrue16() throws IllegalArgumentException {
		boolean val = false;
		String msg = null;
		Validate.isTrue(val, msg);
	}

	@Test
	public void isTrue17() {
		boolean val = true;
		String msg = null;
		Validate.isTrue(val, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isTrue18() throws IllegalArgumentException {
		boolean val = false;
		Validate.isTrue(val);
	}

	@Test
	public void isTrue19() {
		boolean val = true;
		Validate.isTrue(val);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void noNullElements20() {
		Object[] objects = null;
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test
	public void noNullElements21() {
		Object[] objects = {};
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noNullElements22() throws IllegalArgumentException {
		Object[] objects = { null };
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test
	public void noNullElements23() {
		Object[] objects = { new Object() };
		String msg = null;
		Validate.noNullElements(objects, msg);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void noNullElements24() {
		Object[] objects = null;
		Validate.noNullElements(objects);
	}

	@Test
	public void noNullElements25() {
		Object[] objects = {};
		Validate.noNullElements(objects);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty26() throws IllegalArgumentException {
		String string = null;
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty27() throws IllegalArgumentException {
		String string = "";
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test
	public void notEmpty28() {
		String string = "A";
		String msg = null;
		Validate.notEmpty(string, msg);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty29() throws IllegalArgumentException {
		String string = null;
		Validate.notEmpty(string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notEmpty30() throws IllegalArgumentException {
		String string = "";
		Validate.notEmpty(string);
	}

	@Test
	public void notEmpty31() {
		String string = "A";
		Validate.notEmpty(string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNull32() throws IllegalArgumentException {
		Object obj = null;
		Validate.notNull(obj);
	}

	@Test
	public void notNull33() {
		Object obj = new Object();
		Validate.notNull(obj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notNull34() throws IllegalArgumentException {
		Object obj = null;
		String msg = null;
		Validate.notNull(obj, msg);
	}

	@Test
	public void notNull35() {
		Object obj = new Object();
		String msg = null;
		Validate.notNull(obj, msg);
	}
}
