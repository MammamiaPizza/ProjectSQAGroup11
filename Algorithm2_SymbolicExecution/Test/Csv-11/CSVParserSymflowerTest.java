package org.apache.commons.csv;

import org.junit.*;
import static org.junit.Assert.*;

public class CSVParserSymflowerTest {
	@Test
	public void getRecordNumber1() {
		CSVParser c = new CSVParser(null, null);
		long expected = 0L;
		long actual = c.getRecordNumber();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void isClosed2() {
		CSVParser c = new CSVParser(null, null);
		c.isClosed();
	}
}
