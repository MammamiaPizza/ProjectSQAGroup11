package org.mockito.internal.stubbing.defaultanswers;

 import static org.junit.Assert.*;

 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 import org.junit.Test;
 import org.mockito.Mockito;

 /**
  * Tests for {@link ReturnsDeepStubs} that expose the bug where mock creation does not
  * respect all upper bounds of type variables and improperly handles generic metadata,
  * leading to ClassCastException.
  */
 @SuppressWarnings({"rawtypes", "unchecked"})
 public class ReturnsDeepStubsTest {

     // -- Interfaces for testing generic deep stubs --

     /** Method type variable with multiple bounds (Comparable & Cloneable). */
     interface MethodWithMultipleBounds {
         <T extends Comparable & Cloneable> T call();
     }

     /** Class type variable with multiple bounds. */
     interface ClassWithTypeVarAndMultipleBounds<K extends Comparable & Cloneable> {
         K get();
     }

     /**
      * Return type is a parameterized type that references a type variable on the class
      * which itself has multiple bounds.
      */
     interface ParameterizedReturnBoundToClassTypeVar<K extends Comparable & Cloneable> {
         Set<K> getSet();
     }

     /** Return type declared with a bounded wildcard. */
     interface WithBoundedWildcard {
         Map.Entry<? extends Comparable, ? extends Serializable> getEntry();
     }

     /** Non-mockable nested generic: a List whose element type is String (not mockable). */
     interface ListOfStringHolder {
         List<String> getList();
     }

     /** Nested generic returning Map.Entry<String, String>. */
     interface WithNestedGenericEntry {
         Map.Entry<String, String> getEntry();
     }

     /**
      * Deep generic nest as in the class-level example:
      * {@code GenericsNest<k extends Comparable<k> & Cloneable> extends Map<k, Set<Number>>}.
      */
     interface GenericsNest<K extends Comparable<K> & Cloneable> extends Map<K, Set<Number>> {}

     // -- Tests --

     @Test
     public void shouldReturnMockImplementingAllBoundsForMethodTypeVariable() {
         MethodWithMultipleBounds mock = Mockito.mock(MethodWithMultipleBounds.class,
                 new ReturnsDeepStubs());
         Object result = mock.call();
         assertTrue(result instanceof Comparable);
         assertTrue(result instanceof Cloneable);
     }

     @Test
     public void shouldReturnMockImplementingAllBoundsForClassTypeVariable() {
         ClassWithTypeVarAndMultipleBounds mock =
Mockito.mock(ClassWithTypeVarAndMultipleBounds.class,
                 new ReturnsDeepStubs());
         Object result = mock.get();
         assertTrue(result instanceof Comparable);
         assertTrue(result instanceof Cloneable);
     }

     @Test
     public void shouldReturnMockForBoundedWildcardReturnType() {
         WithBoundedWildcard mock = Mockito.mock(WithBoundedWildcard.class, new ReturnsDeepStubs());
         Map.Entry<?, ?> entry = mock.getEntry();
         assertNotNull(entry);
         // The returned mock must be castable to Map.Entry
     }

     @Test
     public void shouldCreateMockForParameterizedTypeThatReferencesMultiBoundClassTypeVar() {
         ParameterizedReturnBoundToClassTypeVar mock = Mockito.mock(
                 ParameterizedReturnBoundToClassTypeVar.class, new ReturnsDeepStubs());
         Set set = mock.getSet();
         Iterator<?> iterator = set.iterator();
         Object element = iterator.next();
         assertTrue("Element should be instanceof Comparable", element instanceof Comparable);
         assertTrue("Element should be instanceof Cloneable", element instanceof Cloneable);
     }

     @Test
     public void shouldReturnDefaultValueOnNonMockableNestedGeneric() {
         ListOfStringHolder mock = Mockito.mock(ListOfStringHolder.class, new ReturnsDeepStubs());
         List<String> list = mock.getList();
         Object value = list.get(0);
         // Default value for String is null; buggy version might return a mock, causing this to
fail.
         assertNull("Non-mockable nested generic should return null", value);
     }

     @Test
     public void shouldSupportChainedGenericDeepStubs() {
         GenericsNest mock = Mockito.mock(GenericsNest.class, new ReturnsDeepStubs());
         // Chained call that exercises multiple levels of generic deep stubbing
         Number number = mock.entrySet().iterator().next().getValue().iterator().next();
         assertNotNull(number);
     }

     @Test
     public void shouldReturnWorkingMockForNestedGenericEntry() {
         WithNestedGenericEntry mock = Mockito.mock(WithNestedGenericEntry.class, new
ReturnsDeepStubs());
         Map.Entry<String, String> entry = mock.getEntry();
         assertNotNull(entry);
         assertTrue(entry instanceof Map.Entry);
     }

     @Test
     public void
shouldReturnMockForClassTypeVarWithMultipleBoundsWhenMethodReturnTypeIsParameterized() {
         ClassWithTypeVarAndMultipleBounds mock =
Mockito.mock(ClassWithTypeVarAndMultipleBounds.class,
                 new ReturnsDeepStubs());
         Object result = mock.get();
         assertTrue(result instanceof Comparable);
         assertTrue(result instanceof Cloneable);
     }
 }