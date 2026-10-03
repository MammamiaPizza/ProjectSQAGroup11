package org.mockito.internal.creation.bytebuddy;

 import static org.junit.Assert.*;

 import java.util.Collections;
 import java.util.Set;

 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.exceptions.base.MockitoException;
 import org.mockito.internal.InternalMockHandler;
 import org.mockito.internal.stubbing.InvocationContainer;
 import org.mockito.invocation.Invocation;
 import org.mockito.invocation.MockHandler;
 import org.mockito.mock.MockCreationSettings;
 import org.mockito.mock.SerializableMode;

 public class ByteBuddyMockMakerTest {

     private ByteBuddyMockMaker mockMaker;

     @Before
     public void setUp() {
         mockMaker = new ByteBuddyMockMaker();
     }

     @Test
     public void shouldCreateMockWithConstructorAndFieldsInitialized() {
         MockCreationSettings<ConstructorClass> settings = new
MockSettingsBuilder<>(ConstructorClass.class)
                 .useConstructor(true)
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         ConstructorClass mock = mockMaker.createMock(settings, handler);
         assertEquals("initialized", mock.getValue());
     }

     @Test
     public void canMockAbstractClassWithConstructor() {
         MockCreationSettings<AbstractWithConstructor> settings = new
MockSettingsBuilder<>(AbstractWithConstructor.class)
                 .useConstructor(true)
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         AbstractWithConstructor mock = mockMaker.createMock(settings, handler);
         assertEquals("abstractValue", mock.getValue());
     }

     @Test
     public void shouldSpyInnerClassAndRetainField() {
         MockCreationSettings<InnerClass> settings = new MockSettingsBuilder<>(InnerClass.class)
                 .useConstructor(true)
                 .outerInstance(new ByteBuddyMockMakerTest())
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         InnerClass mock = mockMaker.createMock(settings, handler);
         assertEquals("inner", mock.getInnerValue());
     }

     @Test(expected = MockitoException.class)
     public void shouldThrowWhenWrongOuterInstance() {
         MockCreationSettings<InnerClass> settings = new MockSettingsBuilder<>(InnerClass.class)
                 .useConstructor(true)
                 .outerInstance(new Object())
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         mockMaker.createMock(settings, handler);
     }

     @Test
     public void shouldReportWhenConstructorIsExplosive() {
         MockCreationSettings<ExplosiveConstructor> settings = new
MockSettingsBuilder<>(ExplosiveConstructor.class)
                 .useConstructor(true)
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         try {
             mockMaker.createMock(settings, handler);
             fail("Should have thrown MockitoException");
         } catch (MockitoException e) {
             assertTrue("Message should mention constructor",
                     e.getMessage().toLowerCase().contains("constructor"));
             assertNotNull("Cause should be the explosive exception", e.getCause());
             assertTrue("Cause should be RuntimeException",
                     e.getCause() instanceof RuntimeException);
         }
     }

     @Test
     public void shouldReportWhenConstructorNotFound() {
         MockCreationSettings<NoDefaultConstructor> settings = new
MockSettingsBuilder<>(NoDefaultConstructor.class)
                 .useConstructor(true)
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         try {
             mockMaker.createMock(settings, handler);
             fail("Should throw exception");
         } catch (MockitoException e) {
             assertTrue("Message should indicate constructor issue",
                     e.getMessage().toLowerCase().contains("constructor"));
         }
     }

     @Test
     public void createMockWithoutConstructorShouldNotInitializeFields() {
         MockCreationSettings<ConstructorClass> settings = new
MockSettingsBuilder<>(ConstructorClass.class)
                 .useConstructor(false)
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         ConstructorClass mock = mockMaker.createMock(settings, handler);
         assertNull("Field must be null when constructor is not used", mock.getValue());
     }

     @Test(expected = MockitoException.class)
     public void shouldThrowExceptionWhenAcrossClassLoadersMode() {
         MockCreationSettings<String> settings = new MockSettingsBuilder<>(String.class)
                 .serializableMode(SerializableMode.ACROSS_CLASSLOADERS)
                 .build();
         MockHandler handler = new DummyInternalMockHandler();
         mockMaker.createMock(settings, handler);
     }

     @Test(expected = MockitoException.class)
     public void shouldRejectNonInternalMockHandler() {
         MockCreationSettings<ConstructorClass> settings = new
MockSettingsBuilder<>(ConstructorClass.class).build();
         MockHandler handler = new MockHandler() {
             @Override
             public Object handle(Invocation invocation) throws Throwable { return null; }
         };
         mockMaker.createMock(settings, handler);
     }

     @Test
     public void getHandlerReturnsNullForNonMock() {
         assertNull(mockMaker.getHandler(new Object()));
     }

     @Test
     public void getHandlerShouldReturnMockHandlerAfterCreation() {
         MockCreationSettings<ConstructorClass> settings = new
MockSettingsBuilder<>(ConstructorClass.class).build();
         MockHandler handler = new DummyInternalMockHandler();
         ConstructorClass mock = mockMaker.createMock(settings, handler);
         assertNotNull(mockMaker.getHandler(mock));
         assertSame(handler, mockMaker.getHandler(mock));
     }

     @Test
     public void resetMockShouldSetInterceptor() {
         MockCreationSettings<ConstructorClass> settings = new
MockSettingsBuilder<>(ConstructorClass.class).build();
         MockHandler handler = new DummyInternalMockHandler();
         ConstructorClass mock = mockMaker.createMock(settings, handler);
         MockHandler newHandler = new DummyInternalMockHandler();
         mockMaker.resetMock(mock, newHandler, settings);
         assertSame("Handler should be replaced after reset", newHandler,
mockMaker.getHandler(mock));
     }

     // ---------- test model classes ----------

     public static class ConstructorClass {
         private String value;
         public ConstructorClass() { this.value = "initialized"; }
         public String getValue() { return value; }
     }

     public static abstract class AbstractWithConstructor {
         private String value;
         public AbstractWithConstructor() { this.value = "abstractValue"; }
         public String getValue() { return value; }
     }

     public class InnerClass {
         private String innerValue;
         public InnerClass() { this.innerValue = "inner"; }
         public String getInnerValue() { return innerValue; }
     }

     public static class ExplosiveConstructor {
         public ExplosiveConstructor() { throw new RuntimeException("Boom"); }
     }

     public static class NoDefaultConstructor {
         public NoDefaultConstructor(int ignored) {}
     }

     // ---------- helper stubs ----------

     static class DummyInternalMockHandler implements InternalMockHandler {
         @Override public Object handle(Invocation invocation) throws Throwable { return null; }
         @Override public MockCreationSettings getMockSettings() { return null; }
         @Override public InvocationContainer getInvocationContainer() { return null; }
     }

     static class MockSettingsBuilder<T> {
         private final Class<T> typeToMock;
         private boolean useConstructor = false;
         private Object outerInstance;
         private SerializableMode serializableMode = SerializableMode.NONE;

         MockSettingsBuilder(Class<T> typeToMock) { this.typeToMock = typeToMock; }

         MockSettingsBuilder<T> useConstructor(boolean v) { useConstructor = v; return this; }
         MockSettingsBuilder<T> outerInstance(Object o) { outerInstance = o; return this; }
         MockSettingsBuilder<T> serializableMode(SerializableMode m) { serializableMode = m; return
this; }

         MockCreationSettings<T> build() {
             final boolean cons = useConstructor;
             final Object outer = outerInstance;
             final SerializableMode ser = serializableMode;
             return new MockCreationSettings<T>() {
                 @Override public Class<T> getTypeToMock() { return typeToMock; }
                 @Override public Set<Class<?>> getExtraInterfaces() { return
Collections.emptySet(); }
                 @Override public boolean isUsingConstructor() { return cons; }
                 @Override public Object getOuterInstance() { return outer; }
                 @Override public SerializableMode getSerializableMode() { return ser; }
                 @Override public Object getSpiedInstance() { return null; }
                 @Override public boolean isSerializable() { return false; }
                 @Override public String getMockName() { return typeToMock.getSimpleName(); }
                 @Override public boolean isStubOnly() { return false; }
                 @Override public boolean isLenient() { return false; }
                 @Override public <R> R getConstructorArgs() { return null; }
                 @Override public MockHandler getMockHandler() { return null; }
                 @Override public boolean isInline() { return false; }
                 @Override public <R> R getMockMaker() { return null; }
                 @Override public boolean isInstance(Object instance) { return false; }
                 @Override public Set<Class<?>> getAdditionalInterfaces() { return
Collections.emptySet(); }
             };
         }
     }
 }
