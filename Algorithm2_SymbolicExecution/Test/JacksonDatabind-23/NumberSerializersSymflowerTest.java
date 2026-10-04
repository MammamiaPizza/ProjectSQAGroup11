package com.fasterxml.jackson.databind.ser.std;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class NumberSerializersSymflowerTest {
	@Test
	public void NumberSerializers1() {
		NumberSerializers expected = new NumberSerializers();
		NumberSerializers actual = new NumberSerializers();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
