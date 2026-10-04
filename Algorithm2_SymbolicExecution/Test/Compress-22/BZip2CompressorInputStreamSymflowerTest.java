package org.apache.commons.compress.compressors.bzip2;

import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

public class BZip2CompressorInputStreamSymflowerTest {
	@Test
	public void close1() throws IOException {
		BZip2CompressorInputStream b = new BZip2CompressorInputStream(null);
		b.close();
	}

	@Test
	public void matches2() {
		byte[] signature = null;
		int length = 0;
		boolean actual = BZip2CompressorInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches3() {
		byte[] signature = null;
		int length = 3;
		BZip2CompressorInputStream.matches(signature, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches4() {
		byte[] signature = {};
		int length = 3;
		BZip2CompressorInputStream.matches(signature, length);
	}

	@Test
	public void matches5() {
		byte[] signature = { 0 };
		int length = 3;
		boolean actual = BZip2CompressorInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches6() {
		byte[] signature = { 66 };
		int length = 3;
		BZip2CompressorInputStream.matches(signature, length);
	}

	@Test
	public void matches7() {
		byte[] signature = { 66, 0 };
		int length = 3;
		boolean actual = BZip2CompressorInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches8() {
		byte[] signature = { 66, 90 };
		int length = 3;
		BZip2CompressorInputStream.matches(signature, length);
	}

	@Test
	public void matches9() {
		byte[] signature = { 66, 90, 0 };
		int length = 3;
		boolean actual = BZip2CompressorInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test
	public void matches10() {
		byte[] signature = { 66, 90, 104 };
		int length = 3;
		boolean actual = BZip2CompressorInputStream.matches(signature, length);

		assertTrue(actual);
	}
}
