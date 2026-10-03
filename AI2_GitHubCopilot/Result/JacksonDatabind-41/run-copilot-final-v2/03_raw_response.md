package com.fasterxml.jackson.databind.type;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.lang.reflect.Type;
 import java.lang.reflect.TypeVariable;
 import java.lang.reflect.WildcardType;

 import org.junit.Test;
 import org.powermock.reflect.Whitebox;

 import com.fasterxml.jackson.databind.JavaType;

 /**
  * Regression test for issue #1115: TypeFactory fails with IllegalArgumentException
  * "Unrecognized Type: [null]" when encountering wildcard types or type variables
  * whose bounds contain null or are empty (e.g. raw unbounded wildcards).
  */
 public class TypeFactoryNullBoundsTest {
     private final TypeFactory factory = TypeFactory.defaultInstance();
     private final TypeBindings bindings = TypeBindings.emptyBindings();

     // Helper to create a simple ClassStack for testing
     private ClassStack classStack() {
         return new ClassStack(Object.class);
     }

     @Test
     public void testWildcardNullUpperBoundElement() throws Exception {
         WildcardType wildcard = mock(WildcardType.class);
         // simulate an upper bound array with a null entry
         when(wildcard.getUpperBounds()).thenReturn(new Type[] { null });
         when(wildcard.getLowerBounds()).thenReturn(new Type[0]);
         // should not throw; the fix maps null bounds to Object
         JavaType result = Whitebox.invokeMethod(factory, "_fromWildcard",
                 classStack(), wildcard, bindings);
         assertNotNull(result);
         assertEquals(Object.class, result.getRawClass());
     }

     @Test
     public void testWildcardEmptyUpperBounds() throws Exception {
         WildcardType wildcard = mock(WildcardType.class);
         when(wildcard.getUpperBounds()).thenReturn(new Type[0]);
         when(wildcard.getLowerBounds()).thenReturn(new Type[0]);
         // should resolve to Object (unbounded wildcard) without exception
         JavaType result = Whitebox.invokeMethod(factory, "_fromWildcard",
                 classStack(), wildcard, bindings);
         assertNotNull(result);
         assertEquals(Object.class, result.getRawClass());
     }

     @Test
     public void testWildcardNormalExtends() throws Exception {
         WildcardType wildcard = mock(WildcardType.class);
         when(wildcard.getUpperBounds()).thenReturn(new Type[] { Number.class });
         when(wildcard.getLowerBounds()).thenReturn(new Type[0]);
         JavaType result = Whitebox.invokeMethod(factory, "_fromWildcard",
                 classStack(), wildcard, bindings);
         assertNotNull(result);
         assertEquals(Number.class, result.getRawClass());
     }

     @Test
     public void testTypeVariableNullBound() throws Exception {
         TypeVariable<?> tv = mock(TypeVariable.class);
         when(tv.getBounds()).thenReturn(new Type[] { null });
         when(tv.getGenericDeclaration()).thenReturn(TypeFactoryNullBoundsTest.class);
         when(tv.getName()).thenReturn("T");
         JavaType result = Whitebox.invokeMethod(factory, "_fromVariable",
                 classStack(), tv, bindings);
         assertNotNull(result);
         assertEquals(Object.class, result.getRawClass());
     }

     @Test
     public void testTypeVariableEmptyBounds() throws Exception {
         TypeVariable<?> tv = mock(TypeVariable.class);
         when(tv.getBounds()).thenReturn(new Type[0]);
         when(tv.getGenericDeclaration()).thenReturn(TypeFactoryNullBoundsTest.class);
         when(tv.getName()).thenReturn("E");
         JavaType result = Whitebox.invokeMethod(factory, "_fromVariable",
                 classStack(), tv, bindings);
         assertNotNull(result);
         assertEquals(Object.class, result.getRawClass());
     }

     @Test
     public void testTypeVariableWithBound() throws Exception {
         TypeVariable<?> tv = mock(TypeVariable.class);
         when(tv.getBounds()).thenReturn(new Type[] { Comparable.class });
         when(tv.getGenericDeclaration()).thenReturn(TypeFactoryNullBoundsTest.class);
         when(tv.getName()).thenReturn("C");
         JavaType result = Whitebox.invokeMethod(factory, "_fromVariable",
                 classStack(), tv, bindings);
         assertNotNull(result);
         assertEquals(Comparable.class, result.getRawClass());
     }

     @Test
     public void testWildcardNullUpperBoundViaAny() throws Exception {
         // Simulate arriving at _fromAny with a WildcardType containing null bounds
         WildcardType wildcard = mock(WildcardType.class);
         when(wildcard.getUpperBounds()).thenReturn(new Type[] { null });
         when(wildcard.getLowerBounds()).thenReturn(new Type[0]);
         // _fromAny dispatches to _fromWildcard
         JavaType result = Whitebox.invokeMethod(factory, "_fromAny",
                 classStack(), wildcard, bindings);
         assertNotNull(result);
         assertEquals(Object.class, result.getRawClass());
     }

     @Test
     public void testVariableNullBoundViaAny() throws Exception {
         TypeVariable<?> tv = mock(TypeVariable.class);
         when(tv.getBounds()).thenReturn(new Type[] { null });
         when(tv.getGenericDeclaration()).thenReturn(TypeFactoryNullBoundsTest.class);
         when(tv.getName()).thenReturn("V");
         JavaType result = Whitebox.invokeMethod(factory, "_fromAny",
                 classStack(), tv, bindings);
         assertNotNull(result);
         assertEquals(Object.class, result.getRawClass());
     }

     @Test
     public void testNestedWildcardWithNullInnerBound() throws Exception {
         // Simulate ? extends ? where inner wildcard has null upper bound
         WildcardType inner = mock(WildcardType.class);
         when(inner.getUpperBounds()).thenReturn(new Type[] { null });
         when(inner.getLowerBounds()).thenReturn(new Type[0]);

         WildcardType outer = mock(WildcardType.class);
         when(outer.getUpperBounds()).thenReturn(new Type[] { inner });
         when(outer.getLowerBounds()).thenReturn(new Type[0]);

         JavaType result = Whitebox.invokeMethod(factory, "_fromAny",
                 classStack(), outer, bindings);
         assertNotNull(result);
         // The resolved upper bound of inner wildcard is Object -> outer resolves to Object
         assertEquals(Object.class, result.getRawClass());
     }
 }