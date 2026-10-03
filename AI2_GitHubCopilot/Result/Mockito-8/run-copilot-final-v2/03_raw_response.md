package org.mockito.internal.util.reflection;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.runners.MockitoJUnitRunner;
 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.lang.reflect.*;
 import java.util.*;

 /**
  * Tests for GenericMetadataSupport focusing on self-referencing and cyclic type variables
  * that previously caused StackOverflowError (bug #114).
  */
 @RunWith(MockitoJUnitRunner.class)
 public class GenericMetadataSupportSelfReferringTypeTest {

     // --- helpers to create special TypeVariables ---
     private TypeVariable createSelfRefTypeVariable() {
         TypeVariable tv = mock(TypeVariable.class);
         when(tv.getBounds()).thenReturn(new Type[]{tv}); // bound is itself
         return tv;
     }

     private TypeVariable[] createCyclePair() {
         TypeVariable tvA = mock(TypeVariable.class, "A");
         TypeVariable tvB = mock(TypeVariable.class, "B");
         when(tvA.getBounds()).thenReturn(new Type[]{tvB});
         when(tvB.getBounds()).thenReturn(new Type[]{tvA});
         return new TypeVariable[]{tvA, tvB};
     }

     @SuppressWarnings("unchecked")
     private Class mockClassWithTypeParams(TypeVariable... typeParams) {
         Class mockClazz = mock(Class.class);
         when(mockClazz.getTypeParameters()).thenReturn(typeParams.length == 0 ? new TypeVariable[0]
: typeParams);
         when(mockClazz.getGenericSuperclass()).thenReturn(Object.class);
         when(mockClazz.getGenericInterfaces()).thenReturn(new Type[0]);
         return mockClazz;
     }

     // ==================== Tests for self-referencing bound ====================
     @Test
     public void inferFrom_classWithSelfBoundTypeParameter_shouldNotStackOverflow() {
         TypeVariable selfTV = createSelfRefTypeVariable();
         Class mockClazz = mockClassWithTypeParams(selfTV);
         try {
             GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(mockClazz);
             assertNotNull(metadata);
             // further access should also not overflow
             metadata.actualTypeArguments();
         } catch (StackOverflowError e) {
             fail("inferFrom on class with self-referencing type parameter should not cause
StackOverflowError");
         }
     }

     @Test
     public void inferFrom_methodReturningSelfBoundTypeVariable_shouldNotStackOverflow() {
         TypeVariable selfTV = createSelfRefTypeVariable();
         Method mockMethod = mock(Method.class);
         when(mockMethod.getGenericReturnType()).thenReturn(selfTV);
         when(mockMethod.getTypeParameters()).thenReturn(new TypeVariable[0]);
         GenericMetadataSupport source = GenericMetadataSupport.inferFrom(Object.class);
         try {
             GenericMetadataSupport ret = source.resolveGenericReturnType(mockMethod);
             assertNotNull(ret);
             ret.rawType(); // may trigger registration
         } catch (StackOverflowError e) {
             fail("resolveGenericReturnType on self-referencing TypeVariable should not cause
StackOverflowError");
         }
     }

     @Test
     public void extraInterfaces_withSelfBoundTypeVariable_shouldNotStackOverflow() {
         TypeVariable selfTV = createSelfRefTypeVariable();
         Method mockMethod = mock(Method.class);
         when(mockMethod.getGenericReturnType()).thenReturn(selfTV);
         when(mockMethod.getTypeParameters()).thenReturn(new TypeVariable[0]);
         GenericMetadataSupport source = GenericMetadataSupport.inferFrom(Object.class);
         GenericMetadataSupport ret = source.resolveGenericReturnType(mockMethod);
         try {
             ret.extraInterfaces();
         } catch (StackOverflowError e) {
             fail("extraInterfaces on self-referencing type variable should not cause
StackOverflowError");
         }
     }

     // ==================== Indirect cycle tests ====================
     @Test
     public void inferFrom_classWithCyclicTypeParameterBounds_shouldNotStackOverflow() {
         TypeVariable[] cycle = createCyclePair();
         Class mockClazz = mockClassWithTypeParams(cycle);
         when(mockClazz.getName()).thenReturn("CyclicClass");
         try {
             GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(mockClazz);
             Map<TypeVariable, Type> args = metadata.actualTypeArguments();
             assertNotNull(args);
         } catch (StackOverflowError e) {
             fail("Cyclic bounds between two type parameters should not cause StackOverflowError");
         }
     }

     @Test
     public void parameterizedTypeWithCycleInActualTypeArguments_shouldNotStackOverflow() {
         TypeVariable tv1 = mock(TypeVariable.class, "X");
         when(tv1.getBounds()).thenReturn(new Type[]{Object.class});
         TypeVariable tv2 = mock(TypeVariable.class, "Y");
         when(tv2.getBounds()).thenReturn(new Type[]{Object.class});
         Class rawType = mockClassWithTypeParams(tv1, tv2);
         ParameterizedType paramType = mock(ParameterizedType.class);
         when(paramType.getRawType()).thenReturn(rawType);
         when(paramType.getActualTypeArguments()).thenReturn(new Type[]{tv2, tv1}); // cycle in
mapping
         try {
             GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(paramType);
             metadata.actualTypeArguments(); // should not overflow
         } catch (StackOverflowError e) {
             fail("Cyclic actual type argument mapping should not cause StackOverflowError");
         }
     }

     // ==================== Normal behavior (no cycles) ====================
     @Test
     public void inferFrom_plainClass_shouldWork() {
         GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(List.class);
         assertEquals(List.class, metadata.rawType());
         Map<TypeVariable, Type> args = metadata.actualTypeArguments();
         assertTrue("List<E> should have one type argument", args.size() >= 1);
     }

     @SuppressWarnings("serial")
     @Test
     public void inferFrom_parameterizedType_shouldStoreActualArguments() {
         // ArrayList<String> as a ParameterizedType
         ParameterizedType stringListType = (ParameterizedType) new
ArrayList<String>(){}.getClass().getGenericSuperclass();
         GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(stringListType);
         Map<TypeVariable, Type> args = metadata.actualTypeArguments();
         // E -> String
         TypeVariable<?> typeVar = ((Class<?>) stringListType.getRawType()).getTypeParameters()[0];
         assertEquals(String.class, args.get(typeVar));
     }

     @Test
     public void inferFrom_wildcardBound_shouldNotStackOverflow() {
         // mock a ParameterizedType with wildcard actual argument
         TypeVariable<?> eVar = List.class.getTypeParameters()[0];
         Class rawType = List.class;
         WildcardType wildcard = mock(WildcardType.class);
         when(wildcard.getUpperBounds()).thenReturn(new Type[]{Number.class});
         when(wildcard.getLowerBounds()).thenReturn(new Type[0]);
         ParameterizedType paramType = mock(ParameterizedType.class);
         when(paramType.getRawType()).thenReturn(rawType);
         when(paramType.getActualTypeArguments()).thenReturn(new Type[]{wildcard});
         GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(paramType);
         Type actual = metadata.getActualTypeArgumentFor(eVar);
         assertNotNull(actual);
         // should be a BoundedType wrapping the wildcard
     }

     @Test
     public void getActualTypeArgumentFor_chainedTypeVariables_shouldResolveToFinalType() {
         TypeVariable tv1 = mock(TypeVariable.class, "T1");
         when(tv1.getBounds()).thenReturn(new Type[]{Object.class});
         TypeVariable tv2 = mock(TypeVariable.class, "T2");
         when(tv2.getBounds()).thenReturn(new Type[]{Object.class});
         // raw type with [T1,T2]
         Class rawType = mockClassWithTypeParams(tv1, tv2);
         ParameterizedType paramType = mock(ParameterizedType.class);
         when(paramType.getRawType()).thenReturn(rawType);
         // T1 -> tv2, T2 -> String
         when(paramType.getActualTypeArguments()).thenReturn(new Type[]{tv2, String.class});
         GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(paramType);
         Type result = metadata.getActualTypeArgumentFor(tv1);
         assertEquals(String.class, result); // T1 -> tv2 -> String
     }

     @Test
     public void extraInterfaces_normalTypeVariable_shouldReturnEmptyList() {
         // FromClassGenericMetadataSupport for Object returns empty
         GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(Object.class);
         assertTrue(metadata.extraInterfaces().isEmpty());
     }

     @Test
     public void inferFrom_withMultipleTypeVariables_shouldNotStackOverflow() {
         // mock class with three independent type variables
         TypeVariable tvA = mock(TypeVariable.class, "A");
         TypeVariable tvB = mock(TypeVariable.class, "B");
         TypeVariable tvC = mock(TypeVariable.class, "C");
         when(tvA.getBounds()).thenReturn(new Type[]{Object.class});
         when(tvB.getBounds()).thenReturn(new Type[]{Object.class});
         when(tvC.getBounds()).thenReturn(new Type[]{Object.class});
         Class mockClazz = mockClassWithTypeParams(tvA, tvB, tvC);
         try {
             GenericMetadataSupport metadata = GenericMetadataSupport.inferFrom(mockClazz);
             metadata.actualTypeArguments();
         } catch (StackOverflowError e) {
             fail("Multiple type variables without cycles should not cause StackOverflowError");
         }
     }

 }