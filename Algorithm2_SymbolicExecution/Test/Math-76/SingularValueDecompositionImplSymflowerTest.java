package org.apache.commons.math.linear;

import java.lang.reflect.Field;
import org.junit.*;
import static org.junit.Assert.*;

public class SingularValueDecompositionImplSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void getConditionNumber1() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getConditionNumber();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void getConditionNumber2() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getConditionNumber();
	}

	@Test
	public void getConditionNumber3() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{ 0.0D });
		double expected = Double.NaN;
		double actual = s.getConditionNumber();

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getCovariance4() throws IllegalAccessException, NoSuchFieldException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldM = SingularValueDecompositionImpl.class.getDeclaredField("m");
		fieldM.setAccessible(true);
		fieldM.set(s, -2147483648);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{ 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507202D });
		double minSingularValue = -8.0D;
		s.getCovariance(minSingularValue);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getCovariance5() {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		double minSingularValue = Double.NaN;
		s.getCovariance(minSingularValue);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getCovariance6() throws IllegalAccessException, NoSuchFieldException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{ 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507202D });
		double minSingularValue = -8.0D;
		s.getCovariance(minSingularValue);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getNorm7() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getNorm();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void getNorm8() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getNorm();
	}

	@Test
	public void getNorm9() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{ 0.0D });
		double expected = 0.0D;
		double actual = s.getNorm();

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getS10() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getS();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getSolver11() throws IllegalAccessException, NoSuchFieldException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldM = SingularValueDecompositionImpl.class.getDeclaredField("m");
		fieldM.setAccessible(true);
		fieldM.set(s, -2147483648);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getSolver();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getSolver12() {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getSolver();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getSolver13() throws IllegalAccessException, NoSuchFieldException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getSolver();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getU14() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldM = SingularValueDecompositionImpl.class.getDeclaredField("m");
		fieldM.setAccessible(true);
		fieldM.set(s, -2147483648);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getU();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getU15() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getU();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getU16() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getU();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getUT17() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getUT();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getUT18() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldN = SingularValueDecompositionImpl.class.getDeclaredField("n");
		fieldN.setAccessible(true);
		fieldN.set(s, 1);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getUT();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getUT19() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldM = SingularValueDecompositionImpl.class.getDeclaredField("m");
		fieldM.setAccessible(true);
		fieldM.set(s, 1073741824);
		final Field fieldN = SingularValueDecompositionImpl.class.getDeclaredField("n");
		fieldN.setAccessible(true);
		fieldN.set(s, 1);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getUT();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getV20() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldM = SingularValueDecompositionImpl.class.getDeclaredField("m");
		fieldM.setAccessible(true);
		fieldM.set(s, -2147483648);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getV();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getV21() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getV();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getV22() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getV();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getVT23() throws InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		s.getVT();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getVT24() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldN = SingularValueDecompositionImpl.class.getDeclaredField("n");
		fieldN.setAccessible(true);
		fieldN.set(s, 1);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getVT();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getVT25() throws IllegalAccessException, NoSuchFieldException, InvalidMatrixException {
		SingularValueDecompositionImpl s = new SingularValueDecompositionImpl(null);
		final Field fieldM = SingularValueDecompositionImpl.class.getDeclaredField("m");
		fieldM.setAccessible(true);
		fieldM.set(s, 1073741824);
		final Field fieldN = SingularValueDecompositionImpl.class.getDeclaredField("n");
		fieldN.setAccessible(true);
		fieldN.set(s, 1);
		final Field fieldSingularValues = SingularValueDecompositionImpl.class.getDeclaredField("singularValues");
		fieldSingularValues.setAccessible(true);
		fieldSingularValues.set(s, new double[]{});
		s.getVT();
	}
}
