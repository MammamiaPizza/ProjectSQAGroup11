@org.junit.Test
public void extraInterfacesShouldRejectNullEmptyNullElementAndConcreteClasses() {
    assertExtraInterfacesRejected((Class<?>[]) null);
    assertExtraInterfacesRejected();
    assertExtraInterfacesRejected((Class<?>) null);
    assertExtraInterfacesRejected(String.class);
}

@org.junit.Test
public void settingsShouldRetainConfiguredSpyNameAndDefaultAnswer() {
    org.mockito.internal.creation.MockSettingsImpl settings =
            new org.mockito.internal.creation.MockSettingsImpl();
    Object spiedInstance = new Object();

    org.junit.Assert.assertSame(settings, settings.spiedInstance(spiedInstance));
    org.junit.Assert.assertSame(spiedInstance, settings.getSpiedInstance());
    org.junit.Assert.assertSame(settings, settings.defaultAnswer(null));
    org.junit.Assert.assertNull(settings.getDefaultAnswer());
    org.junit.Assert.assertSame(settings, settings.name("named mock"));
    org.junit.Assert.assertNotNull(settings.getMockName());
    org.junit.Assert.assertFalse(new org.mockito.internal.creation.MockSettingsImpl().isSerializable());
}

@org.junit.Test
public void mockUtilShouldTreatNullAsNonMockAndRejectGettingItsHandler() {
    org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();

    org.junit.Assert.assertFalse(mockUtil.isMock(null));

    try {
        mockUtil.getMockHandler(null);
        org.junit.Assert.fail("Expected NotAMockException");
    } catch (org.mockito.exceptions.misusing.NotAMockException expected) {
        org.junit.Assert.assertEquals("Argument should be a mock, but is null!", expected.getMessage());
    }
}

@org.junit.Test
public void mockUtilShouldKeepConfiguredNameWhenResettingMock() {
    org.mockito.internal.creation.MockSettingsImpl settings =
            new org.mockito.internal.creation.MockSettingsImpl();
    settings.name("named list");

    org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();
    Object mock = mockUtil.createMock(java.util.List.class, settings);

    mockUtil.resetMock(mock);

    org.junit.Assert.assertEquals("named list", mockUtil.getMockName(mock).toString());
}

private void assertExtraInterfacesRejected(Class<?>... extraInterfaces) {
    try {
        new org.mockito.internal.creation.MockSettingsImpl().extraInterfaces(extraInterfaces);
        org.junit.Assert.fail("Expected invalid extra interfaces to be rejected");
    } catch (RuntimeException expected) {
    }
}