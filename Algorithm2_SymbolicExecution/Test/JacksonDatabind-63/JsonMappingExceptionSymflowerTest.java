package com.fasterxml.jackson.databind;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class JsonMappingExceptionSymflowerTest {
	@Test
	public void referenceReference1() {
		Object from = null;
		int index = 0;
		JsonMappingException.Reference expected = new JsonMappingException.Reference(null, 0);
		JsonMappingException.Reference actual = new JsonMappingException.Reference(from, index);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void referenceReference2() {
		Object from = null;
		JsonMappingException.Reference expected = new JsonMappingException.Reference(null);
		JsonMappingException.Reference actual = new JsonMappingException.Reference(from);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void referenceReference3() {
		Object from = null;
		String fieldName = null;
		new JsonMappingException.Reference(from, fieldName);
	}

	@Test
	public void referenceReference4() {
		Object from = null;
		String fieldName = "";
		JsonMappingException.Reference expected = new JsonMappingException.Reference(null, null);
		expected.setIndex(-1);
		expected.setFieldName("");
		JsonMappingException.Reference actual = new JsonMappingException.Reference(from, fieldName);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void referenceReference5() {
		JsonMappingException.Reference expected = new JsonMappingException.Reference();
		JsonMappingException.Reference actual = new JsonMappingException.Reference();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void referenceGetFieldName6() {
		JsonMappingException.Reference r = new JsonMappingException.Reference(null, 0);
		String actual = r.getFieldName();

		assertNull(actual);
	}

	@Test
	public void referenceGetFrom7() {
		JsonMappingException.Reference r = new JsonMappingException.Reference(new Object(), 0);
		Object expected = new Object();
		Object actual = r.getFrom();

		assertEquals(expected, actual);
	}

	@Test
	public void referenceGetIndex8() {
		JsonMappingException.Reference r = new JsonMappingException.Reference(null, 0);
		int expected = 0;
		int actual = r.getIndex();

		assertEquals(expected, actual);
	}

	@Test
	public void referenceSetDescription9() {
		JsonMappingException.Reference r = new JsonMappingException.Reference(null, 0);
		String d = null;
		r.setDescription(d);

		JsonMappingException.Reference rExpected = new JsonMappingException.Reference(null, 0);

		assertTrue(EqualsBuilder.reflectionEquals(rExpected, r, false, null, true));
	}

	@Test
	public void referenceSetFieldName10() {
		JsonMappingException.Reference r = new JsonMappingException.Reference(null, 0);
		String n = null;
		r.setFieldName(n);

		JsonMappingException.Reference rExpected = new JsonMappingException.Reference(null, 0);

		assertTrue(EqualsBuilder.reflectionEquals(rExpected, r, false, null, true));
	}

	@Test
	public void referenceSetIndex11() {
		JsonMappingException.Reference r = new JsonMappingException.Reference(null, 0);
		int ix = 0;
		r.setIndex(ix);

		JsonMappingException.Reference rExpected = new JsonMappingException.Reference(null, 0);

		assertTrue(EqualsBuilder.reflectionEquals(rExpected, r, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void wrapWithPath12() {
		Throwable src = null;
		Object refFrom = null;
		String refFieldName = null;
		JsonMappingException.wrapWithPath(src, refFrom, refFieldName);
	}
}
