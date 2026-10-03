package org.apache.commons.lang3.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ToStringStyleRegistryLifecycleTest {

    private static class SelfReferencingObject {
        private Object self;

        SelfReferencingObject() {
            self = this;
        }
    }

    private static class Parent {
        private Object reference;
    }

    private static class Child extends Parent {
        private int number = 7;
    }

    private static class PrimitiveArrayHolder {
        private final long[] longs = new long[] { 1L, 2L };
        private final int[] ints = new int[] { 3, 4 };
        private final short[] shorts = new short[] { 5, 6 };
        private final byte[] bytes = new byte[] { 7, 8 };
        private final char[] chars = new char[] { 'a', 'b' };
        private final double[] doubles = new double[] { 1.5d, 2.5d };
        private final float[] floats = new float[] { 3.5f, 4.5f };
        private final boolean[] booleans = new boolean[] { true, false };
    }

    private static class ThrowingValue {
        @Override
        public String toString() {
            throw new IllegalStateException("expected");
        }
    }

    @Test
    public void testRegisterAndUnregisterRemovesEmptyRegistry() {
        Object value = new Object();

        ToStringStyle.register(value);

        assertTrue(ToStringStyle.isRegistered(value));
        assertNotNull(ToStringStyle.getRegistry());

        ToStringStyle.unregister(value);

        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testNormalObjectAppendLeavesNoRegistry() {
        StringBuffer buffer = new StringBuffer();

        ToStringStyle.DEFAULT_STYLE.append(buffer, "value", (Object) "alpha", Boolean.TRUE);

        assertTrue(buffer.toString().indexOf("alpha") >= 0);
        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testNullObjectAppendDoesNotCreateRegistry() {
        StringBuffer buffer = new StringBuffer();

        ToStringStyle.DEFAULT_STYLE.append(buffer, "value", (Object) null, Boolean.TRUE);

        assertTrue(buffer.toString().indexOf("null") >= 0);
        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testExceptionDuringDetailRenderingCleansRegistry() {
        StringBuffer buffer = new StringBuffer();

        try {
            ToStringStyle.DEFAULT_STYLE.append(buffer, "value", (Object) new ThrowingValue(), Boolean.TRUE);
            fail("Expected the value's toString() exception");
        } catch (IllegalStateException expected) {
            assertTrue("expected".equals(expected.getMessage()));
        }

        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testReflectionSelfReferenceUsesCycleHandlingAndCleansRegistry() {
        String rendered = ReflectionToStringBuilder.toString(new SelfReferencingObject());

        assertNotNull(rendered);
        assertTrue(rendered.indexOf('@') >= 0);
        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testCyclicObjectArrayUsesCycleHandlingAndCleansRegistry() {
        Object[] values = new Object[1];
        values[0] = values;
        StringBuffer buffer = new StringBuffer();

        ToStringStyle.DEFAULT_STYLE.append(buffer, "values", (Object) values, Boolean.TRUE);

        assertTrue(buffer.toString().indexOf('@') >= 0);
        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testReflectionHierarchyWithCycleCleansRegistry() {
        Child child = new Child();
        ((Parent) child).reference = child;

        String rendered = ReflectionToStringBuilder.toString(child);

        assertNotNull(rendered);
        assertTrue(rendered.indexOf("number=7") >= 0);
        assertNull(ToStringStyle.getRegistry());
    }

    @Test
    public void testReflectionPrimitiveArraysCleanRegistry() {
        String rendered = ReflectionToStringBuilder.toString(new PrimitiveArrayHolder());

        assertNotNull(rendered);
        assertTrue(rendered.indexOf("longs") >= 0);
        assertTrue(rendered.indexOf("booleans") >= 0);
        assertNull(ToStringStyle.getRegistry());
    }

@Test
public void testDirectPrimitiveAppendsRenderAllScalarValuesAndCleanRegistry() {
    ToStringStyle style = ToStringStyle.DEFAULT_STYLE;
    StringBuffer buffer = new StringBuffer();
    Object root = new Object();

    style.appendStart(buffer, root);
    style.append(buffer, "byteValue", (byte) 1);
    style.append(buffer, "charValue", 'z');
    style.append(buffer, "doubleValue", 2.5d);
    style.append(buffer, "floatValue", 3.5f);
    style.append(buffer, "intValue", 4);
    style.append(buffer, "longValue", 5L);
    style.appendEnd(buffer, root);

    assertTrue(buffer.toString().indexOf("byteValue") >= 0);
    assertTrue(buffer.toString().indexOf("charValue") >= 0);
    assertTrue(buffer.toString().indexOf("doubleValue") >= 0);
    assertTrue(buffer.toString().indexOf("floatValue") >= 0);
    assertTrue(buffer.toString().indexOf("intValue") >= 0);
    assertTrue(buffer.toString().indexOf("longValue") >= 0);
    assertTrue(buffer.toString().indexOf("z") >= 0);
    assertNull(ToStringStyle.getRegistry());
}

@Test
public void testDirectArrayAppendsRenderFieldsAndCleanRegistry() {
    ToStringStyle style = ToStringStyle.DEFAULT_STYLE;
    StringBuffer buffer = new StringBuffer();
    Object root = new Object();

    style.appendStart(buffer, root);
    style.append(buffer, "bytes", new byte[] { 1, 2 }, Boolean.TRUE);
    style.append(buffer, "chars", new char[] { 'x' }, Boolean.TRUE);
    style.append(buffer, "doubles", new double[] { 1.5d }, Boolean.TRUE);
    style.append(buffer, "floats", new float[] { 2.5f }, Boolean.TRUE);
    style.append(buffer, "ints", new int[] { 3 }, Boolean.TRUE);
    style.append(buffer, "longs", new long[] { 4L }, Boolean.TRUE);
    style.append(buffer, "objects", new Object[] { "value", null }, Boolean.TRUE);
    style.appendEnd(buffer, root);

    assertTrue(buffer.toString().indexOf("bytes") >= 0);
    assertTrue(buffer.toString().indexOf("chars") >= 0);
    assertTrue(buffer.toString().indexOf("doubles") >= 0);
    assertTrue(buffer.toString().indexOf("floats") >= 0);
    assertTrue(buffer.toString().indexOf("ints") >= 0);
    assertTrue(buffer.toString().indexOf("longs") >= 0);
    assertTrue(buffer.toString().indexOf("objects") >= 0);
    assertTrue(buffer.toString().indexOf("value") >= 0);
    assertNull(ToStringStyle.getRegistry());
}
}
