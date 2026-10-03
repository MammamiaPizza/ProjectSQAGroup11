@Test
    public void createMock_serializableWithoutExtraInterfaces_createsSerializableMock() {
        org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();
        org.mockito.internal.util.MockSettingsImpl settings = new
org.mockito.internal.util.MockSettingsImpl();
        settings.serializable();
        java.util.List mock = mockUtil.createMock(java.util.List.class, settings);
        org.junit.Assert.assertNotNull(mock);
    }

 @Test
 public void createMock_serializableWithExtraInterfaces_createsMockWithExtraInterfaces() {
     org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();
     org.mockito.internal.util.MockSettingsImpl settings = new
org.mockito.internal.util.MockSettingsImpl();
     settings.serializable();
     settings.extraInterfaces(java.io.Serializable.class);
     java.util.List mock = mockUtil.createMock(java.util.List.class, settings);
     org.junit.Assert.assertNotNull(mock);
 }

 @Test
 public void createMock_nonSerializableWithExtraInterfaces_usesExtraAncillaryTypes() {
     org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();
     org.mockito.internal.util.MockSettingsImpl settings = new
org.mockito.internal.util.MockSettingsImpl();
     settings.extraInterfaces(java.io.Serializable.class);
     java.util.List mock = mockUtil.createMock(java.util.List.class, settings);
     org.junit.Assert.assertNotNull(mock);
 }

 @Test
 public void createMock_withSpiedInstance_copiesToMock() {
     org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();
     org.mockito.internal.util.MockSettingsImpl settings = new
org.mockito.internal.util.MockSettingsImpl();
     settings.spiedInstance(new java.util.ArrayList());
     java.util.List mock = mockUtil.createMock(java.util.List.class, settings);
     org.junit.Assert.assertNotNull(mock);
 }