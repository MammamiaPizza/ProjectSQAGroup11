package org.apache.commons.cli;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

public class OptionSymflowerTest {
	@Test
	public void acceptsArg1() {
		Option o = new Option(null, null);
		o.setArgs(-2);
		boolean actual = o.acceptsArg();

		assertTrue(actual);
	}

	@Test // (expected = java.lang.UnsupportedOperationException.class)
	public void addValue2() throws UnsupportedOperationException {
		Option o = new Option(null, null);
		String value = null;
		o.addValue(value);
	}

	@Test // (expected = java.lang.RuntimeException.class)
	public void addValueForProcessing3() throws RuntimeException {
		Option o = new Option(null, null);
		o.setArgs(-1);
		String value = null;
		o.addValueForProcessing(value);
	}

	@Test
	public void equals4() {
		Option o2 = new Option(null, null);
		Object o = null;
		boolean actual = o2.equals(o);

		assertFalse(actual);
	}

	@Test
	public void equals5() {
		Option o2 = new Option(null, null);
		Object o = new Option(null, null);
		boolean actual = o2.equals(o);

		assertTrue(actual);
	}

	@Test
	public void getArgName6() {
		Option o = new Option(null, null);
		String actual = o.getArgName();

		assertNull(actual);
	}

	@Test
	public void getArgs7() {
		Option o = new Option(null, null);
		int expected = 0;
		int actual = o.getArgs();

		assertEquals(expected, actual);
	}

	@Test
	public void getDescription8() {
		Option o = new Option(null, null);
		String actual = o.getDescription();

		assertNull(actual);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void getId9() {
		Option o = new Option(null, null);
		o.getId();
	}

	@Test
	public void getId10() {
		Option o = new Option(null, null);
		o.setLongOpt("A");
		int expected = 65;
		int actual = o.getId();

		assertEquals(expected, actual);
	}

	@Test
	public void getId11() throws IllegalAccessException, NoSuchFieldException {
		Option o = new Option(null, null);
		final Field fieldOpt = Option.class.getDeclaredField("opt");
		fieldOpt.setAccessible(true);
		fieldOpt.set(o, "A");
		int expected = 65;
		int actual = o.getId();

		assertEquals(expected, actual);
	}

	@Test
	public void getKey12() {
		Option o = new Option(null, null);
		String actual = o.getKey();

		assertNull(actual);
	}

	@Test
	public void getKey13() throws IllegalAccessException, NoSuchFieldException {
		Option o = new Option(null, null);
		final Field fieldOpt = Option.class.getDeclaredField("opt");
		fieldOpt.setAccessible(true);
		fieldOpt.set(o, "");
		String expected = "";
		String actual = o.getKey();

		assertEquals(expected, actual);
	}

	@Test
	public void getLongOpt14() {
		Option o = new Option(null, null);
		String actual = o.getLongOpt();

		assertNull(actual);
	}

	@Test
	public void getOpt15() {
		Option o = new Option(null, null);
		String actual = o.getOpt();

		assertNull(actual);
	}

	@Test
	public void getType16() {
		Option o = new Option(null, null);
		o.setType(new Class<Object>("", "", ""));
		Object expected = new Object();
		Object actual = o.getType();

		assertEquals(expected, actual);
	}

	@Test
	public void getValueSeparator17() {
		Option o = new Option(null, null);
		char expected = 0x0;
		char actual = o.getValueSeparator();

		assertEquals(expected, actual);
	}

	@Test
	public void getValuesList18() {
		Option o = new Option(null, null);
		List<Object> actual = o.getValuesList();

		assertNull(actual);
	}

	@Test
	public void hasArg19() {
		Option o = new Option(null, null);
		o.setArgs(-2);
		boolean actual = o.hasArg();

		assertTrue(actual);
	}

	@Test
	public void hasArg20() {
		Option o = new Option(null, null);
		boolean actual = o.hasArg();

		assertFalse(actual);
	}

	@Test
	public void hasArg21() {
		Option o = new Option(null, null);
		o.setArgs(1);
		boolean actual = o.hasArg();

		assertTrue(actual);
	}

	@Test
	public void hasArgName22() {
		Option o = new Option(null, null);
		boolean actual = o.hasArgName();

		assertFalse(actual);
	}

	@Test
	public void hasArgName23() {
		Option o = new Option(null, null);
		o.setArgName("");
		boolean actual = o.hasArgName();

		assertFalse(actual);
	}

	@Test
	public void hasArgName24() {
		Option o = new Option(null, null);
		o.setArgName("A");
		boolean actual = o.hasArgName();

		assertTrue(actual);
	}

	@Test
	public void hasArgs25() {
		Option o = new Option(null, null);
		o.setArgs(-2);
		boolean actual = o.hasArgs();

		assertTrue(actual);
	}

	@Test
	public void hasArgs26() {
		Option o = new Option(null, null);
		boolean actual = o.hasArgs();

		assertFalse(actual);
	}

	@Test
	public void hasArgs27() {
		Option o = new Option(null, null);
		o.setArgs(2);
		boolean actual = o.hasArgs();

		assertTrue(actual);
	}

	@Test
	public void hasLongOpt28() {
		Option o = new Option(null, null);
		boolean actual = o.hasLongOpt();

		assertFalse(actual);
	}

	@Test
	public void hasLongOpt29() {
		Option o = new Option(null, null);
		o.setLongOpt("");
		boolean actual = o.hasLongOpt();

		assertTrue(actual);
	}

	@Test
	public void hasOptionalArg30() {
		Option o = new Option(null, null);
		boolean actual = o.hasOptionalArg();

		assertFalse(actual);
	}

	@Test
	public void hasValueSeparator31() {
		Option o = new Option(null, null);
		boolean actual = o.hasValueSeparator();

		assertFalse(actual);
	}

	@Test
	public void hasValueSeparator32() {
		Option o = new Option(null, null);
		o.setValueSeparator(0x1);
		boolean actual = o.hasValueSeparator();

		assertTrue(actual);
	}

	@Test
	public void hashCode33() {
		Option o = new Option(null, null);
		int expected = 0;
		int actual = o.hashCode();

		assertEquals(expected, actual);
	}

	@Test
	public void isRequired34() {
		Option o = new Option(null, null);
		boolean actual = o.isRequired();

		assertFalse(actual);
	}

	@Test
	public void requiresArg35() {
		Option o = new Option(null, null);
		o.setOptionalArg(true);
		boolean actual = o.requiresArg();

		assertFalse(actual);
	}

	@Test
	public void setArgName36() {
		Option o = new Option(null, null);
		String argName = null;
		o.setArgName(argName);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setArgs37() {
		Option o = new Option(null, null);
		int num = 0;
		o.setArgs(num);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setDescription38() {
		Option o = new Option(null, null);
		String description = null;
		o.setDescription(description);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setLongOpt39() {
		Option o = new Option(null, null);
		String longOpt = null;
		o.setLongOpt(longOpt);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setOptionalArg40() {
		Option o = new Option(null, null);
		boolean optionalArg = false;
		o.setOptionalArg(optionalArg);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setRequired41() {
		Option o = new Option(null, null);
		boolean required = false;
		o.setRequired(required);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setType42() {
		Option o = new Option(null, null);
		Class type = null;
		o.setType(type);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}

	@Test
	public void setValueSeparator43() {
		Option o = new Option(null, null);
		char sep = 0x0;
		o.setValueSeparator(sep);

		Option oExpected = new Option(null, null);

		assertEquals(oExpected, o);
	}
}
