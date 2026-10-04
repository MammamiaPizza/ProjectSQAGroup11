package org.apache.commons.codec.binary;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StringUtilsSymflowerTest {
	@Test
	public void equals1() {
		CharSequence cs1 = mock(CharSequence.class);
		CharSequence cs2 = mock(CharSequence.class);
		boolean actual = StringUtils.equals(cs1, cs2);

		assertTrue(actual);
	}

	@Test
	public void getBytesUnchecked2() {
		String string = null;
		String charsetName = null;
		byte[] actual = StringUtils.getBytesUnchecked(string, charsetName);

		assertNull(actual);
	}

	@Test
	public void newString3() {
		byte[] bytes = null;
		String charsetName = null;
		String actual = StringUtils.newString(bytes, charsetName);

		assertNull(actual);
	}
}
