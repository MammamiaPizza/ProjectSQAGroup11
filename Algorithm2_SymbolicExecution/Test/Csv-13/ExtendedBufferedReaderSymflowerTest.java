package org.apache.commons.csv;

import java.lang.reflect.Field;
import org.junit.*;
import static org.junit.Assert.*;

public class ExtendedBufferedReaderSymflowerTest {
	@Test
	public void getLastChar7() throws IllegalAccessException, NoSuchFieldException {
		ExtendedBufferedReader e = new ExtendedBufferedReader(null);
		final Field fieldLastChar = ExtendedBufferedReader.class.getDeclaredField("lastChar");
		fieldLastChar.setAccessible(true);
		fieldLastChar.set(e, 0);
		int expected = 0;
		int actual = e.getLastChar();

		assertEquals(expected, actual);
	}

	@Test
	public void getPosition8() throws IllegalAccessException, NoSuchFieldException {
		ExtendedBufferedReader e = new ExtendedBufferedReader(null);
		final Field fieldLastChar = ExtendedBufferedReader.class.getDeclaredField("lastChar");
		fieldLastChar.setAccessible(true);
		fieldLastChar.set(e, 0);
		long expected = 0L;
		long actual = e.getPosition();

		assertEquals(expected, actual);
	}

	@Test
	public void isClosed9() throws IllegalAccessException, NoSuchFieldException {
		ExtendedBufferedReader e = new ExtendedBufferedReader(null);
		final Field fieldLastChar = ExtendedBufferedReader.class.getDeclaredField("lastChar");
		fieldLastChar.setAccessible(true);
		fieldLastChar.set(e, 0);
		boolean actual = e.isClosed();

		assertFalse(actual);
	}
}
