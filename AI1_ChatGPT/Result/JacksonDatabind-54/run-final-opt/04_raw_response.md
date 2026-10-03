@org.junit.Test
public void emptyAtomicReferenceIsSuppressedAsNonEmpty() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("{}", mapper.writeValueAsString(new NonEmptyAtomicReferenceBean1256()));
}

@org.junit.Test
public void absentAtomicReferenceIsSuppressedAsNonAbsentAndPresentValueIsRetained() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("{}", mapper.writeValueAsString(new NonAbsentAtomicReferenceBean1256()));
    NonAbsentAtomicReferenceBean1256 bean = new NonAbsentAtomicReferenceBean1256();
    bean.a.set("value");
    org.junit.Assert.assertEquals("{\"a\":\"value\"}", mapper.writeValueAsString(bean));
}

@org.junit.Test
public void throwWrappedPropagatesRootRuntimeExceptionAndErrorAndWrapsCheckedException() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    ThrowingPropertyBuilder1256 builder = new ThrowingPropertyBuilder1256(
            mapper.getSerializationConfig(),
            mapper.getSerializationConfig().introspect(mapper.constructType(NonEmptyAtomicReferenceBean1256.class)));

    try {
        builder.throwWrapped(new Exception(new Exception("checked")), "a", new Object());
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        org.junit.Assert.assertEquals(
                "Failed to get property 'a' of default java.lang.Object instance", e.getMessage());
    }

    RuntimeException runtime = new IllegalStateException("runtime");
    try {
        builder.throwWrapped(new Exception(runtime), "a", new Object());
        org.junit.Assert.fail("Expected runtime exception");
    } catch (RuntimeException e) {
        org.junit.Assert.assertSame(runtime, e);
    }

    AssertionError error = new AssertionError("error");
    try {
        builder.throwWrapped(new Exception(error), "a", new Object());
        org.junit.Assert.fail("Expected error");
    } catch (AssertionError e) {
        org.junit.Assert.assertSame(error, e);
    }
}

public static class NonEmptyAtomicReferenceBean1256 {
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
    public java.util.concurrent.atomic.AtomicReference<String> a =
            new java.util.concurrent.atomic.AtomicReference<String>();
}

public static class NonAbsentAtomicReferenceBean1256 {
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT)
    public java.util.concurrent.atomic.AtomicReference<String> a =
            new java.util.concurrent.atomic.AtomicReference<String>();
}

private static class ThrowingPropertyBuilder1256 extends com.fasterxml.jackson.databind.ser.PropertyBuilder {
    ThrowingPropertyBuilder1256(com.fasterxml.jackson.databind.SerializationConfig config,
            com.fasterxml.jackson.databind.BeanDescription beanDesc) {
        super(config, beanDesc);
    }

    void throwWrapped(Exception e, String propName, Object defaultBean) {
        _throwWrapped(e, propName, defaultBean);
    }
}