package org.mockitousage.stubbing;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import org.junit.Test;

 import java.io.*;
 import java.util.ArrayList;
 import java.util.List;

 /**
  * Tests that deep stubs created with {@code Mockito.RETURNS_DEEP_STUBS} are
  * serializable and remain functional after deserialization.
  * <p>
  * Bug 399: {@code ReturnsDeepStubs} anonymous inner class was not
  * serializable, causing {@code NotSerializableException}.
  */
 public class DeepStubsSerializableTest {

     // -----------------------------------------------------------------
     // Helper interfaces for deep-stub chains
     // -----------------------------------------------------------------

     public interface Alpha extends Serializable {
         Beta getBeta();
     }

     public interface Beta extends Serializable {
         Gamma getGamma();
     }

     public interface Gamma extends Serializable {
         String getName();
     }

     public interface WithGenerics extends Serializable {
         List<String> getStringList();
     }

     public interface WithPrimitive extends Serializable {
         int getValue();
     }

     public interface WithExtraInterface extends Serializable, Cloneable {
         Alpha getAlpha();
     }

     // -----------------------------------------------------------------
     // Serialization utility
     // -----------------------------------------------------------------

     @SuppressWarnings("unchecked")
     private static <T> T roundtrip(T obj) throws IOException, ClassNotFoundException {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream out = new ObjectOutputStream(bos);
         out.writeObject(obj);
         out.close();

         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
         ObjectInputStream in = new ObjectInputStream(bis);
         return (T) in.readObject();
     }

     // -----------------------------------------------------------------
     // Tests
     // -----------------------------------------------------------------

     /**
      * Verify that serializing a deep-stub mock does not throw
      * a {@code NotSerializableException}.
      */
     @Test
     public void should_serialize_and_deserialize_mock_created_by_deep_stubs() throws Exception {
         Alpha mock = mock(Alpha.class, Mockito.RETURNS_DEEP_STUBS);
         // This must complete without throwing
         Alpha deserialized = roundtrip(mock);
         assertNotNull(deserialized);
     }

     /**
      * After deserialization, methods returning mockable types should still
      * return a non-null deep stub (not raw null).
      */
     @Test
     public void should_return_non_null_deep_stub_after_deserialization() throws Exception {
         Alpha original = mock(Alpha.class, Mockito.RETURNS_DEEP_STUBS);
         Alpha deserialized = roundtrip(original);

         Beta beta = deserialized.getBeta();
         assertNotNull("getBeta() returned null after deserialization", beta);
     }

     /**
      * A three-level deep-stub chain must still work after deserialization.
      */
     @Test
     public void should_support_deep_stub_chain_after_deserialization() throws Exception {
         Alpha original = mock(Alpha.class, Mockito.RETURNS_DEEP_STUBS);
         Alpha deserialized = roundtrip(original);

         Gamma gamma = deserialized.getBeta().getGamma();
         assertNotNull("getBeta().getGamma() returned null after deserialization", gamma);

         String name = gamma.getName();
         assertNotNull("getName() returned null after deserialization", name); // default-answer
string
     }

     /**
      * Deep stubs should respect generic return types after deserialization.
      * <p>
      * For example, a method returning {@code List<String>} should produce a
      * non-null {@code List<String>} mock.
      */
     @Test
     public void should_respect_generic_return_type_after_deserialization() throws Exception {
         WithGenerics original = mock(WithGenerics.class, Mockito.RETURNS_DEEP_STUBS);
         WithGenerics deserialized = roundtrip(original);

         List<String> list = deserialized.getStringList();
         assertNotNull("getStringList() returned null after deserialization", list);

         // Calling a list method should return a deep stub for the type parameter
         assertNotNull("list.isEmpty() returned null", list.isEmpty()); // false by default-answer
     }

     /**
      * For methods returning primitive types (not mockable), deep stub must
      * fall back to default values, and this behaviour must be preserved after
      * deserialization.
      */
     @Test
     public void should_return_default_value_for_primitive_after_deserialization() throws Exception
{
         WithPrimitive original = mock(WithPrimitive.class, Mockito.RETURNS_DEEP_STUBS);
         WithPrimitive deserialized = roundtrip(original);

         int value = deserialized.getValue();
         assertEquals("Primitive default after deserialization", 0, value);
     }

     /**
      * Mocks created with extra interfaces must also survive serialization and
      * continue to produce deep stubs.
      */
     @Test
     public void should_work_with_extra_interfaces_after_deserialization() throws Exception {
         WithExtraInterface original =
                 mock(WithExtraInterface.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
         WithExtraInterface deserialized = roundtrip(original);

         Alpha alpha = deserialized.getAlpha();
         assertNotNull("getAlpha() returned null after deserialization", alpha);
         assertTrue(alpha instanceof Serializable);
     }

     /**
      * The same mock should survive multiple serialization round-trips without
      * breaking deep-stub capability.
      */
     @Test
     public void should_survive_multiple_serialization_roundtrips() throws Exception {
         Alpha original = mock(Alpha.class, Mockito.RETURNS_DEEP_STUBS);

         Alpha once = roundtrip(original);
         assertNotNull(once.getBeta());

         Alpha twice = roundtrip(once);
         assertNotNull(twice.getBeta().getGamma());
     }

     /**
      * After deserialization the same deep-stub call must be repeatable and
      * yield consistent results (non-null mocks).
      */
     @Test
     public void should_return_consistent_deep_stubs_after_deserialization() throws Exception {
         Alpha original = mock(Alpha.class, Mockito.RETURNS_DEEP_STUBS);
         Alpha deserialized = roundtrip(original);

         Beta first = deserialized.getBeta();
         Beta second = deserialized.getBeta();
         assertNotNull(first);
         assertNotNull(second);
         // Deep stub matching: same invocation returns the same mock
         assertSame("Multiple identical calls must return the same deep-stub mock",
                 first, second);
     }

     /**
      * Serialisation of a deep-stub mock that has never been used should work
      * (boundary condition: empty invocation container).
      */
     @Test
     public void should_serialize_untouched_deep_stub_mock() throws Exception {
         Alpha untouched = mock(Alpha.class, Mockito.RETURNS_DEEP_STUBS);
         Alpha deserialized = roundtrip(untouched);
         assertNotNull(deserialized);
         // First invocation after deserialization must still produce deep stub
         assertNotNull(deserialized.getBeta());
     }
 }
