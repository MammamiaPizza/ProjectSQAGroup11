package org.apache.commons.lang3.builder;

import org.junit.*;
import static org.junit.Assert.*;

public class ToStringBuilderSymflowerTest {
	@Test
	public void getObject62() {
		ToStringBuilder t = new ToStringBuilder(null);
		Object actual = t.getObject();

		assertNull(actual);
	}

	@Test
	public void getStringBuffer63() {
		ToStringBuilder t = new ToStringBuilder(null);
		StringBuffer actual = t.getStringBuffer();

		assertNull(actual);
	}
}
