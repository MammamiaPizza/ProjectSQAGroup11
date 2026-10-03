package org.mockito.internal.stubbing.defaultanswers;

 import org.junit.Test;
 import org.mockito.Mockito;
 import org.mockito.exceptions.base.MockitoException;

 import java.io.*;
 import java.util.List;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 /**
  * Tests for {@link ReturnsDeepStubs} focusing on Bug 10 / #99:
  * deep stubs should not throw serialization-related MockitoException
  * when accessing non-serializable return types.
  */
 public class ReturnsDeepStubsTest {

     // ---------- plain non-serializable types ----------
     interface NonSerializableFoo {
         NonSerializableBar getBar();
     }
     interface NonSerializableBar {
         String doSomething();
     }

     @Test
     public void shouldNotThrowOnNonSerializableDeepStub() {
         NonSerializableFoo mock = mock(NonSerializableFoo.class, Mockito.RETURNS_DEEP_STUBS);
         // Access deep stub on a non-serializable type – must not throw
         String result = mock.getBar().doSomething();
         assertNull(result);
     }

     // ---------- nested non-serializable chain ----------
     interface DeepA { DeepB getB(); }
     interface DeepB { DeepC getC(); }
     interface DeepC { String value(); }

     @Test
     public void shouldAllowNestedNonSerializableDeepStubs() {
         DeepA mock = mock(DeepA.class, Mockito.RETURNS_DEEP_STUBS);
         // longer chain with only non-serializable types
         String result = mock.getB().getC().value();
         assertNull(result);
     }

     // ---------- generic return type ----------
     interface GenericContainer<T> {
         List<T> getItems();
     }

     @Test
     public void shouldAllowGenericDeepStub() {
         GenericContainer<String> mock = mock(GenericContainer.class, Mockito.RETURNS_DEEP_STUBS);
         List<String> items = mock.getItems();
         assertNotNull(items);
         assertTrue(items instanceof List);
     }

     // ---------- serializable ancestor, non-serializable subtype ----------
     interface SerializableParent extends Serializable {
         NonSerializableChild getChild();
     }
     interface NonSerializableChild {
         String getName();
     }

     @Test
     public void shouldNotThrowOnSerializableAncestorWithNonSerializableSubtype() {
         SerializableParent mock = mock(SerializableParent.class, Mockito.RETURNS_DEEP_STUBS);
         // deep stub access to non-serializable child – must not throw
         String name = mock.getChild().getName();
         assertNull(name);
     }

     // ---------- serialization of the deep stub mock itself ----------
     @Test
     public void shouldFailWhenSerializingDeepStubOfNonSerializableType() {
         NonSerializableFoo mock = mock(NonSerializableFoo.class, Mockito.RETURNS_DEEP_STUBS);
         Object deepStub = mock.getBar();
         try {
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos);
             oos.writeObject(deepStub);
             oos.close();
             fail("Serializing deep stub of non-serializable type should have thrown");
         } catch (Exception e) {
             assertTrue("Expected IOException or MockitoException",
                     e instanceof IOException || e instanceof MockitoException);
         }
     }

     // ---------- primitives and String ----------
     interface WithPrimitiveReturns {
         int getInt();
         boolean getBool();
         String getString();
     }

     @Test
     public void shouldReturnDefaultForNonMockableReturnTypes() {
         WithPrimitiveReturns mock = mock(WithPrimitiveReturns.class, Mockito.RETURNS_DEEP_STUBS);
         assertEquals(0, mock.getInt());
         assertFalse(mock.getBool());
         assertNull(mock.getString());
     }

     // ---------- same mock for repeated calls ----------
     @Test
     public void shouldReturnSameDeepStubForRepeatedCalls() {
         NonSerializableFoo mock = mock(NonSerializableFoo.class, Mockito.RETURNS_DEEP_STUBS);
         NonSerializableBar bar1 = mock.getBar();
         NonSerializableBar bar2 = mock.getBar();
         assertSame("Repeated calls must return the same mock", bar1, bar2);
     }

     // ---------- extra interfaces ----------
     interface ExtraMarker extends Serializable {}
     interface WithExtraInterface {
         ExtraMarker getExtra();
     }

     @Test
     public void shouldWorkWithExtraInterfacesOnDeepStub() {
         WithExtraInterface mock = mock(WithExtraInterface.class, Mockito.RETURNS_DEEP_STUBS);
         ExtraMarker extra = mock.getExtra();
         assertNotNull(extra);
         assertTrue(extra instanceof ExtraMarker);
         assertTrue(extra instanceof Serializable);
     }

     // ---------- fully serializable type (sanity check) ----------
     interface FullySerializable extends Serializable {
         FullySerializable getNext();
     }

     @Test
     public void shouldWorkWithFullySerializableType() {
         FullySerializable mock = mock(FullySerializable.class, Mockito.RETURNS_DEEP_STUBS);
         FullySerializable next = mock.getNext();
         assertNotNull(next);
         // just accessing should not throw
         assertNotNull(next.toString());
     }

     // ---------- stubbing on a deep stub ----------
     @Test
     public void shouldAllowStubbingOnDeepStub() {
         NonSerializableFoo mock = mock(NonSerializableFoo.class, Mockito.RETURNS_DEEP_STUBS);
         when(mock.getBar().doSomething()).thenReturn("custom");
         assertEquals("custom", mock.getBar().doSomething());
     }

     // ---------- void method on deep stubbed parent ----------
     interface WithVoidMethod {
         void doVoid();
         NonSerializableBar getBar();
     }

     @Test
     public void shouldHandleVoidMethodsGracefully() {
         WithVoidMethod mock = mock(WithVoidMethod.class, Mockito.RETURNS_DEEP_STUBS);
         mock.doVoid(); // should not throw
         NonSerializableBar bar = mock.getBar();
         assertNotNull(bar);
     }

     // ---------- access on a mock that has a stubbing already ----------
     @Test
     public void shouldReturnStubbedValueInsteadOfDeepStubWhenStubbingMatches() {
         NonSerializableFoo mock = mock(NonSerializableFoo.class, Mockito.RETURNS_DEEP_STUBS);
         NonSerializableBar stubbedBar = mock(NonSerializableBar.class);
         when(mock.getBar()).thenReturn(stubbedBar);
         assertSame(stubbedBar, mock.getBar());
     }
 }
