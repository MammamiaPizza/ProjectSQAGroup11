package org.apache.commons.csv;

import org.junit.*;

public class CSVFormatSymflowerTest {
	@Test(expected = IllegalArgumentException.class)
	public void newFormat1() throws IllegalArgumentException {
		char delimiter = 0x0;
		CSVFormat.newFormat(delimiter);
	}
}
