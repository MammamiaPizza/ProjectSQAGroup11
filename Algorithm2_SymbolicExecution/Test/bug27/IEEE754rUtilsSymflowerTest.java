package org.apache.commons.lang3.math;

import org.junit.*;
import static org.junit.Assert.*;

public class IEEE754rUtilsSymflowerTest {
	@Test(expected = IllegalArgumentException.class)
	public void max57() throws IllegalArgumentException {
		float[] array = null;
		IEEE754rUtils.max(array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void max58() throws IllegalArgumentException {
		float[] array = {};
		IEEE754rUtils.max(array);
	}

	@Test
	public void max59() {
		float[] array = { 0.0F };
		float expected = 0.0F;
		float actual = IEEE754rUtils.max(array);

		assertEquals(expected, actual, 0.0000001F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void max60() throws IllegalArgumentException {
		double[] array = null;
		IEEE754rUtils.max(array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void max61() throws IllegalArgumentException {
		double[] array = {};
		IEEE754rUtils.max(array);
	}

	@Test
	public void max62() {
		double[] array = { 0.0D };
		double expected = 0.0D;
		double actual = IEEE754rUtils.max(array);

		assertEquals(expected, actual, 0.0000001D);
	}

	@Test(expected = IllegalArgumentException.class)
	public void min63() throws IllegalArgumentException {
		float[] array = null;
		IEEE754rUtils.min(array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void min64() throws IllegalArgumentException {
		float[] array = {};
		IEEE754rUtils.min(array);
	}

	@Test
	public void min65() {
		float[] array = { 0.0F };
		float expected = 0.0F;
		float actual = IEEE754rUtils.min(array);

		assertEquals(expected, actual, 0.0000001F);
	}

	@Test(expected = IllegalArgumentException.class)
	public void min66() throws IllegalArgumentException {
		double[] array = null;
		IEEE754rUtils.min(array);
	}

	@Test(expected = IllegalArgumentException.class)
	public void min67() throws IllegalArgumentException {
		double[] array = {};
		IEEE754rUtils.min(array);
	}

	@Test
	public void min68() {
		double[] array = { 0.0D };
		double expected = 0.0D;
		double actual = IEEE754rUtils.min(array);

		assertEquals(expected, actual, 0.0000001D);
	}
}
