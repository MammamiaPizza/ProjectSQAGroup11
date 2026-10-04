package org.joda.time;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MutableDateTimeSymflowerTest {
	@Test
	public void propertyProperty1() {
		MutableDateTime instant = null;
		DateTimeField field = null;
		MutableDateTime.Property expected = new MutableDateTime.Property(null, null);
		MutableDateTime.Property actual = new MutableDateTime.Property(instant, field);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void propertyGetField2() {
		MutableDateTime.Property p = new MutableDateTime.Property(null, null);
		DateTimeField actual = p.getField();

		assertNull(actual);
	}

	@Test
	public void propertyGetMutableDateTime3() {
		MutableDateTime.Property p = new MutableDateTime.Property(null, null);
		MutableDateTime actual = p.getMutableDateTime();

		assertNull(actual);
	}

	@Test
	public void add4() {
		MutableDateTime m = new MutableDateTime();
		ReadablePeriod period = mock(ReadablePeriod.class);
		int scalar = 0;
		m.add(period, scalar);
	}

	@Test
	public void add5() {
		MutableDateTime m = new MutableDateTime();
		ReadablePeriod period = mock(ReadablePeriod.class);
		m.add(period);
	}

	@Test
	public void getRoundingField6() {
		MutableDateTime m = new MutableDateTime();
		DateTimeField actual = m.getRoundingField();

		assertNull(actual);
	}

	@Test
	public void getRoundingMode7() {
		MutableDateTime m = new MutableDateTime();
		int expected = 0;
		int actual = m.getRoundingMode();

		assertEquals(expected, actual);
	}
}
