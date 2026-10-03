package com.fasterxml.jackson.databind.ser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;

public class PropertyBuilderBug1256Test
{
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class AtomicReferenceBean1256 {
        public AtomicReference<String> a = new AtomicReference<String>();

        public AtomicReferenceBean1256() { }

        public AtomicReferenceBean1256(String value) {
            a = new AtomicReference<String>(value);
        }
    }

    public static class DefaultConstructibleBean {
        public String value = "default";
    }

    public static class NoDefaultConstructorBean {
        public NoDefaultConstructorBean(String value) { }
    }

    private static class ExposedPropertyBuilder extends PropertyBuilder {
        public ExposedPropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
            super(config, beanDesc);
        }

        public Object defaultValue(JavaType type) {
            return getDefaultValue(type);
        }

        public Object defaultBean() {
            return getDefaultBean();
        }
    }

    private ExposedPropertyBuilder propertyBuilderFor(ObjectMapper mapper, Class<?> type) {
        SerializationConfig config = mapper.getSerializationConfig();
        JavaType javaType = mapper.constructType(type);
        return new ExposedPropertyBuilder(config, config.introspect(javaType));
    }

    @Test
    public void emptyAtomicReferenceIsSuppressedAsNonDefault() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{}", mapper.writeValueAsString(new AtomicReferenceBean1256()));
    }

    @Test
    public void nonEmptyAtomicReferenceIsSerializedAsNonDefaultValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("{\"a\":\"value\"}",
                mapper.writeValueAsString(new AtomicReferenceBean1256("value")));
    }

    @Test
    public void defaultValueUsesEmptyMarkerForReferenceAndContainerTypes() {
        ObjectMapper mapper = new ObjectMapper();
        ExposedPropertyBuilder builder = propertyBuilderFor(mapper, AtomicReferenceBean1256.class);

        assertSame(JsonInclude.Include.NON_EMPTY,
                builder.defaultValue(mapper.constructType(AtomicReference.class)));
        assertSame(JsonInclude.Include.NON_EMPTY,
                builder.defaultValue(mapper.constructType(ArrayList.class)));
    }

    @Test
    public void defaultValueHandlesStringPrimitiveAndOrdinaryObjectTypes() {
        ObjectMapper mapper = new ObjectMapper();
        ExposedPropertyBuilder builder = propertyBuilderFor(mapper, AtomicReferenceBean1256.class);

        assertEquals("", builder.defaultValue(mapper.constructType(String.class)));
        assertEquals(Integer.valueOf(0), builder.defaultValue(mapper.constructType(Integer.TYPE)));
        assertNull(builder.defaultValue(mapper.constructType(Object.class)));
    }

    @Test
    public void defaultBeanIsInstantiatedAndCachedWhenAvailable() {
        ObjectMapper mapper = new ObjectMapper();
        ExposedPropertyBuilder builder = propertyBuilderFor(mapper, DefaultConstructibleBean.class);

        Object first = builder.defaultBean();
        Object second = builder.defaultBean();

        assertTrue(first instanceof DefaultConstructibleBean);
        assertSame(first, second);
    }

    @Test
    public void defaultBeanIsNullWhenNoDefaultConstructorExists() {
        ObjectMapper mapper = new ObjectMapper();
        ExposedPropertyBuilder builder = propertyBuilderFor(mapper, NoDefaultConstructorBean.class);

        assertNull(builder.defaultBean());
    }
}
