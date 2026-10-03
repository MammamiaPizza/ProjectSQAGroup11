@org.junit.Test
public void rejectsSerializationAcrossClassLoaders() {
    org.mockito.internal.creation.settings.CreationSettings<java.util.List> settings =
            new org.mockito.internal.creation.settings.CreationSettings<java.util.List>();
    settings.setTypeToMock(java.util.List.class);
    settings.setSerializableMode(org.mockito.mock.SerializableMode.ACROSS_CLASSLOADERS);

    try {
        new org.mockito.internal.creation.bytebuddy.ByteBuddyMockMaker().createMock(settings, null);
        org.junit.Assert.fail("Expected serialization across classloaders to be rejected");
    } catch (org.mockito.exceptions.base.MockitoException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("Serialization across classloaders"));
    }
}

@org.junit.Test
public void returnsNullHandlerForObjectsThatAreNotMocks() {
    org.junit.Assert.assertNull(
            new org.mockito.internal.creation.bytebuddy.ByteBuddyMockMaker().getHandler(new Object()));
}

@org.junit.Test
public void returnsHandlerForByteBuddyMock() {
    Object mock = org.mockito.Mockito.mock(Object.class);

    org.junit.Assert.assertNotNull(
            new org.mockito.internal.creation.bytebuddy.ByteBuddyMockMaker().getHandler(mock));
}

@org.junit.Test
public void rejectsResetWithNonInternalMockHandler() {
    Object mock = org.mockito.Mockito.mock(Object.class);

    try {
        new org.mockito.internal.creation.bytebuddy.ByteBuddyMockMaker().resetMock(mock, null, null);
        org.junit.Assert.fail("Expected reset to reject a non-internal mock handler");
    } catch (org.mockito.exceptions.base.MockitoException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("cannot provide own implementations of MockHandler"));
    }
}