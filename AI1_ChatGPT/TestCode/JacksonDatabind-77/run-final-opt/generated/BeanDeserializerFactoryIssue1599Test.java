package com.fasterxml.jackson.databind.deser;

import java.lang.reflect.Proxy;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BeanDeserializerFactoryIssue1599Test
{
    private static final String ILLEGAL_TYPE =
            "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";

    private static class ExposedBeanDeserializerFactory extends BeanDeserializerFactory {
        public ExposedBeanDeserializerFactory() {
            super(new DeserializerFactoryConfig());
        }

        public boolean potentialBeanType(Class<?> type) {
            return isPotentialBeanType(type);
        }
    }

    public static class SimpleBean {
        public int value;
    }

    @Test
    public void rejectsIssue1599IllegalTypeDuringBeanEligibility() throws Exception {
        ExposedBeanDeserializerFactory factory = new ExposedBeanDeserializerFactory();

        try {
            factory.potentialBeanType(Class.forName(ILLEGAL_TYPE));
            fail("Expected illegal type to be rejected");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Illegal type"));
        }
    }

    @Test
    public void rejectsIssue1599IllegalTypeDuringPolymorphicDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        try {
            mapper.readValue("[\"" + ILLEGAL_TYPE + "\",{}]", Object.class);
            fail("Expected illegal polymorphic type to be rejected");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Illegal type"));
        }
    }

    @Test
    public void rejectsJdkProxyAsBeanType() {
        ExposedBeanDeserializerFactory factory = new ExposedBeanDeserializerFactory();
        Class<?> proxyType = Proxy.getProxyClass(
                BeanDeserializerFactoryIssue1599Test.class.getClassLoader(),
                new Class<?>[] { Runnable.class });

        try {
            factory.potentialBeanType(proxyType);
            fail("Expected proxy type to be rejected");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Proxy class"));
        }
    }

    @Test
    public void acceptsAndDeserializesOrdinaryBeanType() throws Exception {
        ExposedBeanDeserializerFactory factory = new ExposedBeanDeserializerFactory();
        assertTrue(factory.potentialBeanType(SimpleBean.class));

        SimpleBean bean = new ObjectMapper().readValue("{\"value\":13}", SimpleBean.class);
        assertEquals(13, bean.value);
    }
}
