package com.fasterxml.jackson.core.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class TokenFilterSymflowerTest {
	@Test
	public void TokenFilter58() {
		TokenFilter expected = new TokenFilter();
		TokenFilter actual = new TokenFilter();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void _includeScalar59() {
		TokenFilter t = new TokenFilter();
		boolean actual = t._includeScalar();

		assertTrue(actual);
	}

	@Test
	public void filterFinishArray60() {
		TokenFilter t = new TokenFilter();
		t.filterFinishArray();
	}

	@Test
	public void filterFinishObject61() {
		TokenFilter t = new TokenFilter();
		t.filterFinishObject();
	}

	@Test
	public void filterStartArray62() {
		TokenFilter t = new TokenFilter();
		TokenFilter expected = new TokenFilter();
		TokenFilter actual = t.filterStartArray();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void filterStartObject63() {
		TokenFilter t = new TokenFilter();
		TokenFilter expected = new TokenFilter();
		TokenFilter actual = t.filterStartObject();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void includeBinary64() {
		TokenFilter t = new TokenFilter();
		boolean actual = t.includeBinary();

		assertTrue(actual);
	}

	@Test
	public void includeBoolean65() {
		TokenFilter t = new TokenFilter();
		boolean value = false;
		boolean actual = t.includeBoolean(value);

		assertTrue(actual);
	}

	@Test
	public void includeElement66() {
		TokenFilter t = new TokenFilter();
		int index = 0;
		TokenFilter expected = new TokenFilter();
		TokenFilter actual = t.includeElement(index);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void includeEmbeddedValue67() {
		TokenFilter t = new TokenFilter();
		Object ob = null;
		boolean actual = t.includeEmbeddedValue(ob);

		assertTrue(actual);
	}

	@Test
	public void includeNull68() {
		TokenFilter t = new TokenFilter();
		boolean actual = t.includeNull();

		assertTrue(actual);
	}

	@Test
	public void includeNumber69() {
		TokenFilter t = new TokenFilter();
		float v = Float.NaN;
		boolean actual = t.includeNumber(v);

		assertTrue(actual);
	}

	@Test
	public void includeNumber70() {
		TokenFilter t = new TokenFilter();
		double v = Double.NaN;
		boolean actual = t.includeNumber(v);

		assertTrue(actual);
	}

	@Test
	public void includeNumber71() {
		TokenFilter t = new TokenFilter();
		int v = 0;
		boolean actual = t.includeNumber(v);

		assertTrue(actual);
	}

	@Test
	public void includeNumber72() {
		TokenFilter t = new TokenFilter();
		long v = 0L;
		boolean actual = t.includeNumber(v);

		assertTrue(actual);
	}

	@Test
	public void includeProperty73() {
		TokenFilter t = new TokenFilter();
		String name = null;
		TokenFilter expected = new TokenFilter();
		TokenFilter actual = t.includeProperty(name);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void includeRawValue74() {
		TokenFilter t = new TokenFilter();
		boolean actual = t.includeRawValue();

		assertTrue(actual);
	}

	@Test
	public void includeRootValue75() {
		TokenFilter t = new TokenFilter();
		int index = 0;
		TokenFilter expected = new TokenFilter();
		TokenFilter actual = t.includeRootValue(index);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void includeString76() {
		TokenFilter t = new TokenFilter();
		String value = null;
		boolean actual = t.includeString(value);

		assertTrue(actual);
	}
}
