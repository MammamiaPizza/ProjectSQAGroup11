package org.apache.commons.cli;

import org.junit.*;

public class DefaultParserSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void handleConcatenatedOptions1() throws ParseException {
		DefaultParser d = new DefaultParser();
		String token = null;
		d.handleConcatenatedOptions(token);
	}

	@Test
	public void handleConcatenatedOptions2() throws ParseException {
		DefaultParser d = new DefaultParser();
		String token = "";
		d.handleConcatenatedOptions(token);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void handleConcatenatedOptions3() throws ParseException {
		DefaultParser d = new DefaultParser();
		String token = "A\u007f";
		d.handleConcatenatedOptions(token);
	}
}
