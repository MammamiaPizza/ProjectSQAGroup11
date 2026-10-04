package com.fasterxml.jackson.core.sym;

import org.junit.*;
import static org.junit.Assert.*;

public class ByteQuadsCanonicalizerSymflowerTest {
	@Test
	public void _calcTertiaryShift1() {
		int primarySlots = 0;
		int expected = 4;
		int actual = ByteQuadsCanonicalizer._calcTertiaryShift(primarySlots);

		assertEquals(expected, actual);
	}

	@Test
	public void _calcTertiaryShift2() {
		int primarySlots = 1028;
		int expected = 6;
		int actual = ByteQuadsCanonicalizer._calcTertiaryShift(primarySlots);

		assertEquals(expected, actual);
	}

	@Test
	public void _calcTertiaryShift3() {
		int primarySlots = 256;
		int expected = 5;
		int actual = ByteQuadsCanonicalizer._calcTertiaryShift(primarySlots);

		assertEquals(expected, actual);
	}

	@Test
	public void _calcTertiaryShift4() {
		int primarySlots = 4100;
		int expected = 7;
		int actual = ByteQuadsCanonicalizer._calcTertiaryShift(primarySlots);

		assertEquals(expected, actual);
	}
}
