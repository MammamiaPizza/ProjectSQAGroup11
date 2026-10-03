@org.junit.Test
public void shouldExplainWhyRealMethodsCannotBeCalledOnInterfaces() {
    try {
        new org.mockito.exceptions.Reporter().cannotCallRealMethodOnInterface();
        org.junit.Assert.fail("Expected a MockitoException");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("Cannot call real method on java interface"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("mocking concrete classes"));
    }
}

@org.junit.Test
public void shouldDescribeFailureToInitializeSpyField() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForSpyAnnotation("spiedField", new java.lang.Exception("no default constructor"));
        org.junit.Assert.fail("Expected a MockitoException");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("@Spy for 'spiedField'"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("no default constructor"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("Examples of correct usage of @Spy"));
    }
}

@org.junit.Test
public void shouldDescribeFailureToInitializeInjectMocksField() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForInjectMocksAnnotation("service", new java.lang.Exception("construction failed"));
        org.junit.Assert.fail("Expected a MockitoException");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("@InjectMocks field named 'service'"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("construction failed"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("Examples of correct usage of @InjectMocks"));
    }
}