package org.apache.commons.math3.stat.inference;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.junit.*;
import static org.junit.Assert.*;

public class ChiSquareTestSymflowerTest {
	@Test
	public void ChiSquareTest1() {
		ChiSquareTest expected = new ChiSquareTest();
		ChiSquareTest actual = new ChiSquareTest();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquare2() throws DimensionMismatchException, NotPositiveException, NullArgumentException {
		ChiSquareTest c = new ChiSquareTest();
		long[][] counts = null;
		c.chiSquare(counts);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquare3() throws DimensionMismatchException, NotPositiveException, NullArgumentException {
		ChiSquareTest c = new ChiSquareTest();
		long[][] counts = { null, null };
		c.chiSquare(counts);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquare4() throws DimensionMismatchException, NotPositiveException, NotStrictlyPositiveException {
		ChiSquareTest c = new ChiSquareTest();
		double[] expected = null;
		long[] observed = null;
		c.chiSquare(expected, observed);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquare5() throws DimensionMismatchException, NotPositiveException, NotStrictlyPositiveException {
		ChiSquareTest c = new ChiSquareTest();
		double[] expected = { 0.0D, 0.0D };
		long[] observed = null;
		c.chiSquare(expected, observed);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquareDataSetsComparison6() throws DimensionMismatchException, NotPositiveException, ZeroException {
		ChiSquareTest c = new ChiSquareTest();
		long[] observed1 = null;
		long[] observed2 = null;
		c.chiSquareDataSetsComparison(observed1, observed2);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquareDataSetsComparison7() throws DimensionMismatchException, NotPositiveException, ZeroException {
		ChiSquareTest c = new ChiSquareTest();
		long[] observed1 = { 0L, 0L };
		long[] observed2 = null;
		c.chiSquareDataSetsComparison(observed1, observed2);
	}

	@Test
	public void chiSquareDataSetsComparison8() throws DimensionMismatchException, NotPositiveException, ZeroException {
		ChiSquareTest c = new ChiSquareTest();
		long[] observed1 = { 0L, 1L };
		long[] observed2 = { 1L, 0L };
		double expected = 2.0D;
		double actual = c.chiSquareDataSetsComparison(observed1, observed2);

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test
	public void chiSquareDataSetsComparison9() throws DimensionMismatchException, NotPositiveException, ZeroException {
		ChiSquareTest c = new ChiSquareTest();
		long[] observed1 = { 1L, 1L };
		long[] observed2 = { 0L, 2L };
		double expected = 1.3333333333333333D;
		double actual = c.chiSquareDataSetsComparison(observed1, observed2);

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquareTest10() throws DimensionMismatchException, MaxCountExceededException, NotPositiveException, NullArgumentException, OutOfRangeException {
		ChiSquareTest c = new ChiSquareTest();
		long[][] counts = null;
		double alpha = 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507202D;
		c.chiSquareTest(counts, alpha);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquareTest11() throws DimensionMismatchException, MaxCountExceededException, NotPositiveException, NullArgumentException, OutOfRangeException {
		ChiSquareTest c = new ChiSquareTest();
		long[][] counts = { null, null };
		double alpha = 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858766234D;
		c.chiSquareTest(counts, alpha);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquareTest12() throws DimensionMismatchException, MaxCountExceededException, NotPositiveException, NullArgumentException {
		ChiSquareTest c = new ChiSquareTest();
		long[][] counts = null;
		c.chiSquareTest(counts);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void chiSquareTest13() throws DimensionMismatchException, MaxCountExceededException, NotPositiveException, NullArgumentException {
		ChiSquareTest c = new ChiSquareTest();
		long[][] counts = { null, null };
		c.chiSquareTest(counts);
	}
}
