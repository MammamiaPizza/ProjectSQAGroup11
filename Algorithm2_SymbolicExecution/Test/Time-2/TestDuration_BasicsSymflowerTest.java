package org.joda.time;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class TestDuration_BasicsSymflowerTest {
	@Test
	public void mockMutableDurationMockMutableDuration1019() {
		long duration = 0L;
		TestDuration_Basics.MockMutableDuration expected = new TestDuration_Basics.MockMutableDuration(0L);
		TestDuration_Basics.MockMutableDuration actual = new TestDuration_Basics.MockMutableDuration(duration);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void mockMutableDurationSetMillis1020() {
		TestDuration_Basics.MockMutableDuration m = new TestDuration_Basics.MockMutableDuration(0L);
		long duration = 0L;
		m.setMillis(duration);
	}
}
