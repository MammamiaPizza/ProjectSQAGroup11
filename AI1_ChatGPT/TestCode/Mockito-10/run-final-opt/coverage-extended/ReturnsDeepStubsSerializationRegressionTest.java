package org.mockitousage.bugs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;
import org.mockito.Mockito;

public class ReturnsDeepStubsSerializationRegressionTest {

    public interface Leaf {
        String name();
    }

    public interface HasLeaf {
        Leaf leaf();
    }

    public interface SerializableLeafOwner extends HasLeaf, Serializable {
    }

    public interface IntersectionRoot<T extends HasLeaf & Serializable> {
        T value();
    }

    public static abstract class ConcreteIntersectionRoot
            implements IntersectionRoot<SerializableLeafOwner> {
    }

    public interface PlainRoot {
        String text();
    }

    @Test
    public void should_access_deep_stub_when_generic_return_has_serializable_extra_interface() {
        IntersectionRoot<?> root = Mockito.mock(IntersectionRoot.class, Mockito.RETURNS_DEEP_STUBS);

        HasLeaf value = root.value();

        assertNotNull(value);
        assertNotNull(value.leaf());
    }

    @Test
    public void should_cache_deep_stub_for_resolved_serializable_generic_return_type() {
        ConcreteIntersectionRoot root =
                Mockito.mock(ConcreteIntersectionRoot.class, Mockito.RETURNS_DEEP_STUBS);

        SerializableLeafOwner first = root.value();
        SerializableLeafOwner second = root.value();

        assertNotNull(first);
        assertSame(first, second);
        assertNotNull(first.leaf());
    }

    @Test
    public void should_keep_serializable_deep_stub_usable_after_serialization() throws Exception {
        ConcreteIntersectionRoot root =
                Mockito.mock(ConcreteIntersectionRoot.class, Mockito.RETURNS_DEEP_STUBS);

        SerializableLeafOwner deepStub = root.value();
        SerializableLeafOwner restored = serializeAndDeserialize(deepStub);

        assertNotNull(restored);
        assertNotNull(restored.leaf());
    }

    @Test
    public void should_delegate_non_mockable_return_types_to_empty_values() {
        PlainRoot root = Mockito.mock(PlainRoot.class, Mockito.RETURNS_DEEP_STUBS);

        assertNull(root.text());
    }

    private SerializableLeafOwner serializeAndDeserialize(SerializableLeafOwner value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(value);
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        SerializableLeafOwner restored = (SerializableLeafOwner) input.readObject();
        input.close();
        return restored;
    }
}
