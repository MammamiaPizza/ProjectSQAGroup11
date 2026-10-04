package org.joda.time.format;

import java.lang.reflect.Field;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormatterBuilder.PeriodFieldAffix;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PeriodFormatterBuilderSymflowerTest {
	@Test
	public void compositeAffixCompositeAffix1() {
		PeriodFormatterBuilder.PeriodFieldAffix left = mock(PeriodFieldAffix.class);
		PeriodFormatterBuilder.PeriodFieldAffix right = mock(PeriodFieldAffix.class);
		PeriodFormatterBuilder.CompositeAffix expected = new PeriodFormatterBuilder.CompositeAffix(null, null);
		PeriodFormatterBuilder.CompositeAffix actual = new PeriodFormatterBuilder.CompositeAffix(left, right);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void compositeAffixCalculatePrintedLength2() {
		PeriodFormatterBuilder.CompositeAffix c = new PeriodFormatterBuilder.CompositeAffix(null, null);
		int value = 0;
		c.calculatePrintedLength(value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void compositeAffixParse3() {
		PeriodFormatterBuilder.CompositeAffix c = new PeriodFormatterBuilder.CompositeAffix(null, null);
		String periodStr = null;
		int position = 0;
		c.parse(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void compositeAffixPrintTo4() {
		PeriodFormatterBuilder.CompositeAffix c = new PeriodFormatterBuilder.CompositeAffix(null, null);
		StringBuffer buf = null;
		int value = 0;
		c.printTo(buf, value);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void compositeAffixScan5() {
		PeriodFormatterBuilder.CompositeAffix c = new PeriodFormatterBuilder.CompositeAffix(null, null);
		String periodStr = null;
		int position = 0;
		c.scan(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void fieldFormatterFieldFormatter6() {
		PeriodFormatterBuilder.FieldFormatter field = null;
		PeriodFieldAffix suffix = mock(PeriodFieldAffix.class);
		new PeriodFormatterBuilder.FieldFormatter(field, suffix);
	}

	@Test
	public void fieldFormatterFieldFormatter7() {
		PeriodFormatterBuilder.FieldFormatter field = new PeriodFormatterBuilder.FieldFormatter(null, null);
		PeriodFieldAffix suffix = mock(PeriodFieldAffix.class);
		PeriodFormatterBuilder.FieldFormatter expected = new PeriodFormatterBuilder.FieldFormatter(null, null);
		PeriodFormatterBuilder.FieldFormatter actual = new PeriodFormatterBuilder.FieldFormatter(field, suffix);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void fieldFormatterFieldFormatter8() {
		int minPrintedDigits = 0;
		int printZeroSetting = 0;
		int maxParsedDigits = 0;
		boolean rejectSignedValues = false;
		int fieldType = 0;
		PeriodFormatterBuilder.FieldFormatter[] fieldFormatters = null;
		PeriodFormatterBuilder.PeriodFieldAffix prefix = mock(PeriodFormatterBuilder.PeriodFieldAffix.class);
		PeriodFormatterBuilder.PeriodFieldAffix suffix = mock(PeriodFormatterBuilder.PeriodFieldAffix.class);
		PeriodFormatterBuilder.FieldFormatter expected = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		PeriodFormatterBuilder.FieldFormatter actual = new PeriodFormatterBuilder.FieldFormatter(minPrintedDigits, printZeroSetting, maxParsedDigits, rejectSignedValues, fieldType, fieldFormatters, prefix, suffix);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void fieldFormatterGetFieldType9() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		int expected = 0;
		int actual = f.getFieldType();

		assertEquals(expected, actual);
	}

	@Test
	public void fieldFormatterIsZero10() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadablePeriod period = mock(ReadablePeriod.class);
		when(period.size()).thenReturn(-2147483647);
		boolean actual = f.isZero(period);

		assertTrue(actual);

		verify(period, times(1)).size();
	}

	@Test
	public void fieldFormatterIsZero11() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadablePeriod period = mock(ReadablePeriod.class);
		when(period.getValue(anyInt())).thenReturn(0);
		when(period.size()).thenReturn(1);
		boolean actual = f.isZero(period);

		assertTrue(actual);

		verify(period, times(1)).getValue(anyInt());
		verify(period, times(1)).size();
	}

	@Test
	public void fieldFormatterIsZero12() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadablePeriod period = mock(ReadablePeriod.class);
		when(period.getValue(anyInt())).thenReturn(1);
		when(period.size()).thenReturn(1);
		boolean actual = f.isZero(period);

		assertFalse(actual);

		verify(period, times(1)).getValue(anyInt());
		verify(period, times(1)).size();
	}

	@Test
	public void fieldFormatterSetFieldValue13() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		int field = 8;
		int value = 0;
		f.setFieldValue(period, field, value);
	}

	@Test
	public void fieldFormatterSetFieldValue14() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setYears(anyInt());
		int field = 0;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setYears(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue15() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setMonths(anyInt());
		int field = 1;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setMonths(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue16() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setWeeks(anyInt());
		int field = 2;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setWeeks(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue17() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setDays(anyInt());
		int field = 3;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setDays(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue18() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setHours(anyInt());
		int field = 4;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setHours(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue19() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setMinutes(anyInt());
		int field = 5;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setMinutes(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue20() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setSeconds(anyInt());
		int field = 6;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setSeconds(anyInt());
	}

	@Test
	public void fieldFormatterSetFieldValue21() {
		PeriodFormatterBuilder.FieldFormatter f = new PeriodFormatterBuilder.FieldFormatter(0, 0, 0, false, 0, null, null, null);
		ReadWritablePeriod period = mock(ReadWritablePeriod.class);
		doNothing().when(period).setMillis(anyInt());
		int field = 7;
		int value = 0;
		f.setFieldValue(period, field, value);

		verify(period, times(1)).setMillis(anyInt());
	}

	@Test
	public void literalLiteral22() {
		String text = null;
		PeriodFormatterBuilder.Literal expected = new PeriodFormatterBuilder.Literal(null);
		PeriodFormatterBuilder.Literal actual = new PeriodFormatterBuilder.Literal(text);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void pluralAffixPluralAffix23() {
		String singularText = null;
		String pluralText = null;
		PeriodFormatterBuilder.PluralAffix expected = new PeriodFormatterBuilder.PluralAffix(null, null);
		PeriodFormatterBuilder.PluralAffix actual = new PeriodFormatterBuilder.PluralAffix(singularText, pluralText);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixCalculatePrintedLength24() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix(null, null);
		int value = 0;
		p.calculatePrintedLength(value);
	}

	@Test
	public void pluralAffixCalculatePrintedLength25() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix(null, "");
		int value = 0;
		int expected = 0;
		int actual = p.calculatePrintedLength(value);

		assertEquals(expected, actual);
	}

	@Test
	public void pluralAffixCalculatePrintedLength26() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix("", null);
		int value = 1;
		int expected = 0;
		int actual = p.calculatePrintedLength(value);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixParse27() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix(null, null);
		String periodStr = null;
		int position = 0;
		p.parse(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixParse28() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix(null, "");
		String periodStr = null;
		int position = 0;
		p.parse(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixParse29() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix("A", "A");
		String periodStr = null;
		int position = 0;
		p.parse(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixScan30() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix(null, null);
		String periodStr = null;
		int position = 0;
		p.scan(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixScan31() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix(null, "");
		String periodStr = null;
		int position = 0;
		p.scan(periodStr, position);
	}

	@Test
	public void pluralAffixScan32() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix("", "A");
		String periodStr = "";
		int position = 0;
		int expected = -1;
		int actual = p.scan(periodStr, position);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void pluralAffixScan33() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix("A", "A");
		String periodStr = null;
		int position = 0;
		p.scan(periodStr, position);
	}

	@Test
	public void pluralAffixScan34() {
		PeriodFormatterBuilder.PluralAffix p = new PeriodFormatterBuilder.PluralAffix("B", "");
		String periodStr = "";
		int position = 0;
		int expected = -1;
		int actual = p.scan(periodStr, position);

		assertEquals(expected, actual);
	}

	@Test
	public void simpleAffixSimpleAffix35() {
		String text = null;
		PeriodFormatterBuilder.SimpleAffix expected = new PeriodFormatterBuilder.SimpleAffix(null);
		PeriodFormatterBuilder.SimpleAffix actual = new PeriodFormatterBuilder.SimpleAffix(text);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void simpleAffixCalculatePrintedLength36() {
		PeriodFormatterBuilder.SimpleAffix s = new PeriodFormatterBuilder.SimpleAffix(null);
		int value = 0;
		s.calculatePrintedLength(value);
	}

	@Test
	public void simpleAffixCalculatePrintedLength37() {
		PeriodFormatterBuilder.SimpleAffix s = new PeriodFormatterBuilder.SimpleAffix("");
		int value = 0;
		int expected = 0;
		int actual = s.calculatePrintedLength(value);

		assertEquals(expected, actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void simpleAffixParse38() {
		PeriodFormatterBuilder.SimpleAffix s = new PeriodFormatterBuilder.SimpleAffix(null);
		String periodStr = null;
		int position = 0;
		s.parse(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void simpleAffixParse39() {
		PeriodFormatterBuilder.SimpleAffix s = new PeriodFormatterBuilder.SimpleAffix("");
		String periodStr = null;
		int position = 0;
		s.parse(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void simpleAffixScan40() {
		PeriodFormatterBuilder.SimpleAffix s = new PeriodFormatterBuilder.SimpleAffix(null);
		String periodStr = null;
		int position = 0;
		s.scan(periodStr, position);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void simpleAffixScan41() {
		PeriodFormatterBuilder.SimpleAffix s = new PeriodFormatterBuilder.SimpleAffix("");
		String periodStr = null;
		int position = 0;
		s.scan(periodStr, position);
	}

	@Test(expected = IllegalArgumentException.class)
	public void append42() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		PeriodPrinter printer = mock(PeriodPrinter.class);
		PeriodParser parser = mock(PeriodParser.class);
		p.append(printer, parser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendLiteral43() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String text = null;
		p.appendLiteral(text);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendPrefix44() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String singularText = null;
		String pluralText = null;
		p.appendPrefix(singularText, pluralText);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendPrefix45() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String singularText = "";
		String pluralText = null;
		p.appendPrefix(singularText, pluralText);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendPrefix46() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String text = null;
		p.appendPrefix(text);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendSuffix47() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String singularText = null;
		String pluralText = null;
		p.appendSuffix(singularText, pluralText);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendSuffix48() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String singularText = "";
		String pluralText = null;
		p.appendSuffix(singularText, pluralText);
	}

	@Test(expected = IllegalArgumentException.class)
	public void appendSuffix49() throws IllegalArgumentException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		String text = null;
		p.appendSuffix(text);
	}

	@Test
	public void maximumParsedDigits50() {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		int maxDigits = 0;
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		PeriodFormatterBuilder actual = p.maximumParsedDigits(maxDigits);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void minimumPrintedDigits51() {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		int minDigits = 0;
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		PeriodFormatterBuilder actual = p.minimumPrintedDigits(minDigits);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void printZeroAlways52() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting.setAccessible(true);
		fieldIPrintZeroSetting.set(expected, 4);
		PeriodFormatterBuilder actual = p.printZeroAlways();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting2 = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting2.setAccessible(true);
		fieldIPrintZeroSetting2.set(pExpected, 4);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void printZeroIfSupported53() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting.setAccessible(true);
		fieldIPrintZeroSetting.set(expected, 3);
		PeriodFormatterBuilder actual = p.printZeroIfSupported();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting2 = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting2.setAccessible(true);
		fieldIPrintZeroSetting2.set(pExpected, 3);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void printZeroNever54() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting.setAccessible(true);
		fieldIPrintZeroSetting.set(expected, 5);
		PeriodFormatterBuilder actual = p.printZeroNever();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting2 = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting2.setAccessible(true);
		fieldIPrintZeroSetting2.set(pExpected, 5);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void printZeroRarelyFirst55() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting.setAccessible(true);
		fieldIPrintZeroSetting.set(expected, 1);
		PeriodFormatterBuilder actual = p.printZeroRarelyFirst();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting2 = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting2.setAccessible(true);
		fieldIPrintZeroSetting2.set(pExpected, 1);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void printZeroRarelyLast56() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting.setAccessible(true);
		fieldIPrintZeroSetting.set(expected, 2);
		PeriodFormatterBuilder actual = p.printZeroRarelyLast();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();
		final Field fieldIPrintZeroSetting2 = PeriodFormatterBuilder.class.getDeclaredField("iPrintZeroSetting");
		fieldIPrintZeroSetting2.setAccessible(true);
		fieldIPrintZeroSetting2.set(pExpected, 2);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void rejectSignedValues57() {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		boolean v = false;
		PeriodFormatterBuilder expected = new PeriodFormatterBuilder();
		PeriodFormatterBuilder actual = p.rejectSignedValues(v);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));

		PeriodFormatterBuilder pExpected = new PeriodFormatterBuilder();

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void toParser58() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		final Field fieldINotParser = PeriodFormatterBuilder.class.getDeclaredField("iNotParser");
		fieldINotParser.setAccessible(true);
		fieldINotParser.set(p, true);
		PeriodParser actual = p.toParser();

		assertNull(actual);
	}

	@Test
	public void toPrinter59() throws IllegalAccessException, NoSuchFieldException {
		PeriodFormatterBuilder p = new PeriodFormatterBuilder();
		final Field fieldINotPrinter = PeriodFormatterBuilder.class.getDeclaredField("iNotPrinter");
		fieldINotPrinter.setAccessible(true);
		fieldINotPrinter.set(p, true);
		PeriodPrinter actual = p.toPrinter();

		assertNull(actual);
	}
}
