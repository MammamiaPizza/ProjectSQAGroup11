package org.mockito.internal.creation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

public class DelegatingMethodEqualityTest {

    private static class Sample {
        public String first(String value) {
            return value;
        }

        public void second() {
        }
    }

    @Test
    public void equals_should_return_true_when_self() throws Exception {
        DelegatingMethod delegatingMethod = new DelegatingMethod(firstMethod());

        assertTrue(delegatingMethod.equals(delegatingMethod));
    }

    @Test
    public void equals_should_return_true_for_distinct_wrappers_of_equal_methods() throws Exception {
        DelegatingMethod first = new DelegatingMethod(firstMethod());
        DelegatingMethod second = new DelegatingMethod(firstMethod());

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    @Test
    public void equal_delegating_methods_should_have_same_hash_code() throws Exception {
        DelegatingMethod first = new DelegatingMethod(firstMethod());
        DelegatingMethod second = new DelegatingMethod(firstMethod());

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_should_return_false_for_wrapper_of_different_method() throws Exception {
        DelegatingMethod first = new DelegatingMethod(firstMethod());
        DelegatingMethod second = new DelegatingMethod(secondMethod());

        assertFalse(first.equals(second));
    }

    @Test
    public void equals_should_return_false_for_null_and_unrelated_objects() throws Exception {
        DelegatingMethod delegatingMethod = new DelegatingMethod(firstMethod());

        assertFalse(delegatingMethod.equals(null));
        assertFalse(delegatingMethod.equals("first"));
    }

    private Method firstMethod() throws Exception {
        return Sample.class.getDeclaredMethod("first", String.class);
    }

    private Method secondMethod() throws Exception {
        return Sample.class.getDeclaredMethod("second");
    }
}
