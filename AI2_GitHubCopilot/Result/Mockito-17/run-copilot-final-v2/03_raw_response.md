package org.mockito.internal.creation;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.io.Serializable;
 import java.util.Arrays;

 import org.junit.Test;
 import org.mockito.exceptions.base.MockitoException;
 import org.mockito.internal.util.MockUtil;

 /**
  * Tests for MockSettingsImpl serialization behaviour (related to bug #152).
  * The main defect: {@code serializable()} followed by {@code extraInterfaces(...)}
  * overwrites the {@code Serializable} interface instead of merging it.
  */
 public class MockSettingsImplSerializableTest {

     // Helper interfaces for testing
     interface Foo {}
     interface Bar {}
     class NonInterfaceClass {}

     // -- MockSettingsImpl tests --

     @Test
     public void serializableShouldMakeSettingsSerializable() {
         MockSettingsImpl settings = new MockSettingsImpl();
         settings.serializable();
         assertTrue(settings.isSerializable());
     }

     @Test
     public void serializableShouldAddSerializableToExtraInterfaces() {
         MockSettingsImpl settings = new MockSettingsImpl();
         settings.serializable();
         Class<?>[] extraInterfaces = settings.getExtraInterfaces();
         assertNotNull(extraInterfaces);
         assertTrue(Arrays.asList(extraInterfaces).contains(Serializable.class));
     }

     @Test
     public void serializableAndExtraInterfacesShouldContainBoth() {
         // This exposes the bug: at b17 serializable() then extraInterfaces() loses Serializable
         MockSettingsImpl settings = new MockSettingsImpl();
         settings.serializable().extraInterfaces(Foo.class);
         Class<?>[] extraInterfaces = settings.getExtraInterfaces();
         assertNotNull(extraInterfaces);
         assertTrue("Should still contain Serializable after extraInterfaces call",
                 Arrays.asList(extraInterfaces).contains(Serializable.class));
         assertTrue("Should also contain Foo",
                 Arrays.asList(extraInterfaces).contains(Foo.class));
     }

     @Test
     public void isSerializableShouldBeFalseByDefault() {
         MockSettingsImpl settings = new MockSettingsImpl();
         assertFalse(settings.isSerializable());
     }

     @Test(expected = MockitoException.class)
     public void extraInterfacesWithNullShouldThrow() {
         new MockSettingsImpl().extraInterfaces((Class<?>[]) null);
     }

     @Test(expected = MockitoException.class)
     public void extraInterfacesWithEmptyArrayShouldThrow() {
         new MockSettingsImpl().extraInterfaces(new Class<?>[0]);
     }

     @Test(expected = MockitoException.class)
     public void extraInterfacesWithNonInterfaceShouldThrow() {
         new MockSettingsImpl().extraInterfaces(NonInterfaceClass.class);
     }

     @Test
     public void multipleExtraInterfacesCallsShouldTakeLatest() {
         MockSettingsImpl settings = new MockSettingsImpl();
         settings.extraInterfaces(Foo.class);
         settings.extraInterfaces(Bar.class);
         Class<?>[] extras = settings.getExtraInterfaces();
         assertEquals(1, extras.length);
         assertEquals(Bar.class, extras[0]);
     }

     // -- MockUtil / Mock creation tests --

     @Test
     public void mockCreatedWithSerializableShouldBeSerializable() {
         MockSettingsImpl settings = new MockSettingsImpl();
         settings.serializable();

         MockUtil mockUtil = new MockUtil();
         Foo mock = mockUtil.createMock(Foo.class, settings);
         assertTrue(mock instanceof Serializable);
     }

     @Test
     public void mockCreatedWithSerializableAndExtraInterfaceShouldBeSerializable() {
         // Bug-exposing: mock should implement both Serializable and the extra interface
         MockSettingsImpl settings = new MockSettingsImpl();
         settings.serializable().extraInterfaces(Foo.class);

         MockUtil mockUtil = new MockUtil();
         Object mock = mockUtil.createMock(Bar.class, settings); // mock a different interface to
avoid confusion
         assertTrue(mock instanceof Serializable);
         assertTrue(mock instanceof Foo);
     }

     @Test
     public void mockCreatedWithoutSerializableShouldNotBeSerializable() {
         MockSettingsImpl settings = new MockSettingsImpl();
         MockUtil mockUtil = new MockUtil();
         Foo mock = mockUtil.createMock(Foo.class, settings);
         assertFalse(mock instanceof Serializable);
     }
 }