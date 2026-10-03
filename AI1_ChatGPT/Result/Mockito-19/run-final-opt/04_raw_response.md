@Test
public void shouldReportActualCauseWhenInjectMocksFieldInitializationThrows() throws Exception {
    org.mockito.exceptions.base.MockitoException failure = constructorFailureDuringInjection();

    org.junit.Assert.assertTrue(failure.getCause() instanceof IllegalStateException);
}

@Test
public void shouldFailWhenInjectMocksFieldTypeHasNoDefaultConstructor() throws Exception {
    org.mockito.exceptions.base.MockitoException failure = noDefaultConstructorDuringInjection();

    org.junit.Assert.assertNotNull(failure);
}

private static org.mockito.exceptions.base.MockitoException constructorFailureDuringInjection() throws Exception {
    class ExplodingDependency {
        ExplodingDependency() {
            throw new IllegalStateException("boom");
        }
    }
    class Owner {
        ExplodingDependency dependency;
    }

    try {
        new org.mockito.internal.configuration.injection.PropertyAndSetterInjection().processInjection(
                Owner.class.getDeclaredField("dependency"),
                new Owner(),
                java.util.Collections.<Object>emptySet());
    } catch (org.mockito.exceptions.base.MockitoException e) {
        return e;
    }

    throw new AssertionError("Expected initialization to fail");
}

private static org.mockito.exceptions.base.MockitoException noDefaultConstructorDuringInjection() throws Exception {
    class DependencyWithoutDefaultConstructor {
        DependencyWithoutDefaultConstructor(String value) {
        }
    }
    class Owner {
        DependencyWithoutDefaultConstructor dependency;
    }

    try {
        new org.mockito.internal.configuration.injection.PropertyAndSetterInjection().processInjection(
                Owner.class.getDeclaredField("dependency"),
                new Owner(),
                java.util.Collections.<Object>emptySet());
    } catch (org.mockito.exceptions.base.MockitoException e) {
        return e;
    }

    throw new AssertionError("Expected initialization to fail");
}