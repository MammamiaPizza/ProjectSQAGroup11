package org.mockito.internal.util.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

public class GenericMasterRegressionTest {

    private static class Fields {
        String plain;
        List<String> strings;
        List<List<String>> nestedLists;
    }

    @Captor
    private ArgumentCaptor<List<String>> listCaptor;

    @Test
    public void shouldReturnTheFirstConcreteGenericArgument() throws Exception {
        Field field = Fields.class.getDeclaredField("strings");

        assertEquals(String.class, new GenericMaster().getGenericType(field));
    }

    @Test
    public void shouldReturnRawTypeForNestedGenericArgument() throws Exception {
        Field field = Fields.class.getDeclaredField("nestedLists");

        assertEquals(List.class, new GenericMaster().getGenericType(field));
    }

    @Test
    public void shouldReturnObjectForNonParameterizedFields() throws Exception {
        Field field = Fields.class.getDeclaredField("plain");

        assertEquals(Object.class, new GenericMaster().getGenericType(field));
    }

    @Test
    public void shouldInitializeCaptorWhoseCapturedTypeIsParameterized() {
        MockitoAnnotations.initMocks(this);

        assertNotNull(listCaptor);
    }
}