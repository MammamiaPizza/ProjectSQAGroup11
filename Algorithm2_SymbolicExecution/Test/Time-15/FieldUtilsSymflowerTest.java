package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.junit.*;
import static org.junit.Assert.*;

public class FieldUtilsSymflowerTest {
	@Test
	public void equals1() {
		Object object1 = null;
		Object object2 = null;
		boolean actual = FieldUtils.equals(object1, object2);

		assertTrue(actual);
	}

	@Test
	public void equals2() {
		Object object1 = null;
		Object object2 = new Object();
		boolean actual = FieldUtils.equals(object1, object2);

		assertFalse(actual);
	}

	@Test
	public void equals3() {
		Object object1 = new Object();
		Object object2 = null;
		boolean actual = FieldUtils.equals(object1, object2);

		assertFalse(actual);
	}

	@Test
	public void equals4() {
		Object object1 = new Object();
		Object object2 = new Object();
		boolean actual = FieldUtils.equals(object1, object2);

		assertFalse(actual);
	}

	@Test // (expected = ArithmeticException.class)
	public void getWrappedValue5() {
		int value = -1;
		int minValue = -2147483648;
		int maxValue = 2147483647;
		FieldUtils.getWrappedValue(value, minValue, maxValue);
	}

	@Test // (expected = ArithmeticException.class)
	public void getWrappedValue6() {
		int value = 0;
		int minValue = -2147483648;
		int maxValue = 2147483647;
		FieldUtils.getWrappedValue(value, minValue, maxValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getWrappedValue7() throws IllegalArgumentException {
		int value = 0;
		int minValue = 0;
		int maxValue = 0;
		FieldUtils.getWrappedValue(value, minValue, maxValue);
	}

	@Test
	public void getWrappedValue8() {
		int value = 0;
		int minValue = 0;
		int maxValue = 1;
		int expected = 0;
		int actual = FieldUtils.getWrappedValue(value, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test
	public void getWrappedValue9() {
		int value = 0;
		int minValue = 0;
		int maxValue = 2147483647;
		int expected = 0;
		int actual = FieldUtils.getWrappedValue(value, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test
	public void getWrappedValue10() {
		int value = 0;
		int minValue = 1;
		int maxValue = 2;
		int expected = 2;
		int actual = FieldUtils.getWrappedValue(value, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test
	public void getWrappedValue11() {
		int value = 0;
		int minValue = 2;
		int maxValue = 3;
		int expected = 2;
		int actual = FieldUtils.getWrappedValue(value, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArithmeticException.class)
	public void getWrappedValue12() {
		int value = 1;
		int minValue = -2147483648;
		int maxValue = 2147483647;
		FieldUtils.getWrappedValue(value, minValue, maxValue);
	}

	@Test
	public void getWrappedValue13() {
		int currentValue = -1;
		int wrapValue = -2147483648;
		int minValue = 0;
		int maxValue = 1;
		int expected = 1;
		int actual = FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArithmeticException.class)
	public void getWrappedValue14() {
		int currentValue = -2147483648;
		int wrapValue = 0;
		int minValue = -2147483648;
		int maxValue = 2147483647;
		FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);
	}

	@Test
	public void getWrappedValue15() {
		int currentValue = 0;
		int wrapValue = -2147483647;
		int minValue = 0;
		int maxValue = 1;
		int expected = 1;
		int actual = FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArithmeticException.class)
	public void getWrappedValue16() {
		int currentValue = 0;
		int wrapValue = 0;
		int minValue = -2147483648;
		int maxValue = 2147483647;
		FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);
	}

	@Test
	public void getWrappedValue17() {
		int currentValue = 0;
		int wrapValue = 0;
		int minValue = 0;
		int maxValue = 1;
		int expected = 0;
		int actual = FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test
	public void getWrappedValue18() {
		int currentValue = 0;
		int wrapValue = 0;
		int minValue = 0;
		int maxValue = 2147483647;
		int expected = 0;
		int actual = FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test
	public void getWrappedValue19() {
		int currentValue = 0;
		int wrapValue = 0;
		int minValue = 2;
		int maxValue = 3;
		int expected = 2;
		int actual = FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArithmeticException.class)
	public void getWrappedValue20() {
		int currentValue = 0;
		int wrapValue = 1;
		int minValue = -2147483648;
		int maxValue = 2147483647;
		FieldUtils.getWrappedValue(currentValue, wrapValue, minValue, maxValue);
	}

	@Test
	public void safeAdd21() {
		int val1 = 0;
		int val2 = -1;
		int expected = -1;
		int actual = FieldUtils.safeAdd(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeAdd22() {
		int val1 = 0;
		int val2 = 0;
		int expected = 0;
		int actual = FieldUtils.safeAdd(val1, val2);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeAdd23() throws ArithmeticException {
		int val1 = 2147483647;
		int val2 = 896;
		FieldUtils.safeAdd(val1, val2);
	}

	@Test
	public void safeAdd24() {
		long val1 = 0L;
		long val2 = -1L;
		long expected = -1L;
		long actual = FieldUtils.safeAdd(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeAdd25() {
		long val1 = 0L;
		long val2 = 0L;
		long expected = 0L;
		long actual = FieldUtils.safeAdd(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply26() {
		int val1 = 0;
		int val2 = 0;
		int expected = 0;
		int actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeMultiply27() throws ArithmeticException {
		int val1 = 2140881254;
		int val2 = 85731840;
		FieldUtils.safeMultiply(val1, val2);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeMultiply28() throws ArithmeticException {
		int val1 = 2147483640;
		int val2 = -54525888;
		FieldUtils.safeMultiply(val1, val2);
	}

	@Test
	public void safeMultiply29() {
		long val1 = -9223372036854775808L;
		int val2 = -1;
		long expected = -9223372036854775808L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply30() {
		long val1 = 0L;
		int val2 = -1;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply31() {
		long val1 = 0L;
		int val2 = -2;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply32() {
		long val1 = 0L;
		int val2 = 0;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply33() {
		long val1 = 0L;
		int val2 = 1;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply34() {
		long val1 = -1L;
		long val2 = -1L;
		long expected = 1L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeMultiply35() throws ArithmeticException {
		long val1 = -9223372036854775808L;
		long val2 = -1L;
		FieldUtils.safeMultiply(val1, val2);
	}

	@Test
	public void safeMultiply36() {
		long val1 = 0L;
		long val2 = 0L;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply37() {
		long val1 = 0L;
		long val2 = 1L;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply38() {
		long val1 = 1L;
		long val2 = 0L;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiply39() {
		long val1 = 2L;
		long val2 = 0L;
		long expected = 0L;
		long actual = FieldUtils.safeMultiply(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeMultiplyToInt40() {
		long val1 = 0L;
		long val2 = 1L;
		int expected = 0;
		int actual = FieldUtils.safeMultiplyToInt(val1, val2);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeNegate41() throws ArithmeticException {
		int value = -2147483648;
		FieldUtils.safeNegate(value);
	}

	@Test
	public void safeNegate42() {
		int value = 0;
		int expected = 0;
		int actual = FieldUtils.safeNegate(value);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeSubtract43() throws ArithmeticException {
		long val1 = 0L;
		long val2 = -9223372036854775808L;
		FieldUtils.safeSubtract(val1, val2);
	}

	@Test
	public void safeSubtract44() {
		long val1 = 0L;
		long val2 = 0L;
		long expected = 0L;
		long actual = FieldUtils.safeSubtract(val1, val2);

		assertEquals(expected, actual);
	}

	@Test
	public void safeSubtract45() {
		long val1 = 0L;
		long val2 = 1L;
		long expected = -1L;
		long actual = FieldUtils.safeSubtract(val1, val2);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.ArithmeticException.class)
	public void safeToInt46() throws ArithmeticException {
		long value = -9223372036854775808L;
		FieldUtils.safeToInt(value);
	}

	@Test
	public void safeToInt47() {
		long value = 0L;
		int expected = 0;
		int actual = FieldUtils.safeToInt(value);

		assertEquals(expected, actual);
	}

	@Test
	public void verifyValueBounds48() {
		DateTimeField field = null;
		int value = 0;
		int lowerBound = 0;
		int upperBound = 0;
		FieldUtils.verifyValueBounds(field, value, lowerBound, upperBound);
	}

	@Test
	public void verifyValueBounds49() {
		DateTimeFieldType fieldType = null;
		int value = 0;
		int lowerBound = 0;
		int upperBound = 0;
		FieldUtils.verifyValueBounds(fieldType, value, lowerBound, upperBound);
	}

	@Test
	public void verifyValueBounds50() {
		String fieldName = null;
		int value = 0;
		int lowerBound = 0;
		int upperBound = 0;
		FieldUtils.verifyValueBounds(fieldName, value, lowerBound, upperBound);
	}
}
