package org.joda.time;

import org.junit.*;
import static org.junit.Assert.*;

public class LocalDateSymflowerTest {
	@Test
	public void propertyGetField1() {
		LocalDate.Property p = new LocalDate.Property(null, null);
		DateTimeField actual = p.getField();

		assertNull(actual);
	}
}
