package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import org.junit.*;
import static org.junit.Assert.*;

public class JsonGeneratorSymflowerTest {
	@Test
	public void featureEnabledByDefault1() {
		Feature f = Feature.AUTO_CLOSE_TARGET;
		boolean actual = f.enabledByDefault();

		assertTrue(actual);
	}

	@Test
	public void featureEnabledIn2() {
		Feature f = Feature.AUTO_CLOSE_TARGET;
		int flags = 0;
		boolean actual = f.enabledIn(flags);

		assertFalse(actual);
	}

	@Test
	public void featureEnabledIn3() {
		Feature f = Feature.AUTO_CLOSE_TARGET;
		int flags = 1;
		boolean actual = f.enabledIn(flags);

		assertTrue(actual);
	}

	@Test
	public void featureGetMask4() {
		Feature f = Feature.AUTO_CLOSE_TARGET;
		int expected = 1;
		int actual = f.getMask();

		assertEquals(expected, actual);
	}

	@Test
	public void featureCollectDefaults5() {
		int expected = 31;
		int actual = Feature.collectDefaults();

		assertEquals(expected, actual);
	}
}
