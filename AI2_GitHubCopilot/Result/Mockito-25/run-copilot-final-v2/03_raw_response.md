package org.mockito.internal.stubbing.defaultanswers;

 import static org.junit.Assert.*;

 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 import org.junit.Test;
 import org.mockito.Mockito;

 @SuppressWarnings({"rawtypes", "unchecked"})
 public class ReturnsDeepStubsTest {

     // -- Interfaces for testing generic deep stubs --

     interface MethodWithMultipleBounds {
         <T extends Comparable & Cloneable> T call();
     }

     interface ClassWithTypeVarAndMultipleBounds<K extends Comparable & Cloneable> {
         K get();
     }

     interface ParameterizedReturnBoundToClassTypeVar<K extends Comparable & Cloneable> {
         Set<K> getSet();
     }

     interface WithBoundedWildcard {
         Map.Entry<? extends Comparable, ? extends Serializable> getEntry();
     }

     interface ListOfStringHolder {
         List<String> getList();
     }

     interface WithNestedGenericEntry {
         Map.Entry<String, String> getEntry();
     }

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
     }

     @Test
     public void shouldCreateMockForParameterizedTypeThatReferencesMultiBoundClassTypeVar() {
         ParameterizedReturnBoundToClassTypeVar mock = Mockito.mock(
                 ParameterizedReturnBoundToClassTypeVar.class, new ReturnsDeepStubs());
         Set set = mock.getSet();
         Iterator<?> iter = set.iterator();
         Object element = iter.next();
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