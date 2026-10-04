package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.*;
import static org.junit.Assert.*;

public class MathArraysSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void checkNonNegative1() throws NotPositiveException {
		long[][] in = null;
		MathArrays.checkNonNegative(in);
	}

	@Test
	public void checkNonNegative2() throws NotPositiveException {
		long[][] in = {};
		MathArrays.checkNonNegative(in);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void checkNonNegative3() throws NotPositiveException {
		long[][] in = { null };
		MathArrays.checkNonNegative(in);
	}

	@Test
	public void checkNonNegative4() throws NotPositiveException {
		long[][] in = { {} };
		MathArrays.checkNonNegative(in);
	}

	@Test
	public void checkNonNegative5() throws NotPositiveException {
		long[][] in = { { 0L } };
		MathArrays.checkNonNegative(in);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void checkNonNegative6() throws NotPositiveException {
		long[] in = null;
		MathArrays.checkNonNegative(in);
	}

	@Test
	public void checkNonNegative7() throws NotPositiveException {
		long[] in = {};
		MathArrays.checkNonNegative(in);
	}

	@Test
	public void checkNonNegative8() throws NotPositiveException {
		long[] in = { 0L };
		MathArrays.checkNonNegative(in);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void checkOrder9() throws NonMonotonicSequenceException {
		double[] val = null;
		MathArrays.checkOrder(val);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void checkOrder10() throws NonMonotonicSequenceException {
		double[] val = {};
		MathArrays.checkOrder(val);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void checkOrder11() throws NonMonotonicSequenceException {
		double[] val = null;
		MathArrays.OrderDirection dir = MathArrays.OrderDirection.DECREASING;
		boolean strict = false;
		boolean abort = false;
		MathArrays.checkOrder(val, dir, strict, abort);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void checkOrder12() throws NonMonotonicSequenceException {
		double[] val = {};
		MathArrays.OrderDirection dir = null;
		boolean strict = false;
		boolean abort = false;
		MathArrays.checkOrder(val, dir, strict, abort);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void checkOrder13() throws NonMonotonicSequenceException {
		double[] val = null;
		MathArrays.OrderDirection dir = MathArrays.OrderDirection.DECREASING;
		boolean strict = false;
		MathArrays.checkOrder(val, dir, strict);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void checkOrder14() throws NonMonotonicSequenceException {
		double[] val = {};
		MathArrays.OrderDirection dir = null;
		boolean strict = false;
		MathArrays.checkOrder(val, dir, strict);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void checkPositive15() throws NotStrictlyPositiveException {
		double[] in = null;
		MathArrays.checkPositive(in);
	}

	@Test
	public void checkPositive16() throws NotStrictlyPositiveException {
		double[] in = {};
		MathArrays.checkPositive(in);
	}

	@Test
	public void checkPositive17() throws NotStrictlyPositiveException {
		double[] in = { Double.NaN };
		MathArrays.checkPositive(in);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void copyOf18() {
		double[] source = null;
		int len = -1;
		MathArrays.copyOf(source, len);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void copyOf19() {
		int[] source = null;
		MathArrays.copyOf(source);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void copyOf20() {
		int[] source = null;
		int len = -1;
		MathArrays.copyOf(source, len);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void copyOf21() {
		double[] source = null;
		MathArrays.copyOf(source);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeAdd22() throws DimensionMismatchException {
		double[] a = null;
		double[] b = null;
		MathArrays.ebeAdd(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeAdd23() throws DimensionMismatchException {
		double[] a = {};
		double[] b = null;
		MathArrays.ebeAdd(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeDivide24() throws DimensionMismatchException {
		double[] a = null;
		double[] b = null;
		MathArrays.ebeDivide(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeDivide25() throws DimensionMismatchException {
		double[] a = {};
		double[] b = null;
		MathArrays.ebeDivide(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeMultiply26() throws DimensionMismatchException {
		double[] a = null;
		double[] b = null;
		MathArrays.ebeMultiply(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeMultiply27() throws DimensionMismatchException {
		double[] a = {};
		double[] b = null;
		MathArrays.ebeMultiply(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeSubtract28() throws DimensionMismatchException {
		double[] a = null;
		double[] b = null;
		MathArrays.ebeSubtract(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ebeSubtract29() throws DimensionMismatchException {
		double[] a = {};
		double[] b = null;
		MathArrays.ebeSubtract(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isMonotonic30() {
		double[] val = null;
		MathArrays.OrderDirection dir = MathArrays.OrderDirection.DECREASING;
		boolean strict = false;
		MathArrays.isMonotonic(val, dir, strict);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void isMonotonic31() {
		double[] val = {};
		MathArrays.OrderDirection dir = null;
		boolean strict = false;
		MathArrays.isMonotonic(val, dir, strict);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void linearCombination32() throws DimensionMismatchException {
		double[] a = null;
		double[] b = null;
		MathArrays.linearCombination(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void linearCombination33() throws DimensionMismatchException {
		double[] a = {};
		double[] b = null;
		MathArrays.linearCombination(a, b);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void linearCombination34() throws DimensionMismatchException {
		double[] a = {};
		double[] b = {};
		MathArrays.linearCombination(a, b);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void linearCombination35() throws DimensionMismatchException {
		double[] a = { -0.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011573874488697087D };
		double[] b = { -147912896823231880000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.0D };
		MathArrays.linearCombination(a, b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void safeNorm36() {
		double[] v = null;
		MathArrays.safeNorm(v);
	}

	@Test
	public void scale37() {
		double val = -0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001305687889416034D;
		double[] arr = { -0.000000000000000000000000000000000000000000000000000000000000000000000000000014684368520177422D };
		double[] expected = { 0.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001917320214051771D };
		double[] actual = MathArrays.scale(val, arr);

		assertArrayEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void scale38() {
		double val = Double.NaN;
		double[] arr = null;
		MathArrays.scale(val, arr);
	}

	@Test
	public void scale39() {
		double val = Double.NaN;
		double[] arr = {};
		double[] expected = {};
		double[] actual = MathArrays.scale(val, arr);

		assertArrayEquals(expected, actual);
	}

	@Test
	public void scaleInPlace40() {
		double val = -12.968780312519115D;
		double[] arr = { 0.000030726442699080536D };
		MathArrays.scaleInPlace(val, arr);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void scaleInPlace41() {
		double val = Double.NaN;
		double[] arr = null;
		MathArrays.scaleInPlace(val, arr);
	}

	@Test
	public void scaleInPlace42() {
		double val = Double.NaN;
		double[] arr = {};
		MathArrays.scaleInPlace(val, arr);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void sortInPlace43() throws DimensionMismatchException, NullArgumentException {
		double[] x = {};
		double[][] yList = null;
		MathArrays.sortInPlace(x, yList);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void sortInPlace44() throws DimensionMismatchException, NullArgumentException {
		double[] x = {};
		MathArrays.OrderDirection dir = MathArrays.OrderDirection.DECREASING;
		double[][] yList = null;
		MathArrays.sortInPlace(x, dir, yList);
	}
}
