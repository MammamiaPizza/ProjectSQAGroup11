package org.apache.commons.csv;

import java.io.IOException;
import java.lang.reflect.Field;
import org.junit.*;
import static org.junit.Assert.*;

public class ExtendedBufferedReaderSymflowerTest {
	@Test
	public void getLineNumber75() throws IllegalAccessException, NoSuchFieldException {
		ExtendedBufferedReader e = new ExtendedBufferedReader(null);
		final Field fieldLastChar = ExtendedBufferedReader.class.getDeclaredField("lastChar");
		fieldLastChar.setAccessible(true);
		fieldLastChar.set(e, 0);
		int expected = 0;
		int actual = e.getLineNumber();

		assertEquals(expected, actual);
	}

	@Test
	public void read76() throws IllegalAccessException, NoSuchFieldException, IOException {
		ExtendedBufferedReader e = new ExtendedBufferedReader(null);
		final Field fieldLastChar = ExtendedBufferedReader.class.getDeclaredField("lastChar");
		fieldLastChar.setAccessible(true);
		fieldLastChar.set(e, 0);
		char[] buf = null;
		int offset = 0;
		int length = 0;
		int expected = 0;
		int actual = e.read(buf, offset, length);

		assertEquals(expected, actual);
	}

	@Test
	public void readAgain77() throws IllegalAccessException, NoSuchFieldException {
		ExtendedBufferedReader e = new ExtendedBufferedReader(null);
		final Field fieldLastChar = ExtendedBufferedReader.class.getDeclaredField("lastChar");
		fieldLastChar.setAccessible(true);
		fieldLastChar.set(e, 0);
		int expected = 0;
		int actual = e.readAgain();

		assertEquals(expected, actual);
	}
}
