package com.fasterxml.jackson.core.util;

import org.junit.*;
import static org.junit.Assert.*;

public class DefaultPrettyPrinterSymflowerTest {
	@Test
	public void fixedSpaceIndenterIsInline1() {
		DefaultPrettyPrinter.FixedSpaceIndenter f = new DefaultPrettyPrinter.FixedSpaceIndenter();
		boolean actual = f.isInline();

		assertTrue(actual);
	}

	@Test
	public void nopIndenterIsInline2() {
		DefaultPrettyPrinter.NopIndenter n = new DefaultPrettyPrinter.NopIndenter();
		boolean actual = n.isInline();

		assertTrue(actual);
	}
}
