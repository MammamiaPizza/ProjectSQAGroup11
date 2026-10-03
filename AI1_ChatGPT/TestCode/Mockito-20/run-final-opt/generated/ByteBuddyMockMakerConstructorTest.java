import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ByteBuddyMockMakerConstructorTest {

    @Spy
    private InnerSpyTarget annotatedInnerSpy;

    @Test
    public void createsConcreteMockUsingNoArgumentConstructor() {
        ConcreteWithConstructor mock = Mockito.mock(
                ConcreteWithConstructor.class,
                Mockito.withSettings().useConstructor().defaultAnswer(Mockito.CALLS_REAL_METHODS));

        assertEquals("hey!", mock.message());
    }

    @Test
    public void createsConcreteMockUsingConstructorArguments() {
        ConcreteWithArguments mock = Mockito.mock(
                ConcreteWithArguments.class,
                Mockito.withSettings().useConstructor("hello").defaultAnswer(Mockito.CALLS_REAL_METHODS));

        assertEquals("hello world", mock.message());
    }

    @Test
    public void createsAbstractMockUsingConstructor() {
        AbstractWithConstructor mock = Mockito.mock(
                AbstractWithConstructor.class,
                Mockito.withSettings().useConstructor().defaultAnswer(Mockito.CALLS_REAL_METHODS));

        assertEquals("hey!", mock.message());
    }

    @Test
    public void createsInnerClassMockUsingProvidedOuterInstance() {
        InnerWithConstructor mock = Mockito.mock(
                InnerWithConstructor.class,
                Mockito.withSettings()
                        .outerInstance(ByteBuddyMockMakerConstructorTest.this)
                        .useConstructor("hey!")
                        .defaultAnswer(Mockito.CALLS_REAL_METHODS));

        assertEquals("hey!", mock.message());
    }

    @Test
    public void initializesSpyOnNonStaticInnerClassThroughItsConstructor() {
        MockitoAnnotations.initMocks(this);

        assertEquals("inner strength", annotatedInnerSpy.strength());
    }

    @Test
    public void reportsFailureWhenConstructorThrows() {
        try {
            Mockito.mock(
                    ExplosiveConstructor.class,
                    Mockito.withSettings().useConstructor().defaultAnswer(Mockito.CALLS_REAL_METHODS));
            fail("Expected mock creation to report the constructor failure");
        } catch (MockitoException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    @Test
    public void reportsFailureWhenNoMatchingConstructorExists() {
        try {
            Mockito.mock(
                    OnlyStringConstructor.class,
                    Mockito.withSettings().useConstructor(Integer.valueOf(3)).defaultAnswer(Mockito.CALLS_REAL_METHODS));
            fail("Expected mock creation to reject unmatched constructor arguments");
        } catch (MockitoException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    @Test
    public void reportsFailureWhenInnerClassReceivesWrongOuterInstance() {
        try {
            Mockito.mock(
                    InnerWithConstructor.class,
                    Mockito.withSettings()
                            .outerInstance(new UnrelatedOuter())
                            .useConstructor("hey!")
                            .defaultAnswer(Mockito.CALLS_REAL_METHODS));
            fail("Expected mock creation to reject an unrelated outer instance");
        } catch (MockitoException expected) {
            assertNotNull(expected.getMessage());
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    public static class ConcreteWithConstructor {
        private final String value;

        public ConcreteWithConstructor() {
            this.value = "hey!";
        }

        public String message() {
            return value;
        }
    }

    public static class ConcreteWithArguments {
        private final String value;

        public ConcreteWithArguments(String value) {
            this.value = value;
        }

        public String message() {
            return value + " world";
        }
    }

    public abstract static class AbstractWithConstructor {
        private final String value;

        public AbstractWithConstructor() {
            this.value = "hey!";
        }

        public String message() {
            return value;
        }
    }

    public class InnerWithConstructor {
        private final String value;

        public InnerWithConstructor(String value) {
            this.value = value;
        }

        public String message() {
            return value;
        }
    }

    public class InnerSpyTarget {
        private final String value;

        public InnerSpyTarget() {
            this.value = "inner strength";
        }

        public String strength() {
            return value;
        }
    }

    public static class ExplosiveConstructor {
        public ExplosiveConstructor() {
            throw new IllegalStateException("explosive constructor");
        }
    }

    public static class OnlyStringConstructor {
        public OnlyStringConstructor(String value) {
        }
    }

    public static class UnrelatedOuter {
    }
}
