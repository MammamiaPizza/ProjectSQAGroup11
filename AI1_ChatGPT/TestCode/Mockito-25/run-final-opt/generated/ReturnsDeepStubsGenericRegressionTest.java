import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

public class ReturnsDeepStubsGenericRegressionTest {

    public interface ValueHolder<T> {
        T value();
    }

    public interface StringHolder extends ValueHolder<String> {
    }

    public interface MultiBoundHolder<T extends Comparable<T> & Cloneable> {
        T value();
    }

    public interface Wrapper<T> {
        T value();
    }

    public interface ParameterizedMultiBoundHolder<T extends Comparable<T> & Cloneable> {
        Wrapper<T> wrapper();
    }

    public interface MethodMultiBoundHolder<T extends Comparable<T> & Cloneable> {
        <S extends T> S value();
    }

    public interface WildcardEntryHolder {
        List<? extends Map.Entry<String, String>> entries();
    }

    public interface Node<T> {
        Node<T> next();
        T value();
    }

    public interface StringNode extends Node<String> {
    }

    public interface PlainNode {
        PlainNode child();
    }

    @Test
    public void returnsDefaultValueForNestedGenericStringRatherThanIncompatibleMock() {
        StringHolder holder = deepMock(StringHolder.class);

        String value = holder.value();

        assertNull(value);
    }

    @Test
    public void createsDeepStubAssignableToAllMultipleBoundsForDirectTypeVariable() {
        MultiBoundHolder<?> holder = deepMock(MultiBoundHolder.class);

        Cloneable value = holder.value();

        assertNotNull(value);
        assertTrue(value instanceof Comparable);
    }

    @Test
    public void createsDeepStubAssignableToAllMultipleBoundsInsideParameterizedReturn() {
        ParameterizedMultiBoundHolder<?> holder = deepMock(ParameterizedMultiBoundHolder.class);

        Cloneable value = holder.wrapper().value();

        assertNotNull(value);
        assertTrue(value instanceof Comparable);
    }

    @Test
    public void createsDeepStubAssignableToMultipleBoundsForGenericMethodReturn() {
        MethodMultiBoundHolder<?> holder = deepMock(MethodMultiBoundHolder.class);

        Cloneable value = holder.value();

        assertNotNull(value);
        assertTrue(value instanceof Comparable);
    }

    @Test
    public void resolvesBoundedWildcardReturnToCompatibleMapEntryMock() {
        WildcardEntryHolder holder = deepMock(WildcardEntryHolder.class);

        Map.Entry<String, String> entry = holder.entries().get(0);

        assertNotNull(entry);
        assertNull(entry.getKey());
        assertNull(entry.getValue());
    }

    @Test
    public void preservesConcreteGenericTypeAcrossChainedDeepStubs() {
        StringNode node = deepMock(StringNode.class);

        String value = node.next().next().value();

        assertNull(value);
    }

    @Test
    public void returnsSameDeepStubForRepeatedMatchingInvocation() {
        PlainNode node = deepMock(PlainNode.class);

        assertSame(node.child(), node.child());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> T deepMock(Class<?> type) {
        return (T) Mockito.mock((Class) type, Mockito.RETURNS_DEEP_STUBS);
    }
}
