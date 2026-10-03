package org.mockitousage.bugs;

import static org.junit.Assert.;
import static org.mockito.Mockito.;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;

public class DeepStubsWronglyReportsSerializationProblemsTest {

 // --- helper types for deep stubbing ---

 interface NonSerializableInterface {
     String getValue();
 }

 interface Service {
     NonSerializableInterface getNonSerializable();
 }

 interface A {
     B getB();
 }
 interface B {
     C getC();
 }
 interface C {
     String getValue();
 }

 interface PrimitiveService {
     int getId();
 }

 interface GenericService<K> {
     K getKey();
 }

 interface SubInterface {
     String sayHello();
 }

 interface ParentWithExtras {
     SubInterface getSub();
 }

 // --- tests ---

 @Test
 public void should_not_raise_a_mockito_exception_about_serialization_when_accessing_deep_stub()

{
        // bug 99: accessing a deep stub should not throw MockitoException about serialization
        Service mock = mock(Service.class, RETURNS_DEEP_STUBS);
        NonSerializableInterface deepStub = mock.getNonSerializable();
        assertNotNull("deep stub should be returned", deepStub);
        assertNull("default value for String return should be null", deepStub.getValue());
    }

 @Test
 public void shouldReturnSameMockOnMultipleInvocations() {
     Service mock = mock(Service.class, RETURNS_DEEP_STUBS);
     NonSerializableInterface first = mock.getNonSerializable();
     NonSerializableInterface second = mock.getNonSerializable();
     assertNotNull(first);
     assertSame("deep stub must be cached", first, second);
 }

 @Test
 public void shouldReturnDefaultValueForNonMockableReturnType() {
     PrimitiveService mock = mock(PrimitiveService.class, RETURNS_DEEP_STUBS);
     assertEquals("primitive returns default 0", 0, mock.getId());
 }

 @Test
 public void shouldSupportNestedDeepStubChains() {
      A mock = mock(A.class, RETURNS_DEEP_STUBS);
      B b = mock.getB();
      assertNotNull(b);
      C c = b.getC();
      assertNotNull(c);
      assertNull(c.getValue());
 }

 @Test(expected = MockitoException.class)
 public void shouldHandleGenericReturnTypes() {
      @SuppressWarnings("unchecked")
      GenericService<String> mock = mock(GenericService.class, RETURNS_DEEP_STUBS);
      mock.getKey(); // String is final, so deep-stubbing it fails with MockitoException
 }

 @Test
 public void shouldPropagateExtraInterfacesToDeepStubMock() {
      ParentWithExtras mock = mock(ParentWithExtras.class,
              withSettings().extraInterfaces(Serializable.class)
                      .defaultAnswer(RETURNS_DEEP_STUBS));
      SubInterface sub = mock.getSub();
      assertNotNull(sub);
      assertFalse("deep stub should not propagate extra interfaces after fix", sub instanceof
Serializable);
 }

 @Test
 public void shouldAllowSerializationOfDeepStubMockWhenParentIsSerializable() throws Exception {
      Service mock = mock(Service.class,
              withSettings().serializable().defaultAnswer(RETURNS_DEEP_STUBS));
      NonSerializableInterface deepStub = mock.getNonSerializable();
      // serialization round-trip must succeed because the deep stub mock is created with
      // serializable settings (via withSettingsUsing) and Mockito makes it serializable
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      new ObjectOutputStream(baos).writeObject(deepStub);
      // if we reach here no exception was thrown
 }

 @Test
 public void shouldReturnDeepStubMockEvenWhenParentIsSerializable() {
      Service mock = mock(Service.class,
              withSettings().serializable().defaultAnswer(RETURNS_DEEP_STUBS));
      NonSerializableInterface deepStub = mock.getNonSerializable();
      assertNotNull(deepStub);
      deepStub.getValue(); // should succeed without MockitoException
 }

}