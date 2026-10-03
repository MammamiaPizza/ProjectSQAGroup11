@Test
public void reportsConstructorFailureWhenNoOuterInstanceIsProvided() {
    try {
        new ConstructorInstantiator(null).newInstance(ThrowingNoArgConstructor.class);
        fail("Expected an InstantationException");
    } catch (org.mockito.internal.creation.instance.InstantationException e) {
        assertEquals(
                "Unable to create mock instance of 'ThrowingNoArgConstructor'.\n"
                        + "Please ensure it has parameter-less constructor.",
                e.getMessage());
        assertNotNull(e.getCause());
    }
}

@Test
public void reportsConstructorFailureWhenInnerClassConstructorThrows() {
    try {
        new ConstructorInstantiator(new ThrowingInnerOuter())
                .newInstance(ThrowingInnerOuter.ThrowingInnerClass.class);
        fail("Expected an InstantationException");
    } catch (org.mockito.internal.creation.instance.InstantationException e) {
        assertEquals(
                "Unable to create mock instance of 'ThrowingInnerClass'.\n"
                        + "Please ensure that the outer instance has correct type and that the target class has parameter-less constructor.",
                e.getMessage());
        assertNotNull(e.getCause());
    }
}

public static class ThrowingNoArgConstructor {
    public ThrowingNoArgConstructor() {
        throw new IllegalStateException("boom");
    }
}

public static class ThrowingInnerOuter {
    public class ThrowingInnerClass {
        public ThrowingInnerClass() {
            throw new IllegalStateException("boom");
        }
    }
}