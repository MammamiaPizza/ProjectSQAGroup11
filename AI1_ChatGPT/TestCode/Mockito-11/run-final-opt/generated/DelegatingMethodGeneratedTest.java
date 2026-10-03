package org.mockito.internal.creation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Test;

public class DelegatingMethodGeneratedTest {

    public interface SampleContract {
        String combine(int number, String... values) throws IOException;
    }

    private Method sampleMethod() throws Exception {
        return SampleContract.class.getMethod("combine", int.class, String[].class);
    }

    @Test
    public void equalsReturnsTrueForSelf() throws Exception {
        DelegatingMethod delegatingMethod = new DelegatingMethod(sampleMethod());

        assertTrue(delegatingMethod.equals(delegatingMethod));
    }

    @Test
    public void equalsReturnsTrueForIndependentWrappersOfEqualMethods() throws Exception {
        Method firstMethod = sampleMethod();
        Method secondMethod = sampleMethod();
        DelegatingMethod first = new DelegatingMethod(firstMethod);
        DelegatingMethod second = new DelegatingMethod(secondMethod);

        assertTrue(firstMethod.equals(secondMethod));
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    @Test
    public void equalsReturnsTrueForWrappedJavaMethodAsDocumented() throws Exception {
        Method method = sampleMethod();
        DelegatingMethod delegatingMethod = new DelegatingMethod(method);

        assertTrue(delegatingMethod.equals(method));
    }

    @Test
    public void equalsReturnsFalseForNullAndUnrelatedObjects() throws Exception {
        DelegatingMethod delegatingMethod = new DelegatingMethod(sampleMethod());

        assertFalse(delegatingMethod.equals(null));
        assertFalse(delegatingMethod.equals("combine"));
    }

    @Test
    public void equalDelegatingMethodsHaveEqualHashCodes() throws Exception {
        DelegatingMethod first = new DelegatingMethod(sampleMethod());
        DelegatingMethod second = new DelegatingMethod(sampleMethod());

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void delegatesMethodMetadata() throws Exception {
        Method method = sampleMethod();
        DelegatingMethod delegatingMethod = new DelegatingMethod(method);

        assertSame(method, delegatingMethod.getJavaMethod());
        assertEquals("combine", delegatingMethod.getName());
        assertEquals(String.class, delegatingMethod.getReturnType());
        assertArrayEquals(new Class<?>[] { int.class, String[].class }, delegatingMethod.getParameterTypes());
        assertArrayEquals(new Class<?>[] { IOException.class }, delegatingMethod.getExceptionTypes());
        assertTrue(delegatingMethod.isVarArgs());
        assertTrue(delegatingMethod.isAbstract());
    }
}
