package org.joda.time.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.joda.time.DurationFieldType;
import org.junit.*;
import static org.junit.Assert.*;

public class UnsupportedDurationFieldSymflowerTest {
	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void add1() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long instant = 0L;
		int value = 0;
		u.add(instant, value);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void add2() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long instant = 0L;
		long value = 0L;
		u.add(instant, value);
	}

	@Test
	public void equals3() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		Object obj = null;
		boolean actual = u.equals(obj);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getDifference4() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long minuendInstant = 0L;
		long subtrahendInstant = 0L;
		u.getDifference(minuendInstant, subtrahendInstant);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getDifferenceAsLong5() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long minuendInstant = 0L;
		long subtrahendInstant = 0L;
		u.getDifferenceAsLong(minuendInstant, subtrahendInstant);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getMillis6() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long value = 0L;
		long instant = 0L;
		u.getMillis(value, instant);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getMillis7() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		int value = 0;
		u.getMillis(value);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getMillis8() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		int value = 0;
		long instant = 0L;
		u.getMillis(value, instant);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getMillis9() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long value = 0L;
		u.getMillis(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getName10() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		u.getName();
	}

	@Test
	public void getType11() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		DurationFieldType actual = u.getType();

		assertNull(actual);
	}

	@Test
	public void getUnitMillis12() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long expected = 0L;
		long actual = u.getUnitMillis();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getValue13() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long duration = 0L;
		long instant = 0L;
		u.getValue(duration, instant);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getValue14() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long duration = 0L;
		u.getValue(duration);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getValueAsLong15() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long duration = 0L;
		long instant = 0L;
		u.getValueAsLong(duration, instant);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void getValueAsLong16() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, UnsupportedOperationException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		long duration = 0L;
		u.getValueAsLong(duration);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void hashCode17() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		u.hashCode();
	}

	@Test
	public void isPrecise18() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		boolean actual = u.isPrecise();

		assertTrue(actual);
	}

	@Test
	public void isSupported19() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		boolean actual = u.isSupported();

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void toString20() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = UnsupportedDurationField.class.getDeclaredConstructor(DurationFieldType.class);
		c.setAccessible(true);
		UnsupportedDurationField u = (UnsupportedDurationField) c.newInstance((Object) null);
		u.toString();
	}
}
