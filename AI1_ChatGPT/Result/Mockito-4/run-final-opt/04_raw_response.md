@org.junit.Test
public void reportsCannotCallAbstractRealMethod() {
    try {
        new org.mockito.exceptions.Reporter().cannotCallAbstractRealMethod();
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Cannot call abstract real method on java object!"));
    }
}

@org.junit.Test
public void reportsSpyInitializationFailureDetails() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForSpyAnnotation(
                "spyField", new java.lang.Exception("construction failed"));
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Cannot instantiate a @Spy for 'spyField' field."));
        org.junit.Assert.assertTrue(e.getMessage().contains("construction failed"));
    }
}

@org.junit.Test
public void reportsInjectMocksInitializationFailureDetails() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForInjectMocksAnnotation(
                "service", new java.lang.Exception("no suitable constructor"));
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Cannot instantiate @InjectMocks field named 'service'."));
        org.junit.Assert.assertTrue(e.getMessage().contains("no suitable constructor"));
    }
}

@org.junit.Test
public void reportsFriendlyReminderWhenAtMostOrNeverIsUsedWithTimeout() {
    try {
        new org.mockito.exceptions.Reporter().atMostAndNeverShouldNotBeUsedWithTimeout();
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("timeout() should not be used with atMost() or never()"));
    }
}