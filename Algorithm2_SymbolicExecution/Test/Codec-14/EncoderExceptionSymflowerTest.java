package org.apache.commons.codec;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class EncoderExceptionSymflowerTest {
	@Test
	public void EncoderException5() {
		Throwable cause = null;
		EncoderException expected = new EncoderException((String) null);
		EncoderException actual = new EncoderException(cause);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void EncoderException6() {
		String message = null;
		Throwable cause = null;
		EncoderException expected = new EncoderException(null, null);
		EncoderException actual = new EncoderException(message, cause);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void EncoderException7() {
		EncoderException expected = new EncoderException();
		EncoderException actual = new EncoderException();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void EncoderException8() {
		String message = null;
		EncoderException expected = new EncoderException((String) null);
		EncoderException actual = new EncoderException(message);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
