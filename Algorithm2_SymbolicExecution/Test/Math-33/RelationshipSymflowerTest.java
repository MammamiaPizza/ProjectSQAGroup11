package org.apache.commons.math3.optimization.linear;

import org.junit.*;
import static org.junit.Assert.*;

public class RelationshipSymflowerTest {
	@Test
	public void toString1() {
		Relationship r = Relationship.EQ;
		String expected = "=";
		String actual = r.toString();

		assertEquals(expected, actual);
	}
}
