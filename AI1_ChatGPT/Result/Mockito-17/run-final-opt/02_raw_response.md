package org.mockito.internal.creation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.internal.util.MockUtil;

public class MockSettingsImplSerializationTest {

    public interface ExtraInterface {
    }

    public interface Sample {
        String value();
    }

    @Test
    public void serializableSettingShouldSurviveAddingExtraInterfaces() {
        MockSettingsImpl settings = new MockSettingsImpl();

        settings.serializable();
        settings.extraInterfaces(ExtraInterface.class);

        assertTrue(settings.isSerializable());
        assertTrue(Arrays.asList(settings.getExtraInterfaces()).contains(ExtraInterface.class));
    }

    @Test
    public void serializableSettingShouldNotDiscardPreviouslyConfiguredExtraInterfaces() {
        MockSettingsImpl settings = new MockSettingsImpl();

        settings.extraInterfaces(ExtraInterface.class);
        settings.serializable();

        assertTrue(settings.isSerializable());
        assertTrue(Arrays.asList(settings.getExtraInterfaces()).contains(ExtraInterface.class));
    }

    @Test
    public void serializableMockWithExtraInterfaceShouldRoundTripAndRemainUsable() throws Exception {
        Sample mock = Mockito.mock(Sample.class,
                Mockito.withSettings().serializable().extraInterfaces(ExtraInterface.class));
        Mockito.when(mock.value()).thenReturn("configured");

        assertTrue(mock instanceof Serializable);
        assertTrue(mock instanceof ExtraInterface);

        Sample deserialized = (Sample) deserialize(serialize(mock));

        assertTrue(deserialized instanceof Serializable);
        assertTrue(deserialized instanceof ExtraInterface);
        assertEquals("configured", deserialized.value());
    }

    @Test
    public void mockUtilShouldCreateSerializableMockWithConfiguredExtraInterface() throws Exception {
        MockSettingsImpl settings = new MockSettingsImpl();
        settings.defaultAnswer(Mockito.RETURNS_DEFAULTS);
        settings.serializable();
        settings.extraInterfaces(ExtraInterface.class);

        Sample mock = new MockUtil().createMock(Sample.class, settings);

        assertTrue(mock instanceof Serializable);
        assertTrue(mock instanceof ExtraInterface);

        Sample deserialized = (Sample) deserialize(serialize(mock));
        assertTrue(deserialized instanceof ExtraInterface);
    }

    @Test
    public void mockUtilShouldRecognizeMocksAndRejectOrdinaryObjects() {
        MockUtil mockUtil = new MockUtil();
        Sample mock = Mockito.mock(Sample.class);

        assertTrue(mockUtil.isMock(mock));
        assertFalse(mockUtil.isMock(new Object()));
        assertFalse(mockUtil.isMock(null));
        assertNotNull(mockUtil.getMockName(mock));
    }

    @Test(expected = NotAMockException.class)
    public void mockUtilShouldRejectGettingHandlerForNonMock() {
        new MockUtil().getMockHandler(new Object());
    }

    private byte[] serialize(Object object) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(object);
        output.close();
        return bytes.toByteArray();
    }

    private Object deserialize(byte[] bytes) throws Exception {
        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Object object = input.readObject();
        input.close();
        return object;
    }
}