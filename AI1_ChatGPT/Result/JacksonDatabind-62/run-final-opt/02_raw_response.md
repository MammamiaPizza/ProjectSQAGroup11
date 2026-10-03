package com.fasterxml.jackson.databind.creators;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.module.SimpleModule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ArrayDelegatorCreatorForCollectionRegressionTest
{
    private static final Set<Object> PROTOTYPE =
            Collections.<Object>unmodifiableSet(new LinkedHashSet<Object>());

    private static final Class<?> UNMODIFIABLE_SET_CLASS = PROTOTYPE.getClass();

    @Test
    public void deserializesUnmodifiableSetUsingArrayDelegateCreator() throws Exception
    {
        Collection<?> result = read("[\"first\",\"second\",\"first\"]");

        assertEquals(UNMODIFIABLE_SET_CLASS, result.getClass());
        assertEquals(new LinkedHashSet<Object>(Arrays.<Object>asList("first", "second")), result);
        assertEquals(2, result.size());
    }

    @Test
    public void arrayDelegateCreatorHandlesEmptyArrayAndReturnsImmutableCollection() throws Exception
    {
        Collection<?> result = read("[]");

        assertEquals(UNMODIFIABLE_SET_CLASS, result.getClass());
        assertTrue(result.isEmpty());

        try {
            @SuppressWarnings("unchecked")
            Collection<Object> mutableView = (Collection<Object>) result;
            mutableView.add("not-allowed");
            fail("Collection created by the array delegate should remain unmodifiable");
        } catch (UnsupportedOperationException expected) {
            assertTrue(result.isEmpty());
        }
    }

    private Collection<?> read(String json) throws Exception
    {
        Object value = mapperWithArrayDelegateCreator().readValue(json, UNMODIFIABLE_SET_CLASS);
        assertTrue(value instanceof Collection<?>);
        return (Collection<?>) value;
    }

    private ObjectMapper mapperWithArrayDelegateCreator()
    {
        SimpleModule module = new SimpleModule();
        module.setValueInstantiators(new ValueInstantiators.Base() {
            @Override
            public ValueInstantiator findValueInstantiator(
                    DeserializationConfig config,
                    BeanDescription beanDesc,
                    ValueInstantiator defaultInstantiator) {
                if (beanDesc.getBeanClass() == UNMODIFIABLE_SET_CLASS) {
                    return new UnmodifiableSetArrayInstantiator();
                }
                return defaultInstantiator;
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }

    private static class UnmodifiableSetArrayInstantiator extends ValueInstantiator.Base
    {
        public UnmodifiableSetArrayInstantiator()
        {
            super(UNMODIFIABLE_SET_CLASS);
        }

        @Override
        public boolean canCreateUsingArrayDelegate()
        {
            return true;
        }

        @Override
        public JavaType getArrayDelegateType(DeserializationConfig config)
        {
            return config.constructType(Object[].class);
        }

        @Override
        public Object createUsingArrayDelegate(
                DeserializationContext ctxt, Object delegate)
        {
            Object[] values = (Object[]) delegate;
            Set<Object> result = new LinkedHashSet<Object>();
            result.addAll(Arrays.asList(values));
            return Collections.unmodifiableSet(result);
        }
    }
}