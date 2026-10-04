package org.apache.commons.lang3;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StringUtilsSymflowerTest {
	@Test
	public void StringUtils1() {
		StringUtils expected = new StringUtils();
		StringUtils actual = new StringUtils();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void abbreviate2() {
		String str = null;
		int offset = 0;
		int maxWidth = 0;
		String actual = StringUtils.abbreviate(str, offset, maxWidth);

		assertNull(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviate3() throws IllegalArgumentException {
		String str = "";
		int offset = 0;
		int maxWidth = 0;
		StringUtils.abbreviate(str, offset, maxWidth);
	}

	@Test
	public void abbreviate4() {
		String str = "A";
		int offset = 0;
		int maxWidth = 4;
		String expected = "A";
		String actual = StringUtils.abbreviate(str, offset, maxWidth);

		assertEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviate5() throws IllegalArgumentException {
		String str = "BCDAEF";
		int offset = 6;
		int maxWidth = 4;
		StringUtils.abbreviate(str, offset, maxWidth);
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviate6() throws IllegalArgumentException {
		String str = "BCDEAFGHI";
		int offset = 5;
		int maxWidth = 4;
		StringUtils.abbreviate(str, offset, maxWidth);
	}

	@Test
	public void abbreviate7() {
		String str = null;
		int maxWidth = 0;
		String actual = StringUtils.abbreviate(str, maxWidth);

		assertNull(actual);
	}

	@Test
	public void capitalize8() {
		CharSequence cs = mock(CharSequence.class);
		String actual = StringUtils.capitalize(cs);

		assertNull(actual);
	}

	@Test
	public void capitalize9() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(0);
		when(cs.toString()).thenReturn(null);
		String actual = StringUtils.capitalize(cs);

		assertNull(actual);

		verify(cs, times(1)).length();
		verify(cs, times(1)).toString();
	}

	@Test
	public void center10() {
		String str = null;
		int size = 0;
		String padStr = null;
		String actual = StringUtils.center(str, size, padStr);

		assertNull(actual);
	}

	@Test
	public void center11() {
		String str = "";
		int size = 0;
		String padStr = null;
		String expected = "";
		String actual = StringUtils.center(str, size, padStr);

		assertEquals(expected, actual);
	}

	@Test
	public void center12() {
		String str = null;
		int size = 0;
		String actual = StringUtils.center(str, size);

		assertNull(actual);
	}

	@Test
	public void center13() {
		String str = null;
		int size = 0;
		char padChar = 0x0;
		String actual = StringUtils.center(str, size, padChar);

		assertNull(actual);
	}

	@Test
	public void center14() {
		String str = "";
		int size = 0;
		char padChar = 0x0;
		String expected = "";
		String actual = StringUtils.center(str, size, padChar);

		assertEquals(expected, actual);
	}

	@Test
	public void center15() {
		String str = "A";
		int size = 1;
		char padChar = 0x0;
		String expected = "A";
		String actual = StringUtils.center(str, size, padChar);

		assertEquals(expected, actual);
	}

	@Test
	public void contains16() {
		String str = null;
		String searchStr = null;
		boolean actual = StringUtils.contains(str, searchStr);

		assertFalse(actual);
	}

	@Test
	public void contains17() {
		String str = "";
		String searchStr = null;
		boolean actual = StringUtils.contains(str, searchStr);

		assertFalse(actual);
	}

	@Test
	public void contains18() {
		String str = "BACA";
		String searchStr = "A";
		boolean actual = StringUtils.contains(str, searchStr);

		assertTrue(actual);
	}

	@Test
	public void contains19() {
		String str = "E";
		String searchStr = "CA";
		boolean actual = StringUtils.contains(str, searchStr);

		assertFalse(actual);
	}

	@Test
	public void containsIgnoreCase20() {
		String str = null;
		String searchStr = null;
		boolean actual = StringUtils.containsIgnoreCase(str, searchStr);

		assertFalse(actual);
	}

	@Test
	public void containsIgnoreCase21() {
		String str = "";
		String searchStr = null;
		boolean actual = StringUtils.containsIgnoreCase(str, searchStr);

		assertFalse(actual);
	}

	@Test
	public void containsIgnoreCase22() {
		String str = "";
		String searchStr = "B";
		boolean actual = StringUtils.containsIgnoreCase(str, searchStr);

		assertFalse(actual);
	}

	@Test
	public void containsNone23() {
		CharSequence cs = mock(CharSequence.class);
		String invalidChars = null;
		boolean actual = StringUtils.containsNone(cs, invalidChars);

		assertTrue(actual);
	}

	@Test
	public void containsNone24() {
		CharSequence cs = mock(CharSequence.class);
		char[] searchChars = null;
		boolean actual = StringUtils.containsNone(cs, searchChars);

		assertTrue(actual);
	}

	@Test
	public void containsNone25() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		char[] searchChars = {};
		boolean actual = StringUtils.containsNone(cs, searchChars);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void containsNone26() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.charAt(anyInt())).thenReturn(0x0);
		when(cs.length()).thenReturn(1);
		char[] searchChars = {};
		boolean actual = StringUtils.containsNone(cs, searchChars);

		assertTrue(actual);

		verify(cs, times(1)).charAt(anyInt());
		verify(cs, times(1)).length();
	}

	@Test
	public void containsNone27() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.charAt(anyInt())).thenReturn(0x0);
		when(cs.length()).thenReturn(262144);
		char[] searchChars = { 0x0 };
		boolean actual = StringUtils.containsNone(cs, searchChars);

		assertFalse(actual);

		verify(cs, times(1)).charAt(anyInt());
		verify(cs, times(1)).length();
	}

	@Test
	public void defaultString28() {
		String str = null;
		String defaultStr = null;
		String actual = StringUtils.defaultString(str, defaultStr);

		assertNull(actual);
	}

	@Test
	public void defaultString29() {
		String str = "";
		String defaultStr = null;
		String expected = "";
		String actual = StringUtils.defaultString(str, defaultStr);

		assertEquals(expected, actual);
	}

	@Test
	public void defaultString30() {
		String str = null;
		String expected = "";
		String actual = StringUtils.defaultString(str);

		assertEquals(expected, actual);
	}

	@Test
	public void defaultString31() {
		String str = "";
		String expected = "";
		String actual = StringUtils.defaultString(str);

		assertEquals(expected, actual);
	}

	@Test
	public void difference32() {
		String str1 = null;
		String str2 = null;
		String actual = StringUtils.difference(str1, str2);

		assertNull(actual);
	}

	@Test
	public void difference33() {
		String str1 = "";
		String str2 = null;
		String expected = "";
		String actual = StringUtils.difference(str1, str2);

		assertEquals(expected, actual);
	}

	@Test
	public void difference34() {
		String str1 = "";
		String str2 = "";
		String expected = "";
		String actual = StringUtils.difference(str1, str2);

		assertEquals(expected, actual);
	}

	@Test
	public void endsWith35() {
		String str = "";
		String suffix = null;
		boolean actual = StringUtils.endsWith(str, suffix);

		assertFalse(actual);
	}

	@Test
	public void endsWithIgnoreCase36() {
		String str = "";
		String suffix = null;
		boolean actual = StringUtils.endsWithIgnoreCase(str, suffix);

		assertFalse(actual);
	}

	@Test
	public void equalsIgnoreCase37() {
		String str1 = null;
		String str2 = null;
		boolean actual = StringUtils.equalsIgnoreCase(str1, str2);

		assertTrue(actual);
	}

	@Test
	public void equalsIgnoreCase38() {
		String str1 = null;
		String str2 = "";
		boolean actual = StringUtils.equalsIgnoreCase(str1, str2);

		assertFalse(actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getLevenshteinDistance39() throws IllegalArgumentException {
		CharSequence s = mock(CharSequence.class);
		CharSequence t = mock(CharSequence.class);
		StringUtils.getLevenshteinDistance(s, t);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void getLevenshteinDistance40() {
		CharSequence s = mock(CharSequence.class);
		when(s.length()).thenReturn(-3);
		CharSequence t = mock(CharSequence.class);
		when(t.length()).thenReturn(4);
		StringUtils.getLevenshteinDistance(s, t);
	}

	@Test
	public void getLevenshteinDistance41() {
		CharSequence s = mock(CharSequence.class);
		when(s.length()).thenReturn(0);
		CharSequence t = mock(CharSequence.class);
		when(t.length()).thenReturn(0);
		int expected = 0;
		int actual = StringUtils.getLevenshteinDistance(s, t);

		assertEquals(expected, actual);

		verify(s, times(1)).length();
		verify(t, times(1)).length();
	}

	@Test
	public void getLevenshteinDistance42() {
		CharSequence s = mock(CharSequence.class);
		when(s.length()).thenReturn(1);
		CharSequence t = mock(CharSequence.class);
		when(t.length()).thenReturn(0);
		int expected = 1;
		int actual = StringUtils.getLevenshteinDistance(s, t);

		assertEquals(expected, actual);

		verify(s, times(1)).length();
		verify(t, times(1)).length();
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void getLevenshteinDistance43() {
		CharSequence s = mock(CharSequence.class);
		when(s.length()).thenReturn(2147483647);
		CharSequence t = mock(CharSequence.class);
		when(t.length()).thenReturn(2147483647);
		StringUtils.getLevenshteinDistance(s, t);
	}

	@Test
	public void indexOf44() {
		String str = null;
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.indexOf(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf45() {
		String str = "";
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.indexOf(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf46() {
		String str = "";
		String searchStr = "";
		int expected = 0;
		int actual = StringUtils.indexOf(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf47() {
		String str = null;
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.indexOf(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf48() {
		String str = "";
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.indexOf(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOf49() {
		String str = "";
		String searchStr = "";
		int startPos = 0;
		int expected = 0;
		int actual = StringUtils.indexOf(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny50() {
		String str = null;
		String[] searchStrs = null;
		int expected = -1;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny51() {
		String str = "";
		String[] searchStrs = null;
		int expected = -1;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny52() {
		String str = "";
		String[] searchStrs = {};
		int expected = -1;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny53() {
		String str = "";
		String[] searchStrs = { null };
		int expected = -1;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny54() {
		String str = "";
		String[] searchStrs = { "A" };
		int expected = -1;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny55() {
		String str = "";
		String[] searchStrs = { "", null, "" };
		int expected = 0;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfAny56() {
		String str = "A";
		String[] searchStrs = { "A" };
		int expected = 0;
		int actual = StringUtils.indexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfDifference57() {
		CharSequence cs1 = mock(CharSequence.class);
		CharSequence cs2 = mock(CharSequence.class);
		int expected = 0;
		int actual = StringUtils.indexOfDifference(cs1, cs2);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfDifference58() {
		CharSequence cs1 = mock(CharSequence.class);
		CharSequence cs2 = mock(CharSequence.class);
		int expected = -1;
		int actual = StringUtils.indexOfDifference(cs1, cs2);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfDifference59() {
		CharSequence[] css = null;
		int expected = -1;
		int actual = StringUtils.indexOfDifference(css);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfDifference60() {
		CharSequence[] css = {};
		int expected = -1;
		int actual = StringUtils.indexOfDifference(css);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase61() {
		String str = null;
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase62() {
		String str = null;
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase63() {
		String str = "";
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase64() {
		String str = "";
		String searchStr = "A";
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase65() {
		String str = "";
		String searchStr = "BC";
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase66() {
		String str = "";
		String searchStr = "DC";
		int startPos = -1;
		int expected = -1;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void indexOfIgnoreCase67() {
		String str = "A";
		String searchStr = "";
		int startPos = 0;
		int expected = 0;
		int actual = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void isAllLowerCase68() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isAllLowerCase(cs);

		assertFalse(actual);
	}

	@Test
	public void isAllUpperCase69() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isAllUpperCase(cs);

		assertFalse(actual);
	}

	@Test
	public void isAlpha70() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isAlpha(cs);

		assertFalse(actual);
	}

	@Test
	public void isAlpha71() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isAlpha(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isAlphaSpace72() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isAlphaSpace(cs);

		assertFalse(actual);
	}

	@Test
	public void isAlphaSpace73() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isAlphaSpace(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isAlphanumeric74() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isAlphanumeric(cs);

		assertFalse(actual);
	}

	@Test
	public void isAlphanumeric75() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isAlphanumeric(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isAlphanumericSpace76() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isAlphanumericSpace(cs);

		assertFalse(actual);
	}

	@Test
	public void isAlphanumericSpace77() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isAlphanumericSpace(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isBlank78() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isBlank(cs);

		assertTrue(actual);
	}

	@Test
	public void isBlank79() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isBlank(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isBlank80() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(0);
		boolean actual = StringUtils.isBlank(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isEmpty81() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isEmpty(cs);

		assertTrue(actual);
	}

	@Test
	public void isEmpty82() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(0);
		boolean actual = StringUtils.isEmpty(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isEmpty83() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(1);
		boolean actual = StringUtils.isEmpty(cs);

		assertFalse(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isNotEmpty84() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isNotEmpty(cs);

		assertFalse(actual);
	}

	@Test
	public void isNumeric85() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isNumeric(cs);

		assertFalse(actual);
	}

	@Test
	public void isNumeric86() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isNumeric(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isNumericSpace87() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isNumericSpace(cs);

		assertFalse(actual);
	}

	@Test
	public void isNumericSpace88() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isNumericSpace(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void isWhitespace89() {
		CharSequence cs = mock(CharSequence.class);
		boolean actual = StringUtils.isWhitespace(cs);

		assertFalse(actual);
	}

	@Test
	public void isWhitespace90() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(-2147483647);
		boolean actual = StringUtils.isWhitespace(cs);

		assertTrue(actual);

		verify(cs, times(1)).length();
	}

	@Test
	public void join91() {
		Object[] array = null;
		char separator = 0x0;
		int startIndex = 0;
		int endIndex = 0;
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertNull(actual);
	}

	@Test
	public void join92() {
		Object[] array = {};
		char separator = 0x0;
		int startIndex = 0;
		int endIndex = 0;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void join93() {
		Object[] array = {};
		char separator = 0x0;
		int startIndex = 0;
		int endIndex = 1;
		StringUtils.join(array, separator, startIndex, endIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void join94() {
		Object[] array = {};
		char separator = 0x0;
		int startIndex = 1;
		int endIndex = -2147483648;
		StringUtils.join(array, separator, startIndex, endIndex);
	}

	@Test
	public void join95() {
		Object[] array = { null };
		char separator = 0x0;
		int startIndex = 0;
		int endIndex = 1;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void join96() {
		Object[] array = { null };
		char separator = 0x0;
		int startIndex = 0;
		int endIndex = 2;
		StringUtils.join(array, separator, startIndex, endIndex);
	}

	@Test
	public void join97() {
		Object[] array = { null, null };
		char separator = 0x0;
		int startIndex = 1;
		int endIndex = -2147483648;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void join98() {
		Object[] array = null;
		String separator = null;
		int startIndex = 0;
		int endIndex = 0;
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertNull(actual);
	}

	@Test
	public void join99() {
		Object[] array = {};
		String separator = null;
		int startIndex = 0;
		int endIndex = 0;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void join100() {
		Object[] array = {};
		String separator = "";
		int startIndex = 0;
		int endIndex = 0;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void join101() {
		Object[] array = {};
		String separator = "";
		int startIndex = 0;
		int endIndex = 1;
		StringUtils.join(array, separator, startIndex, endIndex);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void join102() {
		Object[] array = {};
		String separator = "";
		int startIndex = 1;
		int endIndex = -2147483648;
		StringUtils.join(array, separator, startIndex, endIndex);
	}

	@Test
	public void join103() {
		Object[] array = { null };
		String separator = "A";
		int startIndex = 0;
		int endIndex = 1;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test // (expected = ArrayIndexOutOfBoundsException.class)
	public void join104() {
		Object[] array = { null };
		String separator = "A";
		int startIndex = 0;
		int endIndex = 2;
		StringUtils.join(array, separator, startIndex, endIndex);
	}

	@Test
	public void join105() {
		Object[] array = { null, null };
		String separator = "A";
		int startIndex = 1;
		int endIndex = -2147483648;
		String expected = "";
		String actual = StringUtils.join(array, separator, startIndex, endIndex);

		assertEquals(expected, actual);
	}

	@Test
	public void join106() {
		Object[] array = null;
		String separator = null;
		String actual = StringUtils.join(array, separator);

		assertNull(actual);
	}

	@Test
	public void join107() {
		Object[] array = {};
		String separator = "";
		String expected = "";
		String actual = StringUtils.join(array, separator);

		assertEquals(expected, actual);
	}

	@Test
	public void join108() {
		Object[] array = null;
		String actual = StringUtils.join(array);

		assertNull(actual);
	}

	@Test
	public void join109() {
		Object[] array = null;
		char separator = 0x0;
		String actual = StringUtils.join(array, separator);

		assertNull(actual);
	}

	@Test
	public void join110() {
		Object[] array = {};
		char separator = 0x0;
		String expected = "";
		String actual = StringUtils.join(array, separator);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf111() {
		String str = null;
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.lastIndexOf(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf112() {
		String str = "";
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.lastIndexOf(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf113() {
		String str = null;
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.lastIndexOf(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOf114() {
		String str = "";
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.lastIndexOf(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfAny115() {
		String str = null;
		String[] searchStrs = null;
		int expected = -1;
		int actual = StringUtils.lastIndexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfAny116() {
		String str = "";
		String[] searchStrs = null;
		int expected = -1;
		int actual = StringUtils.lastIndexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfAny117() {
		String str = "";
		String[] searchStrs = {};
		int expected = -1;
		int actual = StringUtils.lastIndexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfAny118() {
		String str = "";
		String[] searchStrs = { null };
		int expected = -1;
		int actual = StringUtils.lastIndexOfAny(str, searchStrs);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase119() {
		String str = null;
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase120() {
		String str = "";
		String searchStr = null;
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase121() {
		String str = "";
		String searchStr = "B";
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase122() {
		String str = null;
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase123() {
		String str = "";
		String searchStr = null;
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase124() {
		String str = "";
		String searchStr = "";
		int startPos = 0;
		int expected = 0;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase125() {
		String str = "";
		String searchStr = "A";
		int startPos = 0;
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastIndexOfIgnoreCase126() {
		String str = "A";
		String searchStr = "A";
		int startPos = -1;
		int expected = -1;
		int actual = StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);

		assertEquals(expected, actual);
	}

	@Test
	public void lastOrdinalIndexOf127() {
		String str = null;
		String searchStr = null;
		int ordinal = 0;
		int expected = -1;
		int actual = StringUtils.lastOrdinalIndexOf(str, searchStr, ordinal);

		assertEquals(expected, actual);
	}

	@Test
	public void left128() {
		String str = null;
		int len = 0;
		String actual = StringUtils.left(str, len);

		assertNull(actual);
	}

	@Test
	public void left129() {
		String str = "";
		int len = -1;
		String expected = "";
		String actual = StringUtils.left(str, len);

		assertEquals(expected, actual);
	}

	@Test
	public void left130() {
		String str = "";
		int len = 0;
		String expected = "";
		String actual = StringUtils.left(str, len);

		assertEquals(expected, actual);
	}

	@Test
	public void left131() {
		String str = "B";
		int len = 0;
		String expected = "";
		String actual = StringUtils.left(str, len);

		assertEquals(expected, actual);
	}

	@Test
	public void leftPad132() {
		String str = null;
		int size = 0;
		String padStr = null;
		String actual = StringUtils.leftPad(str, size, padStr);

		assertNull(actual);
	}

	@Test
	public void leftPad133() {
		String str = null;
		int size = 0;
		String actual = StringUtils.leftPad(str, size);

		assertNull(actual);
	}

	@Test
	public void leftPad134() {
		String str = null;
		int size = 0;
		char padChar = 0x0;
		String actual = StringUtils.leftPad(str, size, padChar);

		assertNull(actual);
	}

	@Test
	public void leftPad135() {
		String str = "";
		int size = 0;
		char padChar = 0x0;
		String expected = "";
		String actual = StringUtils.leftPad(str, size, padChar);

		assertEquals(expected, actual);
	}

	@Test
	public void length136() {
		CharSequence cs = mock(CharSequence.class);
		int expected = 0;
		int actual = StringUtils.length(cs);

		assertEquals(expected, actual);
	}

	@Test
	public void lowerCase137() {
		String str = null;
		String actual = StringUtils.lowerCase(str);

		assertNull(actual);
	}

	@Test
	public void mid138() {
		String str = null;
		int pos = 0;
		int len = 0;
		String actual = StringUtils.mid(str, pos, len);

		assertNull(actual);
	}

	@Test
	public void mid139() {
		String str = "";
		int pos = 0;
		int len = -1;
		String expected = "";
		String actual = StringUtils.mid(str, pos, len);

		assertEquals(expected, actual);
	}

	@Test
	public void mid140() {
		String str = "";
		int pos = 1;
		int len = 0;
		String expected = "";
		String actual = StringUtils.mid(str, pos, len);

		assertEquals(expected, actual);
	}

	@Test
	public void ordinalIndexOf141() {
		String str = null;
		String searchStr = null;
		int ordinal = 0;
		int expected = -1;
		int actual = StringUtils.ordinalIndexOf(str, searchStr, ordinal);

		assertEquals(expected, actual);
	}

	@Test
	public void overlay142() {
		String str = null;
		String overlay = null;
		int start = 0;
		int end = 0;
		String actual = StringUtils.overlay(str, overlay, start, end);

		assertNull(actual);
	}

	@Test
	public void repeat143() {
		String str = null;
		String separator = null;
		int repeat = 0;
		String actual = StringUtils.repeat(str, separator, repeat);

		assertNull(actual);
	}

	@Test
	public void repeat144() {
		String str = "";
		String separator = null;
		int repeat = 0;
		String expected = "";
		String actual = StringUtils.repeat(str, separator, repeat);

		assertEquals(expected, actual);
	}

	@Test
	public void repeat145() {
		String str = "";
		String separator = null;
		int repeat = 1;
		String expected = "";
		String actual = StringUtils.repeat(str, separator, repeat);

		assertEquals(expected, actual);
	}

	@Test
	public void repeat146() {
		String str = "";
		String separator = null;
		int repeat = 2;
		String expected = "";
		String actual = StringUtils.repeat(str, separator, repeat);

		assertEquals(expected, actual);
	}

	@Test
	public void repeat147() {
		String str = null;
		int repeat = 0;
		String actual = StringUtils.repeat(str, repeat);

		assertNull(actual);
	}

	@Test
	public void repeat148() {
		String str = "";
		int repeat = 0;
		String expected = "";
		String actual = StringUtils.repeat(str, repeat);

		assertEquals(expected, actual);
	}

	@Test
	public void repeat149() {
		String str = "";
		int repeat = 1;
		String expected = "";
		String actual = StringUtils.repeat(str, repeat);

		assertEquals(expected, actual);
	}

	@Test
	public void repeat150() {
		String str = "";
		int repeat = 2;
		String expected = "";
		String actual = StringUtils.repeat(str, repeat);

		assertEquals(expected, actual);
	}

	@Test // (expected = NegativeArraySizeException.class)
	public void repeat151() {
		String str = "AB";
		int repeat = 1073741824;
		StringUtils.repeat(str, repeat);
	}

	@Test
	public void repeat152() {
		String str = "ABC";
		int repeat = 2;
		String expected = "ABCABC";
		String actual = StringUtils.repeat(str, repeat);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceChars153() {
		String str = null;
		char searchChar = 0x0;
		char replaceChar = 0x0;
		String actual = StringUtils.replaceChars(str, searchChar, replaceChar);

		assertNull(actual);
	}

	@Test
	public void replaceChars154() {
		String str = "A";
		char searchChar = '\u00ff';
		char replaceChar = 0x0;
		String expected = "A";
		String actual = StringUtils.replaceChars(str, searchChar, replaceChar);

		assertEquals(expected, actual);
	}

	@Test
	public void replaceEach155() {
		String text = null;
		String[] searchList = null;
		String[] replacementList = null;
		String actual = StringUtils.replaceEach(text, searchList, replacementList);

		assertNull(actual);
	}

	@Test
	public void replaceEachRepeatedly156() {
		String text = null;
		String[] searchList = null;
		String[] replacementList = null;
		String actual = StringUtils.replaceEachRepeatedly(text, searchList, replacementList);

		assertNull(actual);
	}

	@Test
	public void replaceEachRepeatedly157() {
		String text = null;
		String[] searchList = {};
		String[] replacementList = null;
		String actual = StringUtils.replaceEachRepeatedly(text, searchList, replacementList);

		assertNull(actual);
	}

	@Test
	public void reverse158() {
		String str = null;
		String actual = StringUtils.reverse(str);

		assertNull(actual);
	}

	@Test
	public void right159() {
		String str = null;
		int len = 0;
		String actual = StringUtils.right(str, len);

		assertNull(actual);
	}

	@Test
	public void right160() {
		String str = "";
		int len = -1;
		String expected = "";
		String actual = StringUtils.right(str, len);

		assertEquals(expected, actual);
	}

	@Test
	public void right161() {
		String str = "";
		int len = 0;
		String expected = "";
		String actual = StringUtils.right(str, len);

		assertEquals(expected, actual);
	}

	@Test
	public void right162() {
		String str = "B";
		int len = 0;
		String expected = "";
		String actual = StringUtils.right(str, len);

		assertEquals(expected, actual);
	}

	@Test
	public void rightPad163() {
		String str = null;
		int size = 0;
		String padStr = null;
		String actual = StringUtils.rightPad(str, size, padStr);

		assertNull(actual);
	}

	@Test
	public void rightPad164() {
		String str = null;
		int size = 0;
		String actual = StringUtils.rightPad(str, size);

		assertNull(actual);
	}

	@Test
	public void rightPad165() {
		String str = null;
		int size = 0;
		char padChar = 0x0;
		String actual = StringUtils.rightPad(str, size, padChar);

		assertNull(actual);
	}

	@Test
	public void rightPad166() {
		String str = "";
		int size = 0;
		char padChar = 0x0;
		String expected = "";
		String actual = StringUtils.rightPad(str, size, padChar);

		assertEquals(expected, actual);
	}

	@Test
	public void startsWith167() {
		String str = "";
		String prefix = null;
		boolean actual = StringUtils.startsWith(str, prefix);

		assertFalse(actual);
	}

	@Test
	public void startsWithIgnoreCase168() {
		String str = "";
		String prefix = null;
		boolean actual = StringUtils.startsWithIgnoreCase(str, prefix);

		assertFalse(actual);
	}

	@Test
	public void stripAll169() {
		String[] strs = null;
		String stripChars = null;
		String[] actual = StringUtils.stripAll(strs, stripChars);

		assertNull(actual);
	}

	@Test
	public void stripAll170() {
		String[] strs = {};
		String stripChars = null;
		String[] expected = {};
		String[] actual = StringUtils.stripAll(strs, stripChars);

		assertArrayEquals(expected, actual);
	}

	@Test
	public void stripAll171() {
		String[] strs = null;
		String[] actual = StringUtils.stripAll(strs);

		assertNull(actual);
	}

	@Test
	public void stripEnd172() {
		String str = null;
		String stripChars = null;
		String actual = StringUtils.stripEnd(str, stripChars);

		assertNull(actual);
	}

	@Test
	public void stripEnd173() {
		String str = "";
		String stripChars = null;
		String expected = "";
		String actual = StringUtils.stripEnd(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripEnd174() {
		String str = "A";
		String stripChars = "";
		String expected = "A";
		String actual = StringUtils.stripEnd(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripEnd175() {
		String str = "\u00fe";
		String stripChars = "\u00fe\u00fe";
		String expected = "";
		String actual = StringUtils.stripEnd(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripEnd176() {
		String str = "\u00ff";
		String stripChars = "B";
		String expected = "\u00ff";
		String actual = StringUtils.stripEnd(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripStart177() {
		String str = null;
		String stripChars = null;
		String actual = StringUtils.stripStart(str, stripChars);

		assertNull(actual);
	}

	@Test
	public void stripStart178() {
		String str = "";
		String stripChars = null;
		String expected = "";
		String actual = StringUtils.stripStart(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripStart179() {
		String str = ">";
		String stripChars = "B";
		String expected = ">";
		String actual = StringUtils.stripStart(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripStart180() {
		String str = "A";
		String stripChars = "";
		String expected = "A";
		String actual = StringUtils.stripStart(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripStart181() {
		String str = "\u00fe";
		String stripChars = "\u00fe\u00fe";
		String expected = "";
		String actual = StringUtils.stripStart(str, stripChars);

		assertEquals(expected, actual);
	}

	@Test
	public void stripToEmpty182() {
		String str = null;
		String expected = "";
		String actual = StringUtils.stripToEmpty(str);

		assertEquals(expected, actual);
	}

	@Test
	public void stripToNull183() {
		String str = null;
		String actual = StringUtils.stripToNull(str);

		assertNull(actual);
	}

	@Test
	public void substring184() {
		String str = null;
		int start = 0;
		int end = 0;
		String actual = StringUtils.substring(str, start, end);

		assertNull(actual);
	}

	@Test
	public void substring185() {
		String str = "";
		int start = -1;
		int end = -1;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring186() {
		String str = "";
		int start = -1;
		int end = 0;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring187() {
		String str = "";
		int start = 0;
		int end = -1;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring188() {
		String str = "";
		int start = 1;
		int end = 1;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring189() {
		String str = "A";
		int start = 1;
		int end = 0;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring190() {
		String str = "AB";
		int start = -1;
		int end = 0;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring191() {
		String str = "B";
		int start = 0;
		int end = 0;
		String expected = "";
		String actual = StringUtils.substring(str, start, end);

		assertEquals(expected, actual);
	}

	@Test
	public void substring192() {
		String str = null;
		int start = 0;
		String actual = StringUtils.substring(str, start);

		assertNull(actual);
	}

	@Test
	public void substring193() {
		String str = "";
		int start = -1;
		String expected = "";
		String actual = StringUtils.substring(str, start);

		assertEquals(expected, actual);
	}

	@Test
	public void substring194() {
		String str = "";
		int start = 1;
		String expected = "";
		String actual = StringUtils.substring(str, start);

		assertEquals(expected, actual);
	}

	@Test
	public void substring195() {
		String str = "A";
		int start = 0;
		String expected = "A";
		String actual = StringUtils.substring(str, start);

		assertEquals(expected, actual);
	}

	@Test
	public void substring196() {
		String str = "B";
		int start = -1;
		String expected = "B";
		String actual = StringUtils.substring(str, start);

		assertEquals(expected, actual);
	}

	@Test
	public void substringBetween197() {
		String str = null;
		String tag = null;
		String actual = StringUtils.substringBetween(str, tag);

		assertNull(actual);
	}

	@Test
	public void substringBetween198() {
		String str = null;
		String open = null;
		String close = null;
		String actual = StringUtils.substringBetween(str, open, close);

		assertNull(actual);
	}

	@Test
	public void substringBetween199() {
		String str = "";
		String open = null;
		String close = null;
		String actual = StringUtils.substringBetween(str, open, close);

		assertNull(actual);
	}

	@Test
	public void substringBetween200() {
		String str = "";
		String open = "";
		String close = null;
		String actual = StringUtils.substringBetween(str, open, close);

		assertNull(actual);
	}

	@Test
	public void substringBetween201() {
		String str = "";
		String open = "A";
		String close = "";
		String actual = StringUtils.substringBetween(str, open, close);

		assertNull(actual);
	}

	@Test
	public void substringBetween202() {
		String str = "E";
		String open = "";
		String close = "EA";
		String actual = StringUtils.substringBetween(str, open, close);

		assertNull(actual);
	}

	@Test
	public void swapCase203() {
		String str = null;
		String actual = StringUtils.swapCase(str);

		assertNull(actual);
	}

	@Test
	public void swapCase204() {
		String str = "";
		String expected = "";
		String actual = StringUtils.swapCase(str);

		assertEquals(expected, actual);
	}

	@Test
	public void trim205() {
		String str = null;
		String actual = StringUtils.trim(str);

		assertNull(actual);
	}

	@Test
	public void trimToEmpty206() {
		String str = null;
		String expected = "";
		String actual = StringUtils.trimToEmpty(str);

		assertEquals(expected, actual);
	}

	@Test
	public void uncapitalize207() {
		CharSequence cs = mock(CharSequence.class);
		String actual = StringUtils.uncapitalize(cs);

		assertNull(actual);
	}

	@Test
	public void uncapitalize208() {
		CharSequence cs = mock(CharSequence.class);
		when(cs.length()).thenReturn(0);
		when(cs.toString()).thenReturn(null);
		String actual = StringUtils.uncapitalize(cs);

		assertNull(actual);

		verify(cs, times(1)).length();
		verify(cs, times(1)).toString();
	}

	@Test
	public void upperCase209() {
		String str = null;
		String actual = StringUtils.upperCase(str);

		assertNull(actual);
	}
}
