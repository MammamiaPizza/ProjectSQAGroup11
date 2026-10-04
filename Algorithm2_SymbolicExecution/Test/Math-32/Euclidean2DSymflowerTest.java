package org.apache.commons.math3.geometry.euclidean.twod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class Euclidean2DSymflowerTest {
	@Test
	public void getDimension1() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = Euclidean2D.class.getDeclaredConstructor();
		c.setAccessible(true);
		Euclidean2D e = (Euclidean2D) c.newInstance();
		int expected = 2;
		int actual = e.getDimension();

		assertEquals(expected, actual);
	}

	@Test
	public void getInstance2() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = Euclidean2D.class.getDeclaredConstructor();
		c.setAccessible(true);
		Euclidean2D expected = (Euclidean2D) c.newInstance();
		Euclidean2D actual = Euclidean2D.getInstance();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}
}
