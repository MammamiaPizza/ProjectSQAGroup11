package org.jsoup.select;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class EvaluatorSymflowerTest {
	@Test
	public void allElementsToString1() {
		Evaluator.AllElements a = new Evaluator.AllElements();
		String expected = "*";
		String actual = a.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void attributeAttribute2() {
		String key = null;
		Evaluator.Attribute expected = new Evaluator.Attribute(null);
		Evaluator.Attribute actual = new Evaluator.Attribute(key);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void attributeStartingAttributeStarting3() {
		String keyPrefix = null;
		Evaluator.AttributeStarting expected = new Evaluator.AttributeStarting(null);
		Evaluator.AttributeStarting actual = new Evaluator.AttributeStarting(keyPrefix);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void classClass4() {
		String className = null;
		Evaluator.Class expected = new Evaluator.Class(null);
		Evaluator.Class actual = new Evaluator.Class(className);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void containsOwnTextContainsOwnText5() {
		String searchText = null;
		new Evaluator.ContainsOwnText(searchText);
	}

	@Test // (expected = java.lang.NullPointerException.class)
	public void containsTextContainsText6() {
		String searchText = null;
		new Evaluator.ContainsText(searchText);
	}

	@Test
	public void idId7() {
		String id = null;
		Evaluator.Id expected = new Evaluator.Id(null);
		Evaluator.Id actual = new Evaluator.Id(id);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void indexEqualsIndexEquals8() {
		int index = 0;
		Evaluator.IndexEquals expected = new Evaluator.IndexEquals(0);
		Evaluator.IndexEquals actual = new Evaluator.IndexEquals(index);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void indexGreaterThanIndexGreaterThan9() {
		int index = 0;
		Evaluator.IndexGreaterThan expected = new Evaluator.IndexGreaterThan(0);
		Evaluator.IndexGreaterThan actual = new Evaluator.IndexGreaterThan(index);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void indexLessThanIndexLessThan10() {
		int index = 0;
		Evaluator.IndexLessThan expected = new Evaluator.IndexLessThan(0);
		Evaluator.IndexLessThan actual = new Evaluator.IndexLessThan(index);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void tagTag11() {
		String tagName = null;
		Evaluator.Tag expected = new Evaluator.Tag(null);
		Evaluator.Tag actual = new Evaluator.Tag(tagName);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
