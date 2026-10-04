package com.fasterxml.jackson.databind.introspect;

import org.junit.*;

public class JacksonAnnotationIntrospectorSymflowerTest {
	@Test // (expected = java.lang.NullPointerException.class)
	public void _propertyName1() {
		JacksonAnnotationIntrospector j = new JacksonAnnotationIntrospector();
		String localName = null;
		String namespace = null;
		j._propertyName(localName, namespace);
	}
}
