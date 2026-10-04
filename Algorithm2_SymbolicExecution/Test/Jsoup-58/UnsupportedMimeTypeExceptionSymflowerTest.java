package org.jsoup;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class UnsupportedMimeTypeExceptionSymflowerTest {
	@Test
	public void UnsupportedMimeTypeException9() {
		String message = null;
		String mimeType = null;
		String url = null;
		UnsupportedMimeTypeException expected = new UnsupportedMimeTypeException(null, null, null);
		UnsupportedMimeTypeException actual = new UnsupportedMimeTypeException(message, mimeType, url);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void getMimeType10() {
		UnsupportedMimeTypeException u = new UnsupportedMimeTypeException(null, null, null);
		String actual = u.getMimeType();

		assertNull(actual);
	}

	@Test
	public void getUrl11() {
		UnsupportedMimeTypeException u = new UnsupportedMimeTypeException(null, null, null);
		String actual = u.getUrl();

		assertNull(actual);
	}
}
