package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class AnnotationIntrospectorSymflowerTest {
	@Test
	public void referencePropertyReferenceProperty1() {
		AnnotationIntrospector.ReferenceProperty.Type t = ReferenceProperty.Type.BACK_REFERENCE;
		String n = "";
		AnnotationIntrospector.ReferenceProperty expected = new ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, "");
		ReferenceProperty actual = new AnnotationIntrospector.ReferenceProperty(t, n);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void referencePropertyBack2() {
		String name = "";
		AnnotationIntrospector.ReferenceProperty expected = new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, "");
		AnnotationIntrospector.ReferenceProperty actual = ReferenceProperty.back(name);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void referencePropertyGetName3() {
		AnnotationIntrospector.ReferenceProperty r = new AnnotationIntrospector.ReferenceProperty(null, "");
		String expected = "";
		String actual = r.getName();

		assertEquals(expected, actual);
	}

	@Test
	public void referencePropertyGetType4() {
		AnnotationIntrospector.ReferenceProperty r = new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, null);
		AnnotationIntrospector.ReferenceProperty.Type expected = AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE;
		AnnotationIntrospector.ReferenceProperty.Type actual = r.getType();

		assertEquals(expected, actual);
	}

	@Test
	public void referencePropertyIsBackReference5() {
		ReferenceProperty r = new AnnotationIntrospector.ReferenceProperty(null, null);
		boolean actual = r.isBackReference();

		assertFalse(actual);
	}

	@Test
	public void referencePropertyIsBackReference6() {
		AnnotationIntrospector.ReferenceProperty r = new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, null);
		boolean actual = r.isBackReference();

		assertTrue(actual);
	}

	@Test
	public void referencePropertyIsManagedReference7() {
		ReferenceProperty r = new AnnotationIntrospector.ReferenceProperty(null, null);
		boolean actual = r.isManagedReference();

		assertFalse(actual);
	}

	@Test
	public void referencePropertyIsManagedReference8() {
		AnnotationIntrospector.ReferenceProperty r = new AnnotationIntrospector.ReferenceProperty(ReferenceProperty.Type.MANAGED_REFERENCE, null);
		boolean actual = r.isManagedReference();

		assertTrue(actual);
	}

	@Test
	public void referencePropertyManaged9() {
		String name = null;
		AnnotationIntrospector.ReferenceProperty expected = new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE, null);
		AnnotationIntrospector.ReferenceProperty actual = ReferenceProperty.managed(name);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
