package org.mockito.internal.stubbing.defaultanswers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;
import org.mockito.Mockito;

public class ReturnsDeepStubsSerializationTest {

    public interface Root extends Serializable {
        Child child();
        String text();
    }

    public interface Child extends Serializable {
        String name();
    }

    @Test
    public void shouldSerializeAndDeserializeDeepStubCreatedMock() throws Exception {
        Root root = mock(Root.class, withSettings()
                .serializable()
                .defaultAnswer(Mockito.RETURNS_DEEP_STUBS));

        assertNotNull(root.child());

        Root restored = roundTrip(root);

        assertNotNull(restored.child());
        when(restored.child().name()).thenReturn("restored");
        assertEquals("restored", restored.child().name());
    }

    @Test
    public void shouldReturnTheSameNestedMockForRepeatedDeepStubInvocation() {
        Root root = mock(Root.class, Mockito.RETURNS_DEEP_STUBS);

        Child first = root.child();
        Child second = root.child();

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    public void shouldUseEmptyValueForNonMockableReturnType() {
        Root root = mock(Root.class, Mockito.RETURNS_DEEP_STUBS);

        assertNull(root.text());
    }

    private Root roundTrip(Root root) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(root);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        Root restored = (Root) input.readObject();
        input.close();
        return restored;
    }
}