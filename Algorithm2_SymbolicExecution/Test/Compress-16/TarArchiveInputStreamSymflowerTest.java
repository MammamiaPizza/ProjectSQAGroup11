package org.apache.commons.compress.archivers.tar;

import org.junit.*;
import static org.junit.Assert.*;

public class TarArchiveInputStreamSymflowerTest {
	@Test
	public void matches289() {
		byte[] signature = null;
		int length = 0;
		boolean actual = TarArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}
}
