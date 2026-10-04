package org.apache.commons.math.geometry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.*;
import static org.junit.Assert.*;

public class RotationOrderSymflowerTest {
	@Test
	public void toString11() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor c = RotationOrder.class.getDeclaredConstructor(String.class, Vector3D.class, Vector3D.class, Vector3D.class);
		c.setAccessible(true);
		RotationOrder r = (RotationOrder) c.newInstance(null, null, null, null);
		String actual = r.toString();

		assertNull(actual);
	}
}
