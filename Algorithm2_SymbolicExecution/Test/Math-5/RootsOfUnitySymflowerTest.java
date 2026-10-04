package org.apache.commons.math3.complex;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.*;
import static org.junit.Assert.*;

public class RootsOfUnitySymflowerTest {
	@Test
	public void RootsOfUnity37() {
		RootsOfUnity expected = new RootsOfUnity();
		RootsOfUnity actual = new RootsOfUnity();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void getImaginary38() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException, OutOfRangeException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		final Field fieldOmegaImaginaryClockwise = RootsOfUnity.class.getDeclaredField("omegaImaginaryClockwise");
		fieldOmegaImaginaryClockwise.setAccessible(true);
		fieldOmegaImaginaryClockwise.set(r, new double[]{});
		int k = 0;
		r.getImaginary(k);
	}

	@Test
	public void getImaginary39() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException, OutOfRangeException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		final Field fieldOmegaImaginaryClockwise = RootsOfUnity.class.getDeclaredField("omegaImaginaryClockwise");
		fieldOmegaImaginaryClockwise.setAccessible(true);
		fieldOmegaImaginaryClockwise.set(r, new double[]{ 0.0D });
		int k = 0;
		double expected = 0.0D;
		double actual = r.getImaginary(k);

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void getImaginary40() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException, OutOfRangeException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		final Field fieldOmegaImaginaryCounterClockwise = RootsOfUnity.class.getDeclaredField("omegaImaginaryCounterClockwise");
		fieldOmegaImaginaryCounterClockwise.setAccessible(true);
		fieldOmegaImaginaryCounterClockwise.set(r, new double[]{});
		int k = 0;
		r.getImaginary(k);
	}

	@Test
	public void getImaginary41() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException, OutOfRangeException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		final Field fieldOmegaImaginaryCounterClockwise = RootsOfUnity.class.getDeclaredField("omegaImaginaryCounterClockwise");
		fieldOmegaImaginaryCounterClockwise.setAccessible(true);
		fieldOmegaImaginaryCounterClockwise.set(r, new double[]{ 0.0D });
		int k = 0;
		double expected = 0.0D;
		double actual = r.getImaginary(k);

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getImaginary42() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException, OutOfRangeException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 257);
		int k = 0;
		r.getImaginary(k);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getImaginary43() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException, OutOfRangeException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 257);
		int k = 0;
		r.getImaginary(k);
	}

	@Test
	public void getNumberOfRoots44() throws IllegalAccessException, NoSuchFieldException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		int expected = 0;
		int actual = r.getNumberOfRoots();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getReal45() throws IllegalAccessException, NoSuchFieldException, MathIllegalArgumentException, MathIllegalStateException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		int k = 0;
		r.getReal(k);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void getReal46() throws IllegalAccessException, NoSuchFieldException, MathIllegalArgumentException, MathIllegalStateException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		final Field fieldOmegaReal = RootsOfUnity.class.getDeclaredField("omegaReal");
		fieldOmegaReal.setAccessible(true);
		fieldOmegaReal.set(r, new double[]{});
		int k = 0;
		r.getReal(k);
	}

	@Test
	public void getReal47() throws IllegalAccessException, NoSuchFieldException, MathIllegalArgumentException, MathIllegalStateException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		final Field fieldOmegaReal = RootsOfUnity.class.getDeclaredField("omegaReal");
		fieldOmegaReal.setAccessible(true);
		fieldOmegaReal.set(r, new double[]{ 0.0D });
		int k = 0;
		double expected = 0.0D;
		double actual = r.getReal(k);

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test
	public void isCounterClockWise48() throws IllegalAccessException, NoSuchFieldException, MathIllegalStateException {
		RootsOfUnity r = new RootsOfUnity();
		final Field fieldIsCounterClockWise = RootsOfUnity.class.getDeclaredField("isCounterClockWise");
		fieldIsCounterClockWise.setAccessible(true);
		fieldIsCounterClockWise.set(r, false);
		final Field fieldOmegaCount = RootsOfUnity.class.getDeclaredField("omegaCount");
		fieldOmegaCount.setAccessible(true);
		fieldOmegaCount.set(r, 1);
		boolean actual = r.isCounterClockWise();

		assertFalse(actual);
	}
}
