package org.apache.commons.lang3.reflect;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class ConstructorUtilsSymflowerTest {
	@Test
	public void ConstructorUtils1() {
		ConstructorUtils expected = new ConstructorUtils();
		ConstructorUtils actual = new ConstructorUtils();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
