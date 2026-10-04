package org.apache.commons.compress.archivers.ar;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class ArArchiveOutputStreamSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void close36() throws IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		a.close();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void close37() throws IllegalAccessException, NoSuchFieldException, IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		final Field fieldEntryOffset = ArArchiveOutputStream.class.getDeclaredField("entryOffset");
		fieldEntryOffset.setAccessible(true);
		fieldEntryOffset.set(a, 1L);
		final Field fieldHaveUnclosedEntry = ArArchiveOutputStream.class.getDeclaredField("haveUnclosedEntry");
		fieldHaveUnclosedEntry.setAccessible(true);
		fieldHaveUnclosedEntry.set(a, true);
		final Field fieldPrevEntry = ArArchiveOutputStream.class.getDeclaredField("prevEntry");
		fieldPrevEntry.setAccessible(true);
		fieldPrevEntry.set(a, new ArArchiveEntry(null, 0L, 0, 0, 0, 0L));
		a.close();
	}

	@Test
	public void closeArchiveEntry38() throws IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		a.closeArchiveEntry();

		ArArchiveOutputStream aExpected = new ArArchiveOutputStream(null);

		assertTrue(EqualsBuilder.reflectionEquals(aExpected, a, false, null, true));
	}

	@Test
	public void closeArchiveEntry39() throws IllegalAccessException, NoSuchFieldException, IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		final Field fieldPrevEntry = ArArchiveOutputStream.class.getDeclaredField("prevEntry");
		fieldPrevEntry.setAccessible(true);
		fieldPrevEntry.set(a, new ArArchiveEntry(null, 0L, 0, 0, 0, 0L));
		a.closeArchiveEntry();

		ArArchiveOutputStream aExpected = new ArArchiveOutputStream(null);
		final Field fieldPrevEntry2 = ArArchiveOutputStream.class.getDeclaredField("prevEntry");
		fieldPrevEntry2.setAccessible(true);
		fieldPrevEntry2.set(aExpected, new ArArchiveEntry(null, 0L, 0, 0, 0, 0L));

		assertTrue(EqualsBuilder.reflectionEquals(aExpected, a, false, null, true));
	}

	@Test
	public void closeArchiveEntry40() throws IllegalAccessException, NoSuchFieldException, IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		final Field fieldHaveUnclosedEntry = ArArchiveOutputStream.class.getDeclaredField("haveUnclosedEntry");
		fieldHaveUnclosedEntry.setAccessible(true);
		fieldHaveUnclosedEntry.set(a, true);
		final Field fieldPrevEntry = ArArchiveOutputStream.class.getDeclaredField("prevEntry");
		fieldPrevEntry.setAccessible(true);
		fieldPrevEntry.set(a, new ArArchiveEntry(null, 0L, 0, 0, 0, 0L));
		a.closeArchiveEntry();

		ArArchiveOutputStream aExpected = new ArArchiveOutputStream(null);
		final Field fieldPrevEntry2 = ArArchiveOutputStream.class.getDeclaredField("prevEntry");
		fieldPrevEntry2.setAccessible(true);
		fieldPrevEntry2.set(aExpected, new ArArchiveEntry(null, 0L, 0, 0, 0, 0L));

		assertTrue(EqualsBuilder.reflectionEquals(aExpected, a, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void closeArchiveEntry41() throws IllegalAccessException, NoSuchFieldException, IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		final Field fieldEntryOffset = ArArchiveOutputStream.class.getDeclaredField("entryOffset");
		fieldEntryOffset.setAccessible(true);
		fieldEntryOffset.set(a, 1L);
		final Field fieldHaveUnclosedEntry = ArArchiveOutputStream.class.getDeclaredField("haveUnclosedEntry");
		fieldHaveUnclosedEntry.setAccessible(true);
		fieldHaveUnclosedEntry.set(a, true);
		final Field fieldPrevEntry = ArArchiveOutputStream.class.getDeclaredField("prevEntry");
		fieldPrevEntry.setAccessible(true);
		fieldPrevEntry.set(a, new ArArchiveEntry(null, 0L, 0, 0, 0, 0L));
		a.closeArchiveEntry();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void write42() throws IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		byte[] b = null;
		a.write(b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void write43() throws IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		int b = 0;
		a.write(b);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void write44() throws IOException {
		ArArchiveOutputStream a = new ArArchiveOutputStream(null);
		byte[] b = null;
		int off = 0;
		int len = 0;
		a.write(b, off, len);
	}
}
