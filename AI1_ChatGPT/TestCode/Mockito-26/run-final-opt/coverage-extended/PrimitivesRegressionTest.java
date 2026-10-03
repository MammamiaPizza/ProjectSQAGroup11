package org.mockito.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrimitivesRegressionTest {

    @Test
    public void shouldReturnDoubleDefaultAsDoubleAndAllowTypedAssignment() {
        Double value = Primitives.primitiveValueOrNullFor(double.class);

        assertEquals(Double.class, value.getClass());
        assertEquals(Double.valueOf(0D), value);
    }

    @Test
    public void shouldReturnCorrectDefaultValueAndWrapperTypeForEveryPrimitive() {
        Class<?>[] primitiveTypes = {
                boolean.class, char.class, byte.class, short.class,
                int.class, long.class, float.class, double.class
        };
        Object[] expectedValues = {
                Boolean.FALSE, Character.valueOf('\u0000'), Byte.valueOf((byte) 0), Short.valueOf((short) 0),
                Integer.valueOf(0), Long.valueOf(0L), Float.valueOf(0F), Double.valueOf(0D)
        };

        for (int i = 0; i < primitiveTypes.length; i++) {
            Object actual = Primitives.primitiveValueOrNullFor(primitiveTypes[i]);

            assertEquals("wrong wrapper type for " + primitiveTypes[i],
                    expectedValues[i].getClass(), actual.getClass());
            assertEquals("wrong default value for " + primitiveTypes[i],
                    expectedValues[i], actual);
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void shouldConvertPrimitiveWrappersToPrimitiveTypesAndLeavePrimitiveTypesUnchanged() {
        Class<?>[] wrapperTypes = {
                Boolean.class, Character.class, Byte.class, Short.class,
                Integer.class, Long.class, Float.class, Double.class
        };
        Class<?>[] primitiveTypes = {
                boolean.class, char.class, byte.class, short.class,
                int.class, long.class, float.class, double.class
        };

        for (int i = 0; i < wrapperTypes.length; i++) {
            assertEquals(primitiveTypes[i], Primitives.primitiveTypeOf((Class) wrapperTypes[i]));
            assertEquals(primitiveTypes[i], Primitives.primitiveTypeOf((Class) primitiveTypes[i]));
        }
    }

    @Test
    public void shouldRecognizePrimitiveWrappersAndReturnTheirDefaultWrapperValues() {
        Class<?>[] wrapperTypes = {
                Boolean.class, Character.class, Byte.class, Short.class,
                Integer.class, Long.class, Float.class, Double.class
        };
        Object[] expectedValues = {
                Boolean.FALSE, Character.valueOf('\u0000'), Byte.valueOf((byte) 0), Short.valueOf((short) 0),
                Integer.valueOf(0), Long.valueOf(0L), Float.valueOf(0F), Double.valueOf(0D)
        };

        for (int i = 0; i < wrapperTypes.length; i++) {
            assertTrue(wrapperTypes[i] + " should be a primitive wrapper",
                    Primitives.isPrimitiveWrapper(wrapperTypes[i]));

            Object actual = Primitives.primitiveWrapperOf(wrapperTypes[i]);
            assertEquals(expectedValues[i].getClass(), actual.getClass());
            assertEquals(expectedValues[i], actual);
        }

        assertFalse(Primitives.isPrimitiveWrapper(String.class));
    }
}
