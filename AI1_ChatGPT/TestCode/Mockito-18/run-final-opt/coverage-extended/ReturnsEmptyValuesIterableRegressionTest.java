package org.mockito.internal.stubbing.defaultanswers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

public class ReturnsEmptyValuesIterableRegressionTest {

    private interface ReturnTypes {
        Iterable<?> iterable();
        Collection<?> collection();
        Set<?> set();
        HashSet<?> hashSet();
        SortedSet<?> sortedSet();
        TreeSet<?> treeSet();
        LinkedHashSet<?> linkedHashSet();
        List<?> list();
        LinkedList<?> linkedList();
        ArrayList<?> arrayList();
        Map<?, ?> map();
        HashMap<?, ?> hashMap();
        SortedMap<?, ?> sortedMap();
        TreeMap<?, ?> treeMap();
        LinkedHashMap<?, ?> linkedHashMap();
        int primitiveInt();
        Integer wrapperInteger();
        boolean primitiveBoolean();
        Character wrapperCharacter();
        String string();
    }

    @Test
    public void shouldReturnNonNullEmptyIterable() throws Throwable {
        Object value = answerFor(ReturnTypes.class.getMethod("iterable"));

        assertNotNull(value);
        assertTrue(value instanceof Iterable);
        assertFalse(((Iterable<?>) value).iterator().hasNext());
    }

    @Test
    public void shouldReturnEmptyCollectionTypes() {
        ReturnsEmptyValues answer = new ReturnsEmptyValues();

        Class<?>[] returnTypes = new Class<?>[] {
                Collection.class, Set.class, HashSet.class, SortedSet.class,
                TreeSet.class, LinkedHashSet.class, List.class, LinkedList.class,
                ArrayList.class, Map.class, HashMap.class, SortedMap.class,
                TreeMap.class, LinkedHashMap.class
        };

        Class<?>[] expectedImplementations = new Class<?>[] {
                LinkedList.class, HashSet.class, HashSet.class, TreeSet.class,
                TreeSet.class, LinkedHashSet.class, LinkedList.class, LinkedList.class,
                ArrayList.class, HashMap.class, HashMap.class, TreeMap.class,
                TreeMap.class, LinkedHashMap.class
        };

        for (int i = 0; i < returnTypes.length; i++) {
            Object value = answer.returnValueFor(returnTypes[i]);

            assertNotNull("No value for " + returnTypes[i], value);
            assertEquals(expectedImplementations[i], value.getClass());
            if (value instanceof Collection) {
                assertTrue(((Collection<?>) value).isEmpty());
            } else {
                assertTrue(((Map<?, ?>) value).isEmpty());
            }
        }
    }

    @Test
    public void shouldUseEmptyCollectionDispatchWhenAnsweringInvocation() throws Throwable {
        Object listValue = answerFor(ReturnTypes.class.getMethod("list"));
        Object mapValue = answerFor(ReturnTypes.class.getMethod("map"));

        assertTrue(listValue instanceof List);
        assertTrue(((List<?>) listValue).isEmpty());
        assertTrue(mapValue instanceof Map);
        assertTrue(((Map<?, ?>) mapValue).isEmpty());
    }

    @Test
    public void shouldReturnPrimitiveAndWrapperDefaults() {
        ReturnsEmptyValues answer = new ReturnsEmptyValues();

        assertEquals(Integer.valueOf(0), answer.returnValueFor(Integer.TYPE));
        assertEquals(Integer.valueOf(0), answer.returnValueFor(Integer.class));
        assertEquals(Boolean.FALSE, answer.returnValueFor(Boolean.TYPE));
        assertEquals(Character.valueOf('\0'), answer.returnValueFor(Character.class));
    }

    @Test
    public void shouldReturnNullForUnsupportedReferenceTypes() {
        ReturnsEmptyValues answer = new ReturnsEmptyValues();

        assertNull(answer.returnValueFor(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnModifiableFreshCollections() {
        ReturnsEmptyValues answer = new ReturnsEmptyValues();

        List<Object> first = (List<Object>) answer.returnValueFor(List.class);
        List<Object> second = (List<Object>) answer.returnValueFor(List.class);

        assertTrue(first != second);
        first.add("value");
        assertEquals(1, first.size());
        assertTrue(second.isEmpty());
    }

    private Object answerFor(Method method) throws Throwable {
        InvocationOnMock invocation = mock(InvocationOnMock.class);
        when(invocation.getMethod()).thenReturn(method);
        return new ReturnsEmptyValues().answer(invocation);
    }
}
