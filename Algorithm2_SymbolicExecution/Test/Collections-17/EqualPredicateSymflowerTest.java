package org.apache.commons.collections.functors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class EqualPredicateSymflowerTest {
	@Test
	public void EqualPredicate1() {
		Object object = null;
		Equator<Object> equator = null;
		EqualPredicate<Object> expected = new EqualPredicate<Object>(null, null);
		EqualPredicate<Object> actual = new EqualPredicate(object, equator);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
