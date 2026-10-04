package org.apache.commons.lang3;

import java.io.OutputStream;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SerializationUtilsSymflowerTest {
	@Test
	public void SerializationUtils1() {
		SerializationUtils expected = new SerializationUtils();
		SerializationUtils actual = new SerializationUtils();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void deserialize2() throws IllegalArgumentException {
		byte[] objectData = null;
		SerializationUtils.deserialize(objectData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serialize3() throws IllegalArgumentException {
		Serializable obj = mock(Serializable.class);
		OutputStream outputStream = null;
		SerializationUtils.serialize(obj, outputStream);
	}
}
