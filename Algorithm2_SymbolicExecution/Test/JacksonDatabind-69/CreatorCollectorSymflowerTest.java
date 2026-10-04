package com.fasterxml.jackson.databind.deser.impl;

import org.junit.*;
import static org.junit.Assert.*;

public class CreatorCollectorSymflowerTest {
	@Test
	public void vanillaCanCreateUsingDefault1() {
		CreatorCollector.Vanilla v = new CreatorCollector.Vanilla(0);
		boolean actual = v.canCreateUsingDefault();

		assertTrue(actual);
	}

	@Test
	public void vanillaCanInstantiate2() {
		CreatorCollector.Vanilla v = new CreatorCollector.Vanilla(0);
		boolean actual = v.canInstantiate();

		assertTrue(actual);
	}

	@Test
	public void vanillaGetValueTypeDesc3() {
		CreatorCollector.Vanilla v = new CreatorCollector.Vanilla(0);
		String expected = "java.lang.Object";
		String actual = v.getValueTypeDesc();

		assertEquals(expected, actual);
	}
}
