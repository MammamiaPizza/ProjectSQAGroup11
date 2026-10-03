@org.junit.Test
public void shouldCreateMocksForSerializableAndExtraInterfaceConfigurations() {
    org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();

    org.mockito.internal.creation.MockSettingsImpl serializableSettings =
            new org.mockito.internal.creation.MockSettingsImpl();
    serializableSettings.serializable();
    Object serializableMock = mockUtil.createMock(Runnable.class, serializableSettings);
    org.junit.Assert.assertTrue(serializableMock instanceof java.io.Serializable);

    org.mockito.internal.creation.MockSettingsImpl serializableWithExtraInterface =
            new org.mockito.internal.creation.MockSettingsImpl();
    serializableWithExtraInterface.serializable();
    serializableWithExtraInterface.extraInterfaces(Cloneable.class);
    Object serializableMockWithExtraInterface =
            mockUtil.createMock(Runnable.class, serializableWithExtraInterface);
    org.junit.Assert.assertTrue(serializableMockWithExtraInterface instanceof java.io.Serializable);
    org.junit.Assert.assertTrue(serializableMockWithExtraInterface instanceof Cloneable);

    org.mockito.internal.creation.MockSettingsImpl extraInterfaceSettings =
            new org.mockito.internal.creation.MockSettingsImpl();
    extraInterfaceSettings.extraInterfaces(Cloneable.class);
    Object mockWithExtraInterface = mockUtil.createMock(Runnable.class, extraInterfaceSettings);
    org.junit.Assert.assertTrue(mockWithExtraInterface instanceof Cloneable);
}

@org.junit.Test
public void shouldCopySpiedInstanceFieldsToCreatedMock() {
    class MutableState {
        int value;
    }

    MutableState original = new MutableState();
    original.value = 37;

    org.mockito.internal.creation.MockSettingsImpl settings =
            new org.mockito.internal.creation.MockSettingsImpl();
    settings.spiedInstance(original);

    MutableState mock = new org.mockito.internal.util.MockUtil().createMock(MutableState.class, settings);

    org.junit.Assert.assertEquals(37, mock.value);
}

@org.junit.Test
public void shouldReturnTheMockNameFromTheMockHandlerSettings() {
    org.mockito.internal.util.MockUtil mockUtil = new org.mockito.internal.util.MockUtil();
    org.mockito.internal.creation.MockSettingsImpl settings =
            new org.mockito.internal.creation.MockSettingsImpl();

    Object mock = mockUtil.createMock(Runnable.class, settings);

    org.junit.Assert.assertSame(
            mockUtil.getMockHandler(mock).getMockSettings().getMockName(),
            mockUtil.getMockName(mock));
}

@org.junit.Test
public void shouldNotRecognizeFactoryWithNonMockitoCallbackAsMock() {
    Object factory = java.lang.reflect.Proxy.newProxyInstance(
            net.sf.cglib.proxy.Factory.class.getClassLoader(),
            new Class<?>[] {net.sf.cglib.proxy.Factory.class},
            new java.lang.reflect.InvocationHandler() {
                public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] arguments) {
                    if ("getCallback".equals(method.getName())) {
                        return net.sf.cglib.proxy.NoOp.INSTANCE;
                    }
                    return null;
                }
            });

    org.junit.Assert.assertFalse(new org.mockito.internal.util.MockUtil().isMock(factory));
}