package org.apache.commons.compress.archivers.cpio;

import org.junit.*;
import static org.junit.Assert.*;

public class CpioArchiveInputStreamSymflowerTest {
	@Test
	public void matches76() {
		byte[] signature = null;
		int length = 0;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void matches77() {
		byte[] signature = null;
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches78() {
		byte[] signature = {};
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches79() {
		byte[] signature = { 0 };
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches80() {
		byte[] signature = { 113 };
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test
	public void matches81() {
		byte[] signature = { -57, 113 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertTrue(actual);
	}

	@Test
	public void matches82() {
		byte[] signature = { 0, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test
	public void matches83() {
		byte[] signature = { 0, 113 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test
	public void matches84() {
		byte[] signature = { 113, -57 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertTrue(actual);
	}

	@Test
	public void matches85() {
		byte[] signature = { 113, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test
	public void matches86() {
		byte[] signature = { 48, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches87() {
		byte[] signature = { 48, 55 };
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test
	public void matches88() {
		byte[] signature = { 48, 55, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches89() {
		byte[] signature = { 48, 55, 48 };
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test
	public void matches90() {
		byte[] signature = { 48, 55, 48, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches91() {
		byte[] signature = { 48, 55, 48, 55 };
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test
	public void matches92() {
		byte[] signature = { 48, 55, 48, 55, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void matches93() {
		byte[] signature = { 48, 55, 48, 55, 48 };
		int length = 6;
		CpioArchiveInputStream.matches(signature, length);
	}

	@Test
	public void matches94() {
		byte[] signature = { 48, 55, 48, 55, 48, 0 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertFalse(actual);
	}

	@Test
	public void matches95() {
		byte[] signature = { 48, 55, 48, 55, 48, 49 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertTrue(actual);
	}

	@Test
	public void matches96() {
		byte[] signature = { 48, 55, 48, 55, 48, 50 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertTrue(actual);
	}

	@Test
	public void matches97() {
		byte[] signature = { 48, 55, 48, 55, 48, 55 };
		int length = 6;
		boolean actual = CpioArchiveInputStream.matches(signature, length);

		assertTrue(actual);
	}
}
