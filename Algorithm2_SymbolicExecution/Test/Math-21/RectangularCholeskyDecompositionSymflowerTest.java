package org.apache.commons.math3.linear;

import org.junit.*;
import static org.junit.Assert.*;

public class RectangularCholeskyDecompositionSymflowerTest {
	@Test
	public void getRank1() {
		RectangularCholeskyDecomposition r = new RectangularCholeskyDecomposition(null, 0.0D);
		int expected = 0;
		int actual = r.getRank();

		assertEquals(expected, actual);
	}

	@Test
	public void getRootMatrix2() {
		RectangularCholeskyDecomposition r = new RectangularCholeskyDecomposition(null, 0.0D);
		RealMatrix actual = r.getRootMatrix();

		assertNull(actual);
	}
}
