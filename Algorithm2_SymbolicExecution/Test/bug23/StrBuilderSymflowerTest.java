package org.apache.commons.lang3.text;

import java.io.Reader;
import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StrBuilderSymflowerTest {
	@Test
	public void StrBuilder1() {
		int initialCapacity = 0;
		StrBuilder expected = new StrBuilder(0);
		expected.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		StrBuilder actual = new StrBuilder(initialCapacity);

		assertEquals(expected, actual);
	}

	@Test
	public void StrBuilder2() {
		int initialCapacity = 1;
		StrBuilder expected = new StrBuilder(0);
		expected.buffer = new char[]{ 0x0 };
		StrBuilder actual = new StrBuilder(initialCapacity);

		assertEquals(expected, actual);
	}

	@Test
	public void StrBuilder3() {
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		StrBuilder actual = new StrBuilder();

		assertEquals(expected, actual);
	}

	@Test
	public void StrBuilder4() {
		String str = null;
		StrBuilder expected = new StrBuilder(null);
		expected.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		StrBuilder actual = new StrBuilder(str);

		assertEquals(expected, actual);
	}

	@Test
	public void StrBuilder5() {
		String str = "";
		StrBuilder expected = new StrBuilder(null);
		expected.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		StrBuilder actual = new StrBuilder(str);

		assertEquals(expected, actual);
	}

	@Test
	public void strBuilderReaderStrBuilderReader6() {
		StrBuilder outerClass2 = new StrBuilder();
		StrBuilder.StrBuilderReader expected = outerClass2.new StrBuilderReader();
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader actual = outerClass.new StrBuilderReader();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void strBuilderReaderClose7() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		s.close();
	}

	@Test
	public void strBuilderReaderMark8() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		int readAheadLimit = 0;
		s.mark(readAheadLimit);

		StrBuilder outerClass2 = new StrBuilder();
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderReaderMarkSupported9() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		boolean actual = s.markSupported();

		assertTrue(actual);
	}

	@Test // (expected = java.lang.IndexOutOfBoundsException.class)
	public void strBuilderReaderRead10() throws IndexOutOfBoundsException {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = null;
		int off = -1;
		int len = 0;
		s.read(b, off, len);
	}

	@Test // (expected = java.lang.IndexOutOfBoundsException.class)
	public void strBuilderReaderRead11() throws IndexOutOfBoundsException {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = null;
		int off = 0;
		int len = -1;
		s.read(b, off, len);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void strBuilderReaderRead12() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = null;
		int off = 0;
		int len = 0;
		s.read(b, off, len);
	}

	@Test
	public void strBuilderReaderRead13() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = {};
		int off = 0;
		int len = 0;
		int expected = 0;
		int actual = s.read(b, off, len);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.IndexOutOfBoundsException.class)
	public void strBuilderReaderRead14() throws IndexOutOfBoundsException {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = {};
		int off = 0;
		int len = 1;
		s.read(b, off, len);
	}

	@Test // (expected = java.lang.IndexOutOfBoundsException.class)
	public void strBuilderReaderRead15() throws IndexOutOfBoundsException {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = {};
		int off = 1;
		int len = 0;
		s.read(b, off, len);
	}

	@Test
	public void strBuilderReaderRead16() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = { 0x0 };
		int off = 0;
		int len = 1;
		int expected = -1;
		int actual = s.read(b, off, len);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.IndexOutOfBoundsException.class)
	public void strBuilderReaderRead17() throws IndexOutOfBoundsException {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		char[] b = { 0x0 };
		int off = 1;
		int len = 2147483647;
		s.read(b, off, len);
	}

	@Test
	public void strBuilderReaderRead18() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		int expected = -1;
		int actual = s.read();

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void strBuilderReaderRead19() {
		StrBuilder outerClass = new StrBuilder();
		outerClass.buffer = new char[]{};
		outerClass.size = 1;
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		s.read();
	}

	@Test
	public void strBuilderReaderRead20() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder outerClass = new StrBuilder();
		outerClass.buffer = new char[]{ 0x0 };
		outerClass.size = 1;
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		int expected = 0;
		int actual = s.read();

		assertEquals(expected, actual);

		StrBuilder outerClass2 = new StrBuilder();
		outerClass2.buffer = new char[]{ 0x0 };
		outerClass2.size = 1;
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();
		final Field fieldPos = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(sExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderReaderReady21() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		final Field fieldPos = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(s, -2147483648);
		boolean actual = s.ready();

		assertTrue(actual);
	}

	@Test
	public void strBuilderReaderReady22() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		boolean actual = s.ready();

		assertFalse(actual);
	}

	@Test
	public void strBuilderReaderReset23() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		s.reset();

		StrBuilder outerClass2 = new StrBuilder();
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderReaderSkip24() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder outerClass = new StrBuilder();
		outerClass.size = -2147483645;
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		final Field fieldPos = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(s, 2130706432);
		long n = 0L;
		long expected = 16777219L;
		long actual = s.skip(n);

		assertEquals(expected, actual);

		StrBuilder outerClass2 = new StrBuilder();
		outerClass2.size = -2147483645;
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();
		final Field fieldPos2 = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(sExpected, -2147483645);

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderReaderSkip25() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		long n = -1L;
		long expected = 0L;
		long actual = s.skip(n);

		assertEquals(expected, actual);
	}

	@Test
	public void strBuilderReaderSkip26() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		long n = 0L;
		long expected = 0L;
		long actual = s.skip(n);

		assertEquals(expected, actual);

		StrBuilder outerClass2 = new StrBuilder();
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderReaderSkip27() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder outerClass = new StrBuilder();
		outerClass.size = 1;
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		final Field fieldPos = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(s, 2147483647);
		long n = 9223372034707292161L;
		long expected = 9223372034707292161L;
		long actual = s.skip(n);

		assertEquals(expected, actual);

		StrBuilder outerClass2 = new StrBuilder();
		outerClass2.size = 1;
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderReaderSkip28() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder outerClass = new StrBuilder();
		outerClass.size = 67848;
		StrBuilder.StrBuilderReader s = outerClass.new StrBuilderReader();
		final Field fieldPos = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos.setAccessible(true);
		fieldPos.set(s, 67848);
		long n = 1L;
		long expected = 0L;
		long actual = s.skip(n);

		assertEquals(expected, actual);

		StrBuilder outerClass2 = new StrBuilder();
		outerClass2.size = 67848;
		StrBuilder.StrBuilderReader sExpected = outerClass2.new StrBuilderReader();
		final Field fieldPos2 = StrBuilder.StrBuilderReader.class.getDeclaredField("pos");
		fieldPos2.setAccessible(true);
		fieldPos2.set(sExpected, 67848);

		assertTrue(EqualsBuilder.reflectionEquals(sExpected, s, false, null, true));
	}

	@Test
	public void strBuilderWriterClose29() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		s.close();
	}

	@Test
	public void strBuilderWriterFlush30() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		s.flush();
	}

	@Test
	public void strBuilderWriterWrite31() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		String str = null;
		int off = 0;
		int len = 0;
		s.write(str, off, len);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void strBuilderWriterWrite32() {
		StrBuilder outerClass = new StrBuilder();
		outerClass.buffer = new char[]{};
		outerClass.size = -4;
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		int c = 0;
		s.write(c);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void strBuilderWriterWrite33() {
		StrBuilder outerClass = new StrBuilder();
		outerClass.buffer = new char[]{};
		outerClass.size = 1073741823;
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		int c = 0;
		s.write(c);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void strBuilderWriterWrite34() {
		StrBuilder outerClass = new StrBuilder();
		outerClass.buffer = new char[]{};
		outerClass.size = 2147483647;
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		int c = 0;
		s.write(c);
	}

	@Test
	public void strBuilderWriterWrite35() {
		StrBuilder outerClass = new StrBuilder();
		outerClass.buffer = new char[]{ '\u96d3' };
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		int c = 0;
		s.write(c);
	}

	@Test
	public void strBuilderWriterWrite36() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		char[] cbuf = null;
		int off = 0;
		int len = 0;
		s.write(cbuf, off, len);
	}

	@Test
	public void strBuilderWriterWrite37() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		char[] cbuf = {};
		s.write(cbuf);
	}

	@Test
	public void strBuilderWriterWrite38() {
		StrBuilder outerClass = new StrBuilder();
		StrBuilder.StrBuilderWriter s = outerClass.new StrBuilderWriter();
		String str = "";
		s.write(str);
	}

	@Test
	public void append39() {
		StrBuilder s = new StrBuilder();
		StrBuilder str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append40() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		str.size = -2147483648;
		int startIndex = 0;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append41() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		int startIndex = -1;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append42() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		int startIndex = 0;
		int length = -1;
		s.append(str, startIndex, length);
	}

	@Test
	public void append43() {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test
	public void append44() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		StrBuilder str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append45() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073743424;
		StrBuilder str = new StrBuilder();
		str.size = 1;
		int startIndex = 0;
		int length = 1;
		s.append(str, startIndex, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append46() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1342177031;
		StrBuilder str = null;
		int startIndex = 0;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append47() {
		StrBuilder s = new StrBuilder();
		float value = 0.000000000000000000000000000000000000000048608F;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append48() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		float value = Float.NaN;
		s.append(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append49() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2147475263;
		float value = -3431756000.0F;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append50() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		s.append(ch);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append51() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char ch = 0x0;
		s.append(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void append52() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -4;
		char ch = 0x0;
		s.append(ch);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append53() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		char ch = 0x0;
		s.append(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void append54() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2147483647;
		char ch = 0x0;
		s.append(ch);
	}

	@Test
	public void append55() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u21ca' };
		char ch = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		StrBuilder actual = s.append(ch);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0 };

		assertEquals(sExpected, s);
	}

	@Test
	public void append56() {
		StrBuilder s = new StrBuilder();
		StringBuffer str = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test
	public void append57() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		StringBuffer str = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append58() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		StringBuffer str = null;
		s.append(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append59() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		StringBuffer str = null;
		s.append(str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append60() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073741823;
		StringBuffer str = null;
		s.append(str);
	}

	@Test
	public void append61() {
		StrBuilder s = new StrBuilder();
		Object obj = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(obj);

		assertEquals(expected, actual);
	}

	@Test
	public void append62() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		Object obj = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(obj);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append63() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		Object obj = null;
		s.append(obj);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append64() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		Object obj = null;
		s.append(obj);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append65() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073741823;
		Object obj = null;
		s.append(obj);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append66() {
		StrBuilder s = new StrBuilder();
		double value = 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000382842267379708D;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append67() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		double value = -36929516970342285000.0D;
		s.append(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append68() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1082212416;
		double value = 36893488147419103000.0D;
		s.append(value);
	}

	@Test
	public void append69() {
		StrBuilder s = new StrBuilder();
		String str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append70() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = -1;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test
	public void append71() {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append72() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = 1;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append73() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		String str = "A";
		int startIndex = 0;
		int length = -1;
		s.append(str, startIndex, length);
	}

	@Test
	public void append74() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		String str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append75() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073742207;
		String str = null;
		int startIndex = 0;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append76() {
		StrBuilder s = new StrBuilder();
		int value = 0;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append77() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int value = 0;
		s.append(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append78() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2096619265;
		int value = 0;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append79() {
		StrBuilder s = new StrBuilder();
		s.size = -1610612736;
		char[] chars = { 0x0 };
		int startIndex = 1;
		int length = 2147483647;
		s.append(chars, startIndex, length);
	}

	@Test
	public void append80() {
		StrBuilder s = new StrBuilder();
		char[] chars = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(chars, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append81() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = -1;
		int length = 0;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append82() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 0;
		int length = -1;
		s.append(chars, startIndex, length);
	}

	@Test
	public void append83() {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(chars, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append84() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 0;
		int length = 1;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append85() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 1;
		int length = 0;
		s.append(chars, startIndex, length);
	}

	@Test
	public void append86() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		char[] chars = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(chars, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append87() {
		StrBuilder s = new StrBuilder();
		s.size = 2;
		char[] chars = { 0x0 };
		int startIndex = 0;
		int length = 1;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append88() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char[] chars = { 0x0 };
		int startIndex = 0;
		int length = 1;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append89() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		char[] chars = null;
		int startIndex = 0;
		int length = 0;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append90() {
		StrBuilder s = new StrBuilder();
		s.size = 64;
		char[] chars = { 0x0 };
		int startIndex = 1;
		int length = 2147483647;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append91() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		char[] chars = { 0x0 };
		int startIndex = 0;
		int length = 1;
		s.append(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append92() {
		StrBuilder s = new StrBuilder();
		boolean value = false;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append93() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483644;
		boolean value = false;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append94() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483646;
		boolean value = true;
		s.append(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void append95() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -10;
		boolean value = false;
		s.append(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void append96() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -16;
		boolean value = true;
		s.append(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append97() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741819;
		boolean value = false;
		s.append(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void append98() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2147483644;
		boolean value = false;
		s.append(value);
	}

	@Test
	public void append99() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 't', 't', 't' };
		boolean value = true;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 'e', 't', 't', 't' };
		StrBuilder actual = s.append(value);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 'e', 't', 't', 't' };

		assertEquals(sExpected, s);
	}

	@Test
	public void append100() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 'f', 'f', 'f', 'f' };
		boolean value = false;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 'e', 'f', 'f', 'f', 'f' };
		StrBuilder actual = s.append(value);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 'e', 'f', 'f', 'f', 'f' };

		assertEquals(sExpected, s);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void append101() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2147483646;
		boolean value = true;
		s.append(value);
	}

	@Test
	public void append102() {
		StrBuilder s = new StrBuilder();
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void append103() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StringBuffer str = new StringBuffer();
		int startIndex = -1;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test
	public void append104() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append105() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append106() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append107() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073742175;
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.append(str, startIndex, length);
	}

	@Test
	public void append108() {
		StrBuilder s = new StrBuilder();
		char[] chars = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(chars);

		assertEquals(expected, actual);
	}

	@Test
	public void append109() {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(chars);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append110() {
		StrBuilder s = new StrBuilder();
		char[] chars = { 0x0 };
		s.append(chars);
	}

	@Test
	public void append111() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		char[] chars = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(chars);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append112() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char[] chars = { 0x0 };
		s.append(chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append113() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		char[] chars = null;
		s.append(chars);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append114() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		char[] chars = { 0x0 };
		s.append(chars);
	}

	@Test
	public void append115() {
		StrBuilder s = new StrBuilder();
		StrBuilder str = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test
	public void append116() {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append117() {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		str.size = 1;
		s.append(str);
	}

	@Test
	public void append118() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		StrBuilder str = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append119() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		StrBuilder str = new StrBuilder();
		str.size = 1;
		s.append(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append120() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		StrBuilder str = null;
		s.append(str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append121() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		StrBuilder str = new StrBuilder();
		str.size = 1;
		s.append(str);
	}

	@Test
	public void append122() {
		StrBuilder s = new StrBuilder();
		CharSequence seq = mock(CharSequence.class);
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(seq);

		assertEquals(expected, actual);
	}

	@Test
	public void append123() {
		StrBuilder s = new StrBuilder();
		CharSequence seq = mock(CharSequence.class);
		String parameter = "";
		when(seq.toString()).thenReturn(parameter);
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(seq);

		assertEquals(expected, actual);

		verify(seq, times(1)).toString();
	}

	@Test
	public void append124() {
		StrBuilder s = new StrBuilder();
		CharSequence seq = mock(CharSequence.class);
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(seq, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test
	public void append125() {
		StrBuilder s = new StrBuilder();
		CharSequence seq = mock(CharSequence.class);
		when(seq.toString()).thenReturn(null);
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(seq, startIndex, length);

		assertEquals(expected, actual);

		verify(seq, times(1)).toString();
	}

	@Test
	public void append126() {
		StrBuilder s = new StrBuilder();
		String str = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test
	public void append127() {
		StrBuilder s = new StrBuilder();
		String str = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append128() {
		StrBuilder s = new StrBuilder();
		String str = "A";
		s.append(str);
	}

	@Test
	public void append129() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		String str = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.append(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append130() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		String str = "A";
		s.append(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append131() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		String str = null;
		s.append(str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append132() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		String str = "A";
		s.append(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append133() {
		StrBuilder s = new StrBuilder();
		long value = 0L;
		s.append(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void append134() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		long value = 0L;
		s.append(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void append135() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0 };
		s.size = 2144870913;
		long value = 0L;
		s.append(value);
	}

	@Test
	public void appendAll136() {
		StrBuilder s = new StrBuilder();
		Object[] array = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendAll(array);

		assertEquals(expected, actual);
	}

	@Test
	public void appendAll137() {
		StrBuilder s = new StrBuilder();
		Object[] array = {};
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendAll(array);

		assertEquals(expected, actual);
	}

	@Test
	public void appendAll138() {
		StrBuilder s = new StrBuilder();
		Object[] array = { null };
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendAll(array);

		assertEquals(expected, actual);
	}

	@Test
	public void appendFixedWidthPadLeft139() {
		StrBuilder s = new StrBuilder();
		int value = 0;
		int width = 0;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendFixedWidthPadLeft(value, width, padChar);

		assertEquals(expected, actual);
	}

	@Test
	public void appendFixedWidthPadLeft140() {
		StrBuilder s = new StrBuilder();
		Object obj = null;
		int width = 0;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendFixedWidthPadLeft(obj, width, padChar);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendFixedWidthPadLeft141() {
		StrBuilder s = new StrBuilder();
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadLeft(obj, width, padChar);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendFixedWidthPadLeft142() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadLeft(obj, width, padChar);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendFixedWidthPadLeft143() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		s.size = -2147483648;
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadLeft(obj, width, padChar);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendFixedWidthPadLeft144() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadLeft(obj, width, padChar);
	}

	@Test
	public void appendFixedWidthPadRight145() {
		StrBuilder s = new StrBuilder();
		int value = 0;
		int width = 0;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendFixedWidthPadRight(value, width, padChar);

		assertEquals(expected, actual);
	}

	@Test
	public void appendFixedWidthPadRight146() {
		StrBuilder s = new StrBuilder();
		Object obj = null;
		int width = 0;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendFixedWidthPadRight(obj, width, padChar);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendFixedWidthPadRight147() {
		StrBuilder s = new StrBuilder();
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadRight(obj, width, padChar);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendFixedWidthPadRight148() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadRight(obj, width, padChar);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendFixedWidthPadRight149() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		Object obj = null;
		int width = 1;
		char padChar = 0x0;
		s.appendFixedWidthPadRight(obj, width, padChar);
	}

	@Test
	public void appendNewLine150() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendNewLine();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendNewLine151() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.appendNewLine();
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendNewLine152() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		s.appendNewLine();
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendNewLine153() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 1073741823;
		s.appendNewLine();
	}

	@Test
	public void appendNull154() {
		StrBuilder s = new StrBuilder();
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendNull();

		assertEquals(expected, actual);
	}

	@Test
	public void appendNull155() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.appendNull();

		assertEquals(expected, actual);
	}

	@Test
	public void appendPadding156() {
		StrBuilder s = new StrBuilder();
		int length = -1;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendPadding(length, padChar);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendPadding157() {
		StrBuilder s = new StrBuilder();
		int length = 0;
		char padChar = 0x0;
		s.appendPadding(length, padChar);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendPadding158() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int length = 1;
		char padChar = 0x0;
		s.appendPadding(length, padChar);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendPadding159() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483648;
		int length = 1;
		char padChar = 0x0;
		s.appendPadding(length, padChar);
	}

	@Test
	public void appendPadding160() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		int length = 0;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{};
		StrBuilder actual = s.appendPadding(length, padChar);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendPadding161() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		int length = 0;
		char padChar = 0x0;
		s.appendPadding(length, padChar);
	}

	@Test
	public void appendPadding162() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u8d06' };
		int length = 1;
		char padChar = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		StrBuilder actual = s.appendPadding(length, padChar);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0 };

		assertEquals(sExpected, s);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendPadding163() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2147483646;
		int length = 2;
		char padChar = 0x0;
		s.appendPadding(length, padChar);
	}

	@Test
	public void appendSeparator164() {
		StrBuilder s = new StrBuilder();
		char separator = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendSeparator(separator);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendSeparator165() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char separator = 0x0;
		s.appendSeparator(separator);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendSeparator166() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char separator = 0x0;
		s.appendSeparator(separator);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendSeparator167() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		char separator = 0x0;
		s.appendSeparator(separator);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendSeparator168() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2147483647;
		char separator = 0x0;
		s.appendSeparator(separator);
	}

	@Test
	public void appendSeparator169() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, '\uca3f' };
		s.size = 2;
		char separator = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0, 0x0, 0x0 };
		expected.size = 2;
		StrBuilder actual = s.appendSeparator(separator);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0, 0x0, 0x0 };
		sExpected.size = 2;

		assertEquals(sExpected, s);
	}

	@Test
	public void appendSeparator170() {
		StrBuilder s = new StrBuilder();
		String standard = null;
		String defaultIfEmpty = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendSeparator(standard, defaultIfEmpty);

		assertEquals(expected, actual);
	}

	@Test
	public void appendSeparator171() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String standard = null;
		String defaultIfEmpty = null;
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.appendSeparator(standard, defaultIfEmpty);

		assertEquals(expected, actual);
	}

	@Test
	public void appendSeparator172() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String standard = "";
		String defaultIfEmpty = null;
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.appendSeparator(standard, defaultIfEmpty);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendSeparator173() {
		StrBuilder s = new StrBuilder();
		char standard = 0x0;
		char defaultIfEmpty = 0x0;
		s.appendSeparator(standard, defaultIfEmpty);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendSeparator174() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		char standard = 0x0;
		char defaultIfEmpty = 0x0;
		s.appendSeparator(standard, defaultIfEmpty);
	}

	@Test
	public void appendSeparator175() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u5939' };
		char standard = 0x0;
		char defaultIfEmpty = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		StrBuilder actual = s.appendSeparator(standard, defaultIfEmpty);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0 };

		assertEquals(sExpected, s);
	}

	@Test
	public void appendSeparator176() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, '\ua218' };
		s.size = 2;
		char standard = 0x0;
		char defaultIfEmpty = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0, 0x0, 0x0 };
		expected.size = 2;
		StrBuilder actual = s.appendSeparator(standard, defaultIfEmpty);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0, 0x0, 0x0 };
		sExpected.size = 2;

		assertEquals(sExpected, s);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendSeparator177() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = -2147483616;
		char standard = 0x0;
		char defaultIfEmpty = 0x0;
		s.appendSeparator(standard, defaultIfEmpty);
	}

	@Test
	public void appendSeparator178() {
		StrBuilder s = new StrBuilder();
		char separator = 0x0;
		int loopIndex = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendSeparator(separator, loopIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendSeparator179() {
		StrBuilder s = new StrBuilder();
		char separator = 0x0;
		int loopIndex = 1;
		s.appendSeparator(separator, loopIndex);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendSeparator180() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char separator = 0x0;
		int loopIndex = 1;
		s.appendSeparator(separator, loopIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendSeparator181() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -4;
		char separator = 0x0;
		int loopIndex = 1;
		s.appendSeparator(separator, loopIndex);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendSeparator182() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1778388992;
		char separator = 0x0;
		int loopIndex = 1;
		s.appendSeparator(separator, loopIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendSeparator183() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2147483647;
		char separator = 0x0;
		int loopIndex = 1;
		s.appendSeparator(separator, loopIndex);
	}

	@Test
	public void appendSeparator184() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u4255' };
		char separator = 0x0;
		int loopIndex = 1;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		StrBuilder actual = s.appendSeparator(separator, loopIndex);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0 };

		assertEquals(sExpected, s);
	}

	@Test
	public void appendSeparator185() {
		StrBuilder s = new StrBuilder();
		String separator = null;
		int loopIndex = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendSeparator(separator, loopIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void appendSeparator186() {
		StrBuilder s = new StrBuilder();
		String separator = "";
		int loopIndex = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendSeparator(separator, loopIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void appendSeparator187() {
		StrBuilder s = new StrBuilder();
		String separator = "";
		int loopIndex = 1;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendSeparator(separator, loopIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void appendSeparator188() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String separator = null;
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.appendSeparator(separator);

		assertEquals(expected, actual);
	}

	@Test
	public void appendWithSeparators189() {
		StrBuilder s = new StrBuilder();
		Object[] array = null;
		String separator = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendWithSeparators(array, separator);

		assertEquals(expected, actual);
	}

	@Test
	public void appendWithSeparators190() {
		StrBuilder s = new StrBuilder();
		Object[] array = {};
		String separator = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendWithSeparators(array, separator);

		assertEquals(expected, actual);
	}

	@Test
	public void appendWithSeparators191() {
		StrBuilder s = new StrBuilder();
		Object[] array = { null };
		String separator = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendWithSeparators(array, separator);

		assertEquals(expected, actual);
	}

	@Test
	public void appendWithSeparators192() {
		StrBuilder s = new StrBuilder();
		Object[] array = { null, null };
		String separator = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.appendWithSeparators(array, separator);

		assertEquals(expected, actual);
	}

	@Test
	public void appendWithSeparators193() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		Object[] array = { null };
		String separator = "";
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.appendWithSeparators(array, separator);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendWithSeparators194() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		Object[] array = { null };
		String separator = "";
		s.appendWithSeparators(array, separator);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendWithSeparators195() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		Object[] array = { null };
		String separator = null;
		s.appendWithSeparators(array, separator);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendWithSeparators196() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		Object[] array = { null };
		String separator = "";
		s.appendWithSeparators(array, separator);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendWithSeparators197() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073741824;
		Object[] array = { null };
		String separator = "";
		s.appendWithSeparators(array, separator);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln198() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		str.size = -2147483648;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln199() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		int startIndex = -1;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln200() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		int startIndex = 0;
		int length = -1;
		s.appendln(str, startIndex, length);
	}

	@Test
	public void appendln201() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		StrBuilder str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln202() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073743424;
		StrBuilder str = new StrBuilder();
		str.size = 1;
		int startIndex = 0;
		int length = 1;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln203() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1342177031;
		StrBuilder str = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln204() {
		StrBuilder s = new StrBuilder();
		float value = 0.000000000000000000000000000000000000000048608F;
		s.appendln(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln205() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		float value = Float.NaN;
		s.appendln(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln206() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2147475263;
		float value = -3431756000.0F;
		s.appendln(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendln207() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -4;
		char ch = 0x0;
		s.appendln(ch);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln208() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		StringBuffer str = null;
		s.appendln(str);
	}

	@Test
	public void appendln209() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		StringBuffer str = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln210() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		StringBuffer str = null;
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln211() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		StringBuffer str = null;
		s.appendln(str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln212() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073741823;
		StringBuffer str = null;
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln213() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		Object obj = null;
		s.appendln(obj);
	}

	@Test
	public void appendln214() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		Object obj = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(obj);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln215() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		Object obj = null;
		s.appendln(obj);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln216() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		Object obj = null;
		s.appendln(obj);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln217() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073741823;
		Object obj = null;
		s.appendln(obj);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln218() {
		StrBuilder s = new StrBuilder();
		double value = 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000382842267379708D;
		s.appendln(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln219() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		double value = -36929516970342285000.0D;
		s.appendln(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln220() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1082212416;
		double value = 36893488147419103000.0D;
		s.appendln(value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln221() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = -1;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln222() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = 1;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln223() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		String str = "A";
		int startIndex = 0;
		int length = -1;
		s.appendln(str, startIndex, length);
	}

	@Test
	public void appendln224() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		String str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln225() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073742207;
		String str = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln226() {
		StrBuilder s = new StrBuilder();
		int value = 0;
		s.appendln(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln227() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int value = 0;
		s.appendln(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln228() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2096619265;
		int value = 0;
		s.appendln(value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln229() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = -1;
		int length = 0;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln230() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 0;
		int length = -1;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln231() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 0;
		int length = 1;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln232() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		char[] chars = {};
		int startIndex = 1;
		int length = 0;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln233() {
		StrBuilder s = new StrBuilder();
		char[] chars = { 0x0 };
		int startIndex = 1;
		int length = 2147483647;
		s.appendln(chars, startIndex, length);
	}

	@Test
	public void appendln234() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		char[] chars = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(chars, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln235() {
		StrBuilder s = new StrBuilder();
		s.size = 2;
		char[] chars = { 0x0 };
		int startIndex = 0;
		int length = 1;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln236() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char[] chars = { 0x0 };
		int startIndex = 0;
		int length = 1;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln237() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		char[] chars = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln238() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		char[] chars = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln239() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		char[] chars = {};
		int startIndex = 0;
		int length = 0;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln240() {
		StrBuilder s = new StrBuilder();
		s.size = 64;
		char[] chars = { 0x0 };
		int startIndex = 1;
		int length = 2147483647;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln241() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		char[] chars = { 0x0 };
		int startIndex = 0;
		int length = 1;
		s.appendln(chars, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln242() {
		StrBuilder s = new StrBuilder();
		boolean value = false;
		s.appendln(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln243() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483644;
		boolean value = false;
		s.appendln(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln244() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483646;
		boolean value = true;
		s.appendln(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendln245() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -10;
		boolean value = false;
		s.appendln(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendln246() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -16;
		boolean value = true;
		s.appendln(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln247() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741819;
		boolean value = false;
		s.appendln(value);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendln248() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2147483644;
		boolean value = false;
		s.appendln(value);
	}

	@Test
	public void appendln249() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 'f', 'f', 'f', 'f' };
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		boolean value = false;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 'e', 'f', 'f', 'f', 'f' };
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(value);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 'e', 'f', 'f', 'f', 'f' };
		final Field fieldNewLine3 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine3.setAccessible(true);
		fieldNewLine3.set(sExpected, "");

		assertEquals(sExpected, s);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void appendln250() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 2147483646;
		boolean value = true;
		s.appendln(value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void appendln251() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		StringBuffer str = new StringBuffer();
		int startIndex = -1;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln252() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test
	public void appendln253() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(str, startIndex, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln254() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln255() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln256() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073742175;
		StringBuffer str = null;
		int startIndex = 0;
		int length = 0;
		s.appendln(str, startIndex, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln257() {
		StrBuilder s = new StrBuilder();
		char[] chars = { 0x0 };
		s.appendln(chars);
	}

	@Test
	public void appendln258() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		char[] chars = {};
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(chars);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln259() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		char[] chars = { 0x0 };
		s.appendln(chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln260() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		char[] chars = null;
		s.appendln(chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln261() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		char[] chars = null;
		s.appendln(chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln262() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		char[] chars = {};
		s.appendln(chars);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln263() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		char[] chars = { 0x0 };
		s.appendln(chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln264() {
		StrBuilder s = new StrBuilder();
		StrBuilder str = new StrBuilder();
		str.size = 1;
		s.appendln(str);
	}

	@Test
	public void appendln265() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		StrBuilder str = new StrBuilder();
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln266() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		StrBuilder str = new StrBuilder();
		str.size = 1;
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln267() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		StrBuilder str = null;
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln268() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		StrBuilder str = null;
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln269() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "A");
		s.size = 2147483647;
		StrBuilder str = new StrBuilder();
		s.appendln(str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln270() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		StrBuilder str = new StrBuilder();
		str.size = 1;
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln271() {
		StrBuilder s = new StrBuilder();
		String str = "A";
		s.appendln(str);
	}

	@Test
	public void appendln272() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		String str = "";
		StrBuilder expected = new StrBuilder();
		final Field fieldNewLine2 = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine2.setAccessible(true);
		fieldNewLine2.set(expected, "");
		StrBuilder actual = s.appendln(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln273() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		String str = "A";
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln274() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		String str = null;
		s.appendln(str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln275() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		String str = "A";
		s.appendln(str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln276() {
		StrBuilder s = new StrBuilder();
		long value = 0L;
		s.appendln(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void appendln277() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		long value = 0L;
		s.appendln(value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void appendln278() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0 };
		s.size = 2144870913;
		long value = 0L;
		s.appendln(value);
	}

	@Test
	public void asReader279() {
		StrBuilder s = new StrBuilder();
		StrBuilder outerClass = new StrBuilder();
		Reader expected = outerClass.new StrBuilderReader();
		Reader actual = s.asReader();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void capacity280() {
		StrBuilder s = new StrBuilder();
		s.capacity();
	}

	@Test
	public void capacity281() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		int expected = 0;
		int actual = s.capacity();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void charAt282() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -1107287168;
		int index = 0;
		s.charAt(index);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void charAt283() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		s.charAt(index);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void charAt284() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		int index = 0;
		s.charAt(index);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void charAt285() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 32769;
		int index = 0;
		s.charAt(index);
	}

	@Test
	public void charAt286() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		int index = 0;
		char expected = 0x0;
		char actual = s.charAt(index);

		assertEquals(expected, actual);
	}

	@Test
	public void clear287() {
		StrBuilder s = new StrBuilder();
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.clear();

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();

		assertEquals(sExpected, s);
	}

	@Test
	public void contains288() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		boolean actual = s.contains(ch);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void contains289() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char ch = 0x0;
		s.contains(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void contains290() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char ch = 0x0;
		s.contains(ch);
	}

	@Test
	public void contains291() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		char ch = 0x0;
		boolean actual = s.contains(ch);

		assertTrue(actual);
	}

	@Test
	public void contains292() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		char ch = 0x0;
		boolean actual = s.contains(ch);

		assertFalse(actual);
	}

	@Test
	public void contains293() {
		StrBuilder s = new StrBuilder();
		String str = null;
		boolean actual = s.contains(str);

		assertFalse(actual);
	}

	@Test
	public void contains294() {
		StrBuilder s = new StrBuilder();
		String str = "";
		boolean actual = s.contains(str);

		assertFalse(actual);
	}

	@Test
	public void contains295() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "";
		boolean actual = s.contains(str);

		assertTrue(actual);
	}

	@Test
	public void delete296() {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.delete(startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void delete297() {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = 1;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.delete(startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteAll298() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteAll(ch);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void deleteAll299() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char ch = 0x0;
		s.deleteAll(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void deleteAll300() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char ch = 0x0;
		s.deleteAll(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void deleteAll301() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 2;
		char ch = 0x0;
		s.deleteAll(ch);
	}

	@Test
	public void deleteAll302() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		char ch = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0xFFFF };
		expected.size = 1;
		StrBuilder actual = s.deleteAll(ch);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteAll303() {
		StrBuilder s = new StrBuilder();
		String str = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteAll(str);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteAll304() {
		StrBuilder s = new StrBuilder();
		String str = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteAll(str);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteAll305() {
		StrBuilder s = new StrBuilder();
		String str = "A";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteAll(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void deleteAll306() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "A";
		s.deleteAll(str);
	}

	@Test
	public void deleteAll307() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "AB";
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.deleteAll(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void deleteAll308() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		s.deleteAll(str);
	}

	@Test
	public void deleteAll309() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '8' };
		s.size = 1;
		String str = "\u00e7";
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '8' };
		expected.size = 1;
		StrBuilder actual = s.deleteAll(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void deleteCharAt310() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -1107287168;
		int index = 0;
		s.deleteCharAt(index);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void deleteCharAt311() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		s.deleteCharAt(index);
	}

	@Test
	public void deleteFirst312() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteFirst(ch);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void deleteFirst313() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char ch = 0x0;
		s.deleteFirst(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void deleteFirst314() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char ch = 0x0;
		s.deleteFirst(ch);
	}

	@Test
	public void deleteFirst315() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		char ch = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0xFFFF };
		expected.size = 1;
		StrBuilder actual = s.deleteFirst(ch);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteFirst316() {
		StrBuilder s = new StrBuilder();
		String str = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteFirst(str);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteFirst317() {
		StrBuilder s = new StrBuilder();
		String str = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteFirst(str);

		assertEquals(expected, actual);
	}

	@Test
	public void deleteFirst318() {
		StrBuilder s = new StrBuilder();
		String str = "A";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.deleteFirst(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void deleteFirst319() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "A";
		s.deleteFirst(str);
	}

	@Test
	public void deleteFirst320() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "AB";
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.deleteFirst(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void deleteFirst321() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		s.deleteFirst(str);
	}

	@Test
	public void deleteFirst322() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '8' };
		s.size = 1;
		String str = "\u00e7";
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '8' };
		expected.size = 1;
		StrBuilder actual = s.deleteFirst(str);

		assertEquals(expected, actual);
	}

	@Test
	public void endsWith323() {
		StrBuilder s = new StrBuilder();
		String str = null;
		boolean actual = s.endsWith(str);

		assertFalse(actual);
	}

	@Test
	public void endsWith324() {
		StrBuilder s = new StrBuilder();
		String str = "";
		boolean actual = s.endsWith(str);

		assertTrue(actual);
	}

	@Test
	public void endsWith325() {
		StrBuilder s = new StrBuilder();
		String str = "A";
		boolean actual = s.endsWith(str);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void endsWith326() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "A";
		s.endsWith(str);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void endsWith327() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		s.endsWith(str);
	}

	@Test
	public void endsWith328() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '9' };
		s.size = 1;
		String str = "\u00ff";
		boolean actual = s.endsWith(str);

		assertFalse(actual);
	}

	@Test
	public void endsWith329() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00ff' };
		s.size = 1;
		String str = "\u00ff";
		boolean actual = s.endsWith(str);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void ensureCapacity330() {
		StrBuilder s = new StrBuilder();
		int capacity = 0;
		s.ensureCapacity(capacity);
	}

	@Test
	public void ensureCapacity331() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		int capacity = 0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{};
		StrBuilder actual = s.ensureCapacity(capacity);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void ensureCapacity332() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		int capacity = 1073741824;
		s.ensureCapacity(capacity);
	}

	@Test
	public void equals333() {
		StrBuilder s = new StrBuilder();
		Object obj = null;
		boolean actual = s.equals(obj);

		assertFalse(actual);
	}

	@Test
	public void equals334() {
		StrBuilder s = new StrBuilder();
		Object obj = new StrBuilder();
		boolean actual = s.equals(obj);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equals335() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		StrBuilder other = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(other, "");
		other.size = -2147483648;
		s.equals(other);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equals336() {
		StrBuilder s = new StrBuilder();
		StrBuilder other = null;
		s.equals(other);
	}

	@Test
	public void equals337() {
		StrBuilder s = new StrBuilder();
		StrBuilder other = new StrBuilder();
		boolean actual = s.equals(other);

		assertTrue(actual);
	}

	@Test
	public void equals338() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		StrBuilder other = new StrBuilder();
		boolean actual = s.equals(other);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equals339() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.size = 1;
		s.equals(other);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void equals340() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483648;
		StrBuilder other = new StrBuilder();
		other.size = -2147483648;
		s.equals(other);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void equals341() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 4;
		StrBuilder other = new StrBuilder();
		other.size = 4;
		s.equals(other);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void equals342() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u4e59' };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.buffer = new char[]{};
		other.size = 1;
		s.equals(other);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equals343() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.size = 1;
		s.equals(other);
	}

	@Test
	public void equals344() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.buffer = new char[]{ 0x0 };
		other.size = 1;
		boolean actual = s.equals(other);

		assertTrue(actual);
	}

	@Test
	public void equals345() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.buffer = new char[]{ 0x0 };
		other.size = 1;
		boolean actual = s.equals(other);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equalsIgnoreCase346() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		StrBuilder other = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(other, "");
		other.size = -2147483648;
		s.equalsIgnoreCase(other);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equalsIgnoreCase347() {
		StrBuilder s = new StrBuilder();
		StrBuilder other = null;
		s.equalsIgnoreCase(other);
	}

	@Test
	public void equalsIgnoreCase348() {
		StrBuilder s = new StrBuilder();
		StrBuilder other = new StrBuilder();
		boolean actual = s.equalsIgnoreCase(other);

		assertTrue(actual);
	}

	@Test
	public void equalsIgnoreCase349() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		StrBuilder other = new StrBuilder();
		boolean actual = s.equalsIgnoreCase(other);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equalsIgnoreCase350() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNewLine = StrBuilder.class.getDeclaredField("newLine");
		fieldNewLine.setAccessible(true);
		fieldNewLine.set(s, "");
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.size = 1;
		s.equalsIgnoreCase(other);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void equalsIgnoreCase351() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483648;
		StrBuilder other = new StrBuilder();
		other.size = -2147483648;
		s.equalsIgnoreCase(other);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void equalsIgnoreCase352() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 4;
		StrBuilder other = new StrBuilder();
		other.size = 4;
		s.equalsIgnoreCase(other);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void equalsIgnoreCase353() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u4e59' };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.buffer = new char[]{};
		other.size = 1;
		s.equalsIgnoreCase(other);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void equalsIgnoreCase354() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.size = 1;
		s.equalsIgnoreCase(other);
	}

	@Test
	public void equalsIgnoreCase355() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		StrBuilder other = new StrBuilder();
		other.buffer = new char[]{ 0x0 };
		other.size = 1;
		boolean actual = s.equalsIgnoreCase(other);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void getChars356() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int startIndex = 0;
		int endIndex = 0;
		char[] destination = null;
		int destinationIndex = 0;
		s.getChars(startIndex, endIndex, destination, destinationIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void getChars357() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = -1;
		int endIndex = 0;
		char[] destination = null;
		int destinationIndex = 0;
		s.getChars(startIndex, endIndex, destination, destinationIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void getChars358() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = -1;
		char[] destination = null;
		int destinationIndex = 0;
		s.getChars(startIndex, endIndex, destination, destinationIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void getChars359() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 1;
		int endIndex = 0;
		char[] destination = null;
		int destinationIndex = 0;
		s.getChars(startIndex, endIndex, destination, destinationIndex);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void getChars360() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		char[] destination = null;
		s.getChars(destination);
	}

	@Test
	public void getNewLineText361() {
		StrBuilder s = new StrBuilder();
		String actual = s.getNewLineText();

		assertNull(actual);
	}

	@Test
	public void getNullText362() {
		StrBuilder s = new StrBuilder();
		String actual = s.getNullText();

		assertNull(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void hashCode363() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		s.hashCode();
	}

	@Test
	public void hashCode364() {
		StrBuilder s = new StrBuilder();
		int expected = 0;
		int actual = s.hashCode();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void hashCode365() {
		StrBuilder s = new StrBuilder();
		s.size = 2048;
		s.hashCode();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void hashCode366() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483648;
		s.hashCode();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void hashCode367() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		s.hashCode();
	}

	@Test
	public void hashCode368() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		int expected = 0;
		int actual = s.hashCode();

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf369() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		int expected = -1;
		int actual = s.indexOf(ch);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf370() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		int startIndex = -1;
		int expected = -1;
		int actual = s.indexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf371() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		int startIndex = 0;
		int expected = -1;
		int actual = s.indexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void indexOf372() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		s.indexOf(ch, startIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void indexOf373() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		s.indexOf(ch, startIndex);
	}

	@Test
	public void indexOf374() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		int expected = 0;
		int actual = s.indexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf375() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		int expected = -1;
		int actual = s.indexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf376() {
		StrBuilder s = new StrBuilder();
		String str = null;
		int startIndex = -1;
		int expected = -1;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf377() {
		StrBuilder s = new StrBuilder();
		String str = null;
		int startIndex = 0;
		int expected = -1;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf378() {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = 0;
		int expected = -1;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf379() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "";
		int startIndex = 0;
		int expected = 0;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf380() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "AB";
		int startIndex = 0;
		int expected = -1;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void indexOf381() {
		StrBuilder s = new StrBuilder();
		s.size = 257;
		String str = "A";
		int startIndex = 0;
		s.indexOf(str, startIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void indexOf382() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		int startIndex = 0;
		s.indexOf(str, startIndex);
	}

	@Test
	public void indexOf383() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00b8' };
		s.size = 1;
		String str = "h";
		int startIndex = 0;
		int expected = -1;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf384() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00d2' };
		s.size = 1;
		String str = "\u00d2";
		int startIndex = 0;
		int expected = 0;
		int actual = s.indexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf385() {
		StrBuilder s = new StrBuilder();
		String str = null;
		int expected = -1;
		int actual = s.indexOf(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert386() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		boolean value = false;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert387() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		boolean value = false;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert388() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		boolean value = false;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert389() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483645;
		int index = 0;
		boolean value = false;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert390() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483645;
		int index = 0;
		boolean value = true;
		s.insert(index, value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert391() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741819;
		int index = 0;
		boolean value = false;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert392() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		int value = 0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert393() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		int value = 0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert394() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		int value = 0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert395() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		int value = 0;
		s.insert(index, value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert396() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2080407552;
		int index = 0;
		int value = 0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert397() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		char value = 0x0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert398() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		char value = 0x0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert399() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char value = 0x0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert400() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		char value = 0x0;
		s.insert(index, value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert401() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741824;
		int index = 0;
		char value = 0x0;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert402() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		String str = null;
		s.insert(index, str);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert403() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		String str = null;
		s.insert(index, str);
	}

	@Test
	public void insert404() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		String str = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, str);

		assertEquals(expected, actual);
	}

	@Test
	public void insert405() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		String str = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert406() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		String str = "A";
		s.insert(index, str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert407() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		String str = "A";
		s.insert(index, str);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert408() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		int index = 0;
		String str = null;
		s.insert(index, str);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert409() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		s.size = 1207696189;
		int index = 0;
		String str = "A";
		s.insert(index, str);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert410() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		float value = 2148303400.0F;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert411() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		float value = 8589937000.0F;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert412() {
		StrBuilder s = new StrBuilder();
		s.size = 2;
		int index = 0;
		float value = 0.0625F;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert413() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		float value = 0.000000000000000000000000000000000000000918596F;
		s.insert(index, value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert414() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		int index = 0;
		float value = -565462600000000.0F;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert415() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		Object obj = null;
		s.insert(index, obj);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert416() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		Object obj = null;
		s.insert(index, obj);
	}

	@Test
	public void insert417() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		Object obj = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, obj);

		assertEquals(expected, actual);
	}

	@Test
	public void insert418() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		int index = 0;
		Object obj = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.insert(index, obj);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert419() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		int index = 0;
		Object obj = null;
		s.insert(index, obj);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert420() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		int index = 0;
		Object obj = null;
		s.insert(index, obj);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert421() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 1073741823;
		int index = 0;
		Object obj = null;
		s.insert(index, obj);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert422() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		char[] chars = null;
		s.insert(index, chars);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert423() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		char[] chars = null;
		s.insert(index, chars);
	}

	@Test
	public void insert424() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, chars);

		assertEquals(expected, actual);
	}

	@Test
	public void insert425() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = {};
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, chars);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert426() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = { 0x0 };
		s.insert(index, chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert427() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		char[] chars = { 0x0 };
		s.insert(index, chars);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert428() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		int index = 0;
		char[] chars = null;
		s.insert(index, chars);
	}

	@Test
	public void insert429() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		s.size = 256;
		int index = 0;
		char[] chars = null;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		expected.size = 256;
		StrBuilder actual = s.insert(index, chars);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert430() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		int index = 0;
		char[] chars = { 0x0 };
		s.insert(index, chars);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert431() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		char[] chars = null;
		int offset = 0;
		int length = 0;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert432() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		char[] chars = null;
		int offset = 0;
		int length = 0;
		s.insert(index, chars, offset, length);
	}

	@Test
	public void insert433() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = null;
		int offset = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, chars, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert434() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = {};
		int offset = 0;
		int length = -1;
		s.insert(index, chars, offset, length);
	}

	@Test
	public void insert435() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = {};
		int offset = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.insert(index, chars, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert436() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = { 0x0 };
		int offset = 0;
		int length = 1;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert437() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		char[] chars = { 0x0 };
		int offset = 1;
		int length = 2147483647;
		s.insert(index, chars, offset, length);
	}

	@Test
	public void insert438() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "");
		int index = 0;
		char[] chars = null;
		int offset = 0;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(expected, "");
		StrBuilder actual = s.insert(index, chars, offset, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert439() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		int index = 0;
		char[] chars = { 0x0 };
		int offset = 1;
		int length = 2147483647;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert440() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = 1073741824;
		int index = 0;
		char[] chars = {};
		int offset = -1;
		int length = 0;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert441() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = 1073741824;
		int index = 0;
		char[] chars = {};
		int offset = 0;
		int length = 1;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert442() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = 1207959552;
		int index = 0;
		char[] chars = {};
		int offset = 1;
		int length = 0;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert443() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		char[] chars = { 0x0 };
		int offset = 0;
		int length = 1;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert444() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(s, "A");
		s.size = 2147483647;
		int index = 0;
		char[] chars = null;
		int offset = 0;
		int length = 0;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert445() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741838;
		int index = 0;
		char[] chars = { 0x0 };
		int offset = 0;
		int length = 1;
		s.insert(index, chars, offset, length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert446() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		long value = 0L;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert447() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		long value = 0L;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert448() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		long value = 0L;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert449() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		long value = 0L;
		s.insert(index, value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert450() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0, 0x0, 0x0 };
		s.size = 2143289345;
		int index = 0;
		long value = 0L;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert451() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		double value = -18446462590142904000.0D;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void insert452() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		double value = 0.00000000000000000008152693409823326D;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert453() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		double value = 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002193567164415514D;
		s.insert(index, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void insert454() {
		StrBuilder s = new StrBuilder();
		s.size = 2147483647;
		int index = 0;
		double value = 0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000005656765196125793D;
		s.insert(index, value);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void insert455() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1073741823;
		int index = 0;
		double value = 0.000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000022252096662381774D;
		s.insert(index, value);
	}

	@Test
	public void isEmpty456() {
		StrBuilder s = new StrBuilder();
		boolean actual = s.isEmpty();

		assertTrue(actual);
	}

	@Test
	public void isEmpty457() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		boolean actual = s.isEmpty();

		assertFalse(actual);
	}

	@Test
	public void lastIndexOf458() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483520;
		char ch = 0x0;
		int expected = -1;
		int actual = s.lastIndexOf(ch);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lastIndexOf459() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		char ch = 0x0;
		s.lastIndexOf(ch);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lastIndexOf460() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char ch = 0x0;
		s.lastIndexOf(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lastIndexOf461() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483648;
		char ch = 0x0;
		s.lastIndexOf(ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lastIndexOf462() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char ch = 0x0;
		s.lastIndexOf(ch);
	}

	@Test
	public void lastIndexOf463() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		char ch = 0x0;
		int expected = 0;
		int actual = s.lastIndexOf(ch);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf464() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		char ch = 0x0;
		int expected = -1;
		int actual = s.lastIndexOf(ch);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lastIndexOf465() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		char ch = 0x0;
		int startIndex = 0;
		s.lastIndexOf(ch, startIndex);
	}

	@Test
	public void lastIndexOf466() {
		StrBuilder s = new StrBuilder();
		char ch = 0x0;
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lastIndexOf467() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		s.lastIndexOf(ch, startIndex);
	}

	@Test
	public void lastIndexOf468() {
		StrBuilder s = new StrBuilder();
		s.size = 67109631;
		char ch = 0x0;
		int startIndex = -1;
		int expected = -1;
		int actual = s.lastIndexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lastIndexOf469() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483648;
		char ch = 0x0;
		int startIndex = 0;
		s.lastIndexOf(ch, startIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lastIndexOf470() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		s.lastIndexOf(ch, startIndex);
	}

	@Test
	public void lastIndexOf471() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		int expected = 0;
		int actual = s.lastIndexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf472() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xFFFF };
		s.size = 1;
		char ch = 0x0;
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(ch, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf473() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		String str = null;
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf474() {
		StrBuilder s = new StrBuilder();
		String str = null;
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf475() {
		StrBuilder s = new StrBuilder();
		String str = "";
		int startIndex = -1;
		int expected = -1;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf476() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "";
		int startIndex = 0;
		int expected = 0;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lastIndexOf477() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "A";
		int startIndex = 0;
		s.lastIndexOf(str, startIndex);
	}

	@Test
	public void lastIndexOf478() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "AB";
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf479() {
		StrBuilder s = new StrBuilder();
		s.size = 1073741824;
		String str = null;
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lastIndexOf480() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		int startIndex = 0;
		s.lastIndexOf(str, startIndex);
	}

	@Test
	public void lastIndexOf481() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00e0' };
		s.size = 1;
		String str = "\u00e0";
		int startIndex = 0;
		int expected = 0;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf482() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0xC };
		s.size = 1;
		String str = "\u00f3";
		int startIndex = 0;
		int expected = -1;
		int actual = s.lastIndexOf(str, startIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf483() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483632;
		String str = "";
		int expected = -1;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf484() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		String str = null;
		int expected = -1;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf485() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = null;
		int expected = -1;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf486() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "";
		int expected = 0;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void lastIndexOf487() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "A";
		s.lastIndexOf(str);
	}

	@Test
	public void lastIndexOf488() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "TU";
		int expected = -1;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void lastIndexOf489() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		s.lastIndexOf(str);
	}

	@Test
	public void lastIndexOf490() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 'I' };
		s.size = 1;
		String str = "\u00be";
		int expected = -1;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf491() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00fe' };
		s.size = 1;
		String str = "\u00fe";
		int expected = 0;
		int actual = s.lastIndexOf(str);

		assertEquals(expected, actual);
	}

	@Test
	public void leftString492() {
		StrBuilder s = new StrBuilder();
		int length = 0;
		String expected = "";
		String actual = s.leftString(length);

		assertEquals(expected, actual);
	}

	@Test
	public void length493() {
		StrBuilder s = new StrBuilder();
		int expected = 0;
		int actual = s.length();

		assertEquals(expected, actual);
	}

	@Test
	public void midString494() {
		StrBuilder s = new StrBuilder();
		int index = -1;
		int length = 0;
		String expected = "";
		String actual = s.midString(index, length);

		assertEquals(expected, actual);
	}

	@Test
	public void midString495() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		int length = 0;
		String expected = "";
		String actual = s.midString(index, length);

		assertEquals(expected, actual);
	}

	@Test
	public void midString496() {
		StrBuilder s = new StrBuilder();
		int index = 0;
		int length = 1;
		String expected = "";
		String actual = s.midString(index, length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void minimizeCapacity497() {
		StrBuilder s = new StrBuilder();
		s.minimizeCapacity();
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void minimizeCapacity498() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = -2147483648;
		s.minimizeCapacity();
	}

	@Test
	public void minimizeCapacity499() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 2097152;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		expected.size = 2097152;
		StrBuilder actual = s.minimizeCapacity();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void replace500() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = -1;
		int endIndex = 0;
		String replaceStr = null;
		s.replace(startIndex, endIndex, replaceStr);
	}

	@Test
	public void replace501() {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = 0;
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replace(startIndex, endIndex, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replace502() {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = 1;
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replace(startIndex, endIndex, replaceStr);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void replace503() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 1;
		int endIndex = 0;
		String replaceStr = null;
		s.replace(startIndex, endIndex, replaceStr);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void replace504() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		int startIndex = 0;
		int endIndex = 1;
		String replaceStr = null;
		s.replace(startIndex, endIndex, replaceStr);
	}

	@Test
	public void replace505() {
		StrBuilder s = new StrBuilder();
		s.size = 1073741889;
		int startIndex = 0;
		int endIndex = 0;
		String replaceStr = "";
		StrBuilder expected = new StrBuilder();
		expected.size = 1073741889;
		StrBuilder actual = s.replace(startIndex, endIndex, replaceStr);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void replace506() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1383923712;
		int startIndex = 0;
		int endIndex = 1;
		String replaceStr = null;
		s.replace(startIndex, endIndex, replaceStr);
	}

	@Test
	public void replaceAll507() {
		StrBuilder s = new StrBuilder();
		String searchStr = null;
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll508() {
		StrBuilder s = new StrBuilder();
		String searchStr = "";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll509() {
		StrBuilder s = new StrBuilder();
		String searchStr = "A";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll510() {
		StrBuilder s = new StrBuilder();
		String searchStr = "A";
		String replaceStr = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void replaceAll511() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String searchStr = "A";
		String replaceStr = null;
		s.replaceAll(searchStr, replaceStr);
	}

	@Test
	public void replaceAll512() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String searchStr = "AB";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll513() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String searchStr = "AB";
		String replaceStr = "";
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void replaceAll514() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String searchStr = "A";
		String replaceStr = null;
		s.replaceAll(searchStr, replaceStr);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void replaceAll515() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '~' };
		s.size = 1610612736;
		String searchStr = "~";
		String replaceStr = null;
		s.replaceAll(searchStr, replaceStr);
	}

	@Test
	public void replaceAll516() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00a2' };
		s.size = 1;
		String searchStr = "\u00ec";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '\u00a2' };
		expected.size = 1;
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll517() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0 };
		s.size = 2;
		String searchStr = "\u00ff";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0, 0x0 };
		expected.size = 2;
		StrBuilder actual = s.replaceAll(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll518() {
		StrBuilder s = new StrBuilder();
		char search = 0x0;
		char replace = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceAll(search, replace);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll519() {
		StrBuilder s = new StrBuilder();
		char search = 0x0;
		char replace = 0x1;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceAll(search, replace);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void replaceAll520() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		s.replaceAll(search, replace);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void replaceAll521() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		s.replaceAll(search, replace);
	}

	@Test
	public void replaceAll522() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\ufef3' };
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '\ufef3' };
		expected.size = 1;
		StrBuilder actual = s.replaceAll(search, replace);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceAll523() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x1 };
		expected.size = 1;
		StrBuilder actual = s.replaceAll(search, replace);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x1 };
		sExpected.size = 1;

		assertEquals(sExpected, s);
	}

	@Test
	public void replaceFirst524() {
		StrBuilder s = new StrBuilder();
		String searchStr = null;
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceFirst(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceFirst525() {
		StrBuilder s = new StrBuilder();
		String searchStr = "";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceFirst(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceFirst526() {
		StrBuilder s = new StrBuilder();
		String searchStr = "A";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceFirst(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void replaceFirst527() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String searchStr = "A";
		String replaceStr = null;
		s.replaceFirst(searchStr, replaceStr);
	}

	@Test
	public void replaceFirst528() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String searchStr = "AB";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.replaceFirst(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void replaceFirst529() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String searchStr = "A";
		String replaceStr = null;
		s.replaceFirst(searchStr, replaceStr);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void replaceFirst530() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '~' };
		s.size = 1610612736;
		String searchStr = "~";
		String replaceStr = null;
		s.replaceFirst(searchStr, replaceStr);
	}

	@Test
	public void replaceFirst531() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00cc' };
		s.size = 1;
		String searchStr = "\u00ef";
		String replaceStr = null;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '\u00cc' };
		expected.size = 1;
		StrBuilder actual = s.replaceFirst(searchStr, replaceStr);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceFirst532() {
		StrBuilder s = new StrBuilder();
		char search = 0x0;
		char replace = 0x0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceFirst(search, replace);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceFirst533() {
		StrBuilder s = new StrBuilder();
		char search = 0x0;
		char replace = 0x1;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.replaceFirst(search, replace);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void replaceFirst534() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		s.replaceFirst(search, replace);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void replaceFirst535() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		s.replaceFirst(search, replace);
	}

	@Test
	public void replaceFirst536() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\ufef3' };
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '\ufef3' };
		expected.size = 1;
		StrBuilder actual = s.replaceFirst(search, replace);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceFirst537() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 1;
		char search = 0x0;
		char replace = 0x1;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x1 };
		expected.size = 1;
		StrBuilder actual = s.replaceFirst(search, replace);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x1 };
		sExpected.size = 1;

		assertEquals(sExpected, s);
	}

	@Test
	public void reverse538() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		StrBuilder expected = new StrBuilder();
		expected.size = -2147483648;
		StrBuilder actual = s.reverse();

		assertEquals(expected, actual);
	}

	@Test
	public void reverse539() {
		StrBuilder s = new StrBuilder();
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.reverse();

		assertEquals(expected, actual);
	}

	@Test
	public void reverse540() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		StrBuilder expected = new StrBuilder();
		expected.size = 1;
		StrBuilder actual = s.reverse();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void reverse541() {
		StrBuilder s = new StrBuilder();
		s.size = 32;
		s.reverse();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void reverse542() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 2;
		s.reverse();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void reverse543() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0 };
		s.size = 2;
		s.reverse();
	}

	@Test
	public void reverse544() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ 0x0, 0x0 };
		s.size = 2;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0, 0x0 };
		expected.size = 2;
		StrBuilder actual = s.reverse();

		assertEquals(expected, actual);
	}

	@Test
	public void rightString545() {
		StrBuilder s = new StrBuilder();
		int length = 0;
		String expected = "";
		String actual = s.rightString(length);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void setCharAt546() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2013265920;
		int index = 0;
		char ch = 0x0;
		s.setCharAt(index, ch);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void setCharAt547() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		char ch = 0x0;
		s.setCharAt(index, ch);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void setCharAt548() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		int index = 0;
		char ch = 0x0;
		s.setCharAt(index, ch);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void setCharAt549() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		int index = 0;
		char ch = 0x0;
		s.setCharAt(index, ch);
	}

	@Test
	public void setCharAt550() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u149b' };
		s.size = 1;
		int index = 0;
		char ch = 0x0;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		expected.size = 1;
		StrBuilder actual = s.setCharAt(index, ch);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0 };
		sExpected.size = 1;

		assertEquals(sExpected, s);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void setLength551() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int length = 0;
		s.setLength(length);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void setLength552() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int length = -1;
		s.setLength(length);
	}

	@Test
	public void setLength553() {
		StrBuilder s = new StrBuilder();
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.setLength(length);

		assertEquals(expected, actual);
	}

	@Test
	public void setLength554() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		int length = 0;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.setLength(length);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();

		assertEquals(sExpected, s);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void setLength555() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = -2147483647;
		int length = 0;
		s.setLength(length);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void setLength556() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		int length = 1073741824;
		s.setLength(length);
	}

	@Test
	public void setLength557() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u4976' };
		int length = 1;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ 0x0 };
		expected.size = 1;
		StrBuilder actual = s.setLength(length);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		sExpected.buffer = new char[]{ 0x0 };
		sExpected.size = 1;

		assertEquals(sExpected, s);
	}

	@Test
	public void setNewLineText558() {
		StrBuilder s = new StrBuilder();
		String newLine = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.setNewLineText(newLine);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();

		assertEquals(sExpected, s);
	}

	@Test
	public void setNullText559() {
		StrBuilder s = new StrBuilder();
		String nullText = null;
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.setNullText(nullText);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();

		assertEquals(sExpected, s);
	}

	@Test
	public void setNullText560() {
		StrBuilder s = new StrBuilder();
		String nullText = "";
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.setNullText(nullText);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();

		assertEquals(sExpected, s);
	}

	@Test
	public void setNullText561() throws IllegalAccessException, NoSuchFieldException {
		StrBuilder s = new StrBuilder();
		String nullText = "A";
		StrBuilder expected = new StrBuilder();
		final Field fieldNullText = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText.setAccessible(true);
		fieldNullText.set(expected, "A");
		StrBuilder actual = s.setNullText(nullText);

		assertEquals(expected, actual);

		StrBuilder sExpected = new StrBuilder();
		final Field fieldNullText2 = StrBuilder.class.getDeclaredField("nullText");
		fieldNullText2.setAccessible(true);
		fieldNullText2.set(sExpected, "A");

		assertEquals(sExpected, s);
	}

	@Test
	public void size562() {
		StrBuilder s = new StrBuilder();
		int expected = 0;
		int actual = s.size();

		assertEquals(expected, actual);
	}

	@Test
	public void startsWith563() {
		StrBuilder s = new StrBuilder();
		String str = null;
		boolean actual = s.startsWith(str);

		assertFalse(actual);
	}

	@Test
	public void startsWith564() {
		StrBuilder s = new StrBuilder();
		String str = "";
		boolean actual = s.startsWith(str);

		assertTrue(actual);
	}

	@Test
	public void startsWith565() {
		StrBuilder s = new StrBuilder();
		String str = "A";
		boolean actual = s.startsWith(str);

		assertFalse(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void startsWith566() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		String str = "A";
		s.startsWith(str);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void startsWith567() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		String str = "A";
		s.startsWith(str);
	}

	@Test
	public void startsWith568() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00a3' };
		s.size = 1;
		String str = "\u00ff";
		boolean actual = s.startsWith(str);

		assertFalse(actual);
	}

	@Test
	public void startsWith569() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '\u00df' };
		s.size = 1;
		String str = "\u00df";
		boolean actual = s.startsWith(str);

		assertTrue(actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void subSequence570() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int startIndex = 0;
		int endIndex = 0;
		s.subSequence(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void subSequence571() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = -1;
		int endIndex = 0;
		s.subSequence(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void subSequence572() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 1;
		int endIndex = 0;
		s.subSequence(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void subSequence573() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = 4210688;
		int startIndex = 1;
		int endIndex = -2147483648;
		s.subSequence(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void substring574() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = -1;
		int endIndex = 0;
		s.substring(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void substring575() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 1;
		int endIndex = 0;
		s.substring(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void substring576() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int start = 0;
		s.substring(start);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void substring577() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int start = -1;
		s.substring(start);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void toCharArray578() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = -1;
		int endIndex = 0;
		s.toCharArray(startIndex, endIndex);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void toCharArray579() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 1;
		int endIndex = 0;
		s.toCharArray(startIndex, endIndex);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void toCharArray580() {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		s.toCharArray();
	}

	@Test
	public void trim581() {
		StrBuilder s = new StrBuilder();
		s.size = -1073741824;
		StrBuilder expected = new StrBuilder();
		expected.size = -1073741824;
		StrBuilder actual = s.trim();

		assertEquals(expected, actual);
	}

	@Test
	public void trim582() {
		StrBuilder s = new StrBuilder();
		StrBuilder expected = new StrBuilder();
		StrBuilder actual = s.trim();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void trim583() {
		StrBuilder s = new StrBuilder();
		s.size = 1;
		s.trim();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void trim584() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{};
		s.size = 1;
		s.trim();
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void trim585() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '0' };
		s.size = 2;
		s.trim();
	}

	@Test
	public void trim586() {
		StrBuilder s = new StrBuilder();
		s.buffer = new char[]{ '!', '!' };
		s.size = 2;
		StrBuilder expected = new StrBuilder();
		expected.buffer = new char[]{ '!', '!' };
		expected.size = 2;
		StrBuilder actual = s.trim();

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void validateIndex587() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		s.size = -2147483648;
		int index = 0;
		s.validateIndex(index);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void validateIndex588() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int index = -1;
		s.validateIndex(index);
	}

	@Test
	public void validateIndex589() {
		StrBuilder s = new StrBuilder();
		s.size = 524288;
		int index = 0;
		s.validateIndex(index);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void validateRange590() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = -1;
		int endIndex = 0;
		s.validateRange(startIndex, endIndex);
	}

	@Test
	public void validateRange591() {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = 0;
		int expected = 0;
		int actual = s.validateRange(startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void validateRange592() {
		StrBuilder s = new StrBuilder();
		int startIndex = 0;
		int endIndex = 1;
		int expected = 0;
		int actual = s.validateRange(startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.StringIndexOutOfBoundsException.class)
	public void validateRange593() throws StringIndexOutOfBoundsException {
		StrBuilder s = new StrBuilder();
		int startIndex = 1;
		int endIndex = 0;
		s.validateRange(startIndex, endIndex);
	}
}
