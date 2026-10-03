package org.mockitousage.bugs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;
import org.mockito.Mockito;

public class ReturnsDeepStubsSerializationTest {

    public interface StringRoot extends Serializable, GenericRoot<String> {
    }

    public interface GenericRoot<T> {
        Holder<T> holder();
    }

    public interface Holder<T> {
        T value();

        Nested nested();
    }

    public interface Nested {
        int count();
    }

    public interface EmptyValueRoot {
        int count();

        String name();
    }

    @Test
    public void shouldKeepResolvedGenericInformationWhenAccessingDeepStub() {
        StringRoot root = Mockito.mock(StringRoot.class, Mockito.RETURNS_DEEP_STUBS);

        Holder<String> firstHolder = root.holder();
        Holder<String> secondHolder = root.holder();

        assertNotNull(firstHolder);
        assertSame(firstHolder, secondHolder);
        assertNull(firstHolder.value());
        assertNotNull(firstHolder.nested());
        assertEquals(0, firstHolder.nested().count());
    }

    @Test
    public void shouldNotFailWithSerializationProblemsWhenAccessingRecordedDeepStubs() throws Exception {
        StringRoot root = Mockito.mock(StringRoot.class, Mockito.RETURNS_DEEP_STUBS);
        Holder<String> holder = root.holder();
        Nested nested = holder.nested();

        StringRoot copy = roundTrip(root);
        Holder<String> copiedHolder = copy.holder();

        assertNotNull(copy);
        assertNotNull(copiedHolder);
        assertSame(copiedHolder, copy.holder());
        assertNotNull(copiedHolder.nested());
        assertEquals(0, copiedHolder.nested().count());
        assertEquals(0, nested.count());
    }

    @Test
    public void shouldDelegateNonMockableReturnTypesToEmptyValues() {
        EmptyValueRoot root = Mockito.mock(EmptyValueRoot.class, Mockito.RETURNS_DEEP_STUBS);

        assertEquals(0, root.count());
        assertNull(root.name());
    }

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T value) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(value);
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        Object result = input.readObject();
        input.close();
        return (T) result;
    }
}
