package org.mockito.internal.util.reflection;

  import org.junit.Test;
  import org.junit.Assert;

  import java.lang.reflect.Method;
  import java.lang.reflect.ParameterizedType;
  import java.lang.reflect.Type;
  import java.lang.reflect.TypeVariable;

  /**
   * Tests for {@link GenericMetadataSupport} focusing on the handling of raw generic types,
   * especially the extraction of raw types from type variables that lack a contextual mapping.
   * The bug #7 / bug report #128 causes a MockitoException "Raw extraction not supported for :
 'null'"
   * when raw nested generics are used in deep stubs.
   */
  public class GenericMetadataSupportTest {

      // ----- Helper generics for constructing reflective test cases -----

      interface GenericInterface<T> {
          T get();
      }

      interface BoundedInterface<T extends Number> {
          T value();
      }

      static class StringGeneric implements GenericInterface<String> {
          @Override public String get() { return null; }
      }

      static class RawContainer {
          GenericInterface rawField;              // raw usage
      }

      // -----------------------------------------------------------------

      @Test
      public void shouldNotThrow_WhenResolvingTypeVariableOnRawType() throws Exception {
          // Bug trigger: raw GenericInterface, resolve its get() method, then obtain rawType().
          GenericMetadataSupport source = GenericMetadataSupport.inferFrom(GenericInterface.class);
          Method getMethod = GenericInterface.class.getMethod("get");

          GenericMetadataSupport resolved = source.resolveGenericReturnType(getMethod);

          // The buggy code throws MockitoException here because the TypeVariable T
          // is not mapped in contextualActualTypeParameters and extractRawTypeOf(null) is called.
          Assert.assertNotNull("rawType() must not be null for a raw type variable",
                  resolved.rawType());
      }

      @Test
      public void rawType_OfTypeVariableWithBound_ShouldReturnFirstBound() throws Exception {
          GenericMetadataSupport source = GenericMetadataSupport.inferFrom(BoundedInterface.class);
          Method valueMethod = BoundedInterface.class.getMethod("value");

          GenericMetadataSupport resolved = source.resolveGenericReturnType(valueMethod);

          // T extends Number  -> rawType should be Number.class
          Assert.assertEquals(Number.class, resolved.rawType());
      }

      @Test
      public void rawType_OfTypeVariableWithActualMapping_ShouldReturnConcreteType() throws
Exception
 {
          GenericMetadataSupport source = GenericMetadataSupport.inferFrom(StringGeneric.class);
          Method getMethod = GenericInterface.class.getMethod("get");

          GenericMetadataSupport resolved = source.resolveGenericReturnType(getMethod);

          Assert.assertEquals(String.class, resolved.rawType());
      }

      @Test
      public void rawType_OfRawClass_ShouldReturnTheClassItself() {
          GenericMetadataSupport gms = GenericMetadataSupport.inferFrom(String.class);
          Assert.assertEquals(String.class, gms.rawType());
      }

      @Test
      public void rawType_OfParameterizedType_ShouldBeTheRawClass() throws Exception {
          // Simulate a ParameterizedType: Map<String, List> where List is raw
          // We can construct this via a simple field with a generic type.
          // Here we use a helper class whose single field has a parameterized type.
          class Helper {
              java.util.Map<String, java.util.List> field;
          }
          java.lang.reflect.Field field = Helper.class.getDeclaredField("field");
          Type genericFieldType = field.getGenericType();
          Assert.assertTrue(genericFieldType instanceof ParameterizedType);

          GenericMetadataSupport gms = GenericMetadataSupport.inferFrom(genericFieldType);
          Assert.assertEquals(java.util.Map.class, gms.rawType());
      }

      @Test
      public void parameterizedTypeWithRawArgument_actualTypeArguments_ShouldBeAccessible() throws
 Exception {
          class Helper {
              java.util.Map<String, java.util.List> field;
          }
          java.lang.reflect.Field field = Helper.class.getDeclaredField("field");
          ParameterizedType pType = (ParameterizedType) field.getGenericType();

          GenericMetadataSupport gms = GenericMetadataSupport.inferFrom(pType);

          java.util.Map<TypeVariable, Type> args = gms.actualTypeArguments();
          Assert.assertNotNull(args);
          Assert.assertFalse(args.isEmpty());
          // The value for Map's second type parameter should be the raw List class
          for (Type value : args.values()) {
              if (value instanceof Class && ((Class<?>) value) == java.util.List.class) {
                  return; // found the raw argument
              }
          }
          Assert.fail("Expected raw List as an actual type argument");
      }

      @Test
      public void extraInterfaces_OfTypeVariableWithMultipleBounds_ShouldReturnInterfaceBounds()
 throws Exception {
          interface MultiBound<T extends Number & java.io.Serializable & Cloneable> {
              T get();
          }
          GenericMetadataSupport source = GenericMetadataSupport.inferFrom(MultiBound.class);
          Method getMethod = MultiBound.class.getMethod("get");
          GenericMetadataSupport resolved = source.resolveGenericReturnType(getMethod);

          java.util.List<Type> extra = resolved.extraInterfaces();
          Assert.assertNotNull(extra);
          // should contain Serializable and Cloneable (Number is the first bound, not in
interfaceBounds)
          Assert.assertTrue(extra.contains(java.io.Serializable.class));
          Assert.assertTrue(extra.contains(Cloneable.class));
      }

      @Test
      public void rawExtraInterfaces_ShouldExcludeTheRawTypeItself() throws Exception {
          interface Iface<T extends Comparable<T>> {
              T get();
          }
          GenericMetadataSupport source = GenericMetadataSupport.inferFrom(Iface.class);
          Method getMethod = Iface.class.getMethod("get");
          GenericMetadataSupport resolved = source.resolveGenericReturnType(getMethod);

          Class<?>[] rawExtra = resolved.rawExtraInterfaces();
          // Only Comparable (if resolved) but should not contain the rawType (Comparable)
          Assert.assertNotNull(rawExtra);
          for (Class<?> c : rawExtra) {
              Assert.assertNotEquals("rawExtraInterfaces must not duplicate rawType",
                      resolved.rawType(), c);
          }
      }

      @Test
      public void notGenericReturnType_RawType_ShouldBeTheClass() throws Exception {
          class NonGenericHelper {
              public String identity(String input) { return input; }
          }
          GenericMetadataSupport source = GenericMetadataSupport.inferFrom(NonGenericHelper.class);
          Method idMethod = NonGenericHelper.class.getMethod("identity", String.class);
          GenericMetadataSupport resolved = source.resolveGenericReturnType(idMethod);
          Assert.assertEquals(String.class, resolved.rawType());
      }

      // Provide the missing getter for RawContainer (reflection on field uses getter? No, we use a
 method)
      // We will add the method dynamically via an inner class.
      static class RawContainerWithGetter {
          GenericInterface rawField;
          public GenericInterface getRawField() { return rawField; }
      }

      @Test
      public void rawFieldReturnType_ShouldNotCauseRawExtractionError() throws Exception {
          GenericMetadataSupport source =
 GenericMetadataSupport.inferFrom(RawContainerWithGetter.class);
          Method getter = RawContainerWithGetter.class.getMethod("getRawField");

          // This return type is a raw GenericInterface (a Class), so NotGenericReturnTypeSupport
          GenericMetadataSupport resolved = source.resolveGenericReturnType(getter);
          Assert.assertNotNull(resolved.rawType());
          Assert.assertEquals(GenericInterface.class, resolved.rawType());
      }

      @Test
      public void inferFrom_parameterizedTypeWithBoundedWildcard_ShouldNotThrow() throws Exception {
          // e.g., List<? extends Number>
          class Helper {
              java.util.List<? extends Number> field;
          }
          java.lang.reflect.Field field = Helper.class.getDeclaredField("field");
          Type genericType = field.getGenericType();
          GenericMetadataSupport gms = GenericMetadataSupport.inferFrom(genericType);
          Assert.assertEquals(java.util.List.class, gms.rawType());
      }
  }