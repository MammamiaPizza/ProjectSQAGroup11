package com.fasterxml.jackson.databind.jsontype.impl;

 import static org.junit.Assert.*;
 import static org.mockito.ArgumentMatchers.*;
 import static org.mockito.Mockito.*;

 import java.io.IOException;
 import java.util.concurrent.ConcurrentHashMap;

 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.Mock;
 import org.mockito.junit.MockitoJUnitRunner;

 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.BeanProperty;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.JsonDeserializer;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
 import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
 import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
 import com.fasterxml.jackson.databind.type.TypeFactory;
 import com.fasterxml.jackson.databind.util.ClassUtil;

 @RunWith(MockitoJUnitRunner.class)
 public class TypeDeserializerBaseTest {

     @Mock private TypeIdResolver idResolver;
     @Mock private DeserializationContext ctxt;
     @Mock private TypeFactory typeFactory;
     @Mock private BeanProperty property;
     @Mock private JsonDeserializer<Object> stringDeserializer;
     @Mock private JsonDeserializer<Object> longDeserializer;
     @Mock private JsonDeserializer<Object> customDeserializer;
     @Mock private JavaType baseType;
     @Mock private JavaType stringType;
     @Mock private JavaType longType;
     @Mock private JavaType unknownType;

     private TestTypeDeserializer deserializer;

     @Before
     public void setUp() throws Exception {
         deserializer = new TestTypeDeserializer(baseType, idResolver, "type", false, null);
         // common stubs
         when(baseType.getRawClass()).thenReturn(Object.class);
         when(baseType.getClass()).thenReturn((Class) baseType.getClass()); // real SimpleType, for
instance
         when(stringType.getRawClass()).thenReturn(String.class);
         when(stringType.getClass()).thenReturn((Class) stringType.getClass());
         when(stringType.hasGenericTypes()).thenReturn(false);
         when(longType.getRawClass()).thenReturn(Long.class);
         when(longType.getClass()).thenReturn((Class) longType.getClass());
         when(longType.hasGenericTypes()).thenReturn(false);
         when(unknownType.getRawClass()).thenReturn(java.util.Map.class);
         when(unknownType.getClass()).thenReturn((Class) unknownType.getClass());
         when(unknownType.hasGenericTypes()).thenReturn(false);
         when(ctxt.getTypeFactory()).thenReturn(typeFactory);
         when(typeFactory.constructSpecializedType(any(JvaType.class), any(Class.class)))
             .thenReturn(stringType); // simplified
         when(ctxt.findContextualValueDeserializer(eq(stringType), any(BeanProperty.class)))
             .thenReturn(stringDeserializer);
         when(ctxt.findContextualValueDeserializer(eq(longType), any(BeanProperty.class)))
             .thenReturn(longDeserializer);
         when(ctxt.findContextualValueDeserializer(eq(unknownType), any(BeanProperty.class)))
             .thenReturn(customDeserializer);
     }

     // ---------- concrete subclass that exposes protected methods ----------
     private static class TestTypeDeserializer extends TypeDeserializerBase {
         public TestTypeDeserializer(JavaType baseType, TypeIdResolver idRes, String
typePropertyName,
                                     boolean typeIdVisible, JavaType defaultImpl) {
             super(baseType, idRes, typePropertyName, typeIdVisible, defaultImpl);
         }

         public TestTypeDeserializer(TestTypeDeserializer src, BeanProperty property) {
             super(src, property);
         }

         @Override
         public TypeDeserializer forProperty(BeanProperty prop) {
             return new TestTypeDeserializer(this, prop);
         }

         @Override
         public JsonTypeInfo.As getTypeInclusion() {
             return JsonTypeInfo.As.PROPERTY;
         }

         // expose protected methods
         public JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, String
typeId)
                 throws IOException {
             return _findDeserializer(ctxt, typeId);
         }

         public JavaType handleUnknownTypeId(DeserializationContext ctxt, String typeId)
                 throws IOException {
             return _handleUnknownTypeId(ctxt, typeId);
         }

         public JavaType handleMissingTypeId(DeserializationContext ctxt, String extraDesc)
                 throws IOException {
             return _handleMissingTypeId(ctxt, extraDesc);
         }

         public JsonDeserializer<Object> findDefaultImplDeserializer(DeserializationContext ctxt)
                 throws IOException {
             return _findDefaultImplDeserializer(ctxt);
         }
     }

     // ---------- 1. Known type id resolves directly ----------
     @Test
     public void testFindDeserializerKnownTypeId() throws Exception {
         when(idResolver.typeFromId(ctxt, "string")).thenReturn(stringType);

         JsonDeserializer<Object> result = deserializer.findDeserializer(ctxt, "string");

         assertNotNull("Known type id must yield a deserializer", result);
         assertSame(stringDeserializer, result);
         verify(idResolver).typeFromId(ctxt, "string");
     }

     // ---------- 2. Unknown type id falls back to default impl ----------
     @Test
     public void testFindDeserializerUnknownTypeIdWithDefaultImpl() throws Exception {
         // create instance with a default impl type
         TestTypeDeserializer withDefault = new TestTypeDeserializer(baseType, idResolver, "type",
                 false, stringType); // defaultImpl = stringType
         when(idResolver.typeFromId(ctxt, "unknown")).thenReturn(null);
         // default impl deserializer will be resolved lazily
         when(ctxt.findContextualValueDeserializer(eq(stringType), isNull(BeanProperty.class)))
             .thenReturn(stringDeserializer);
         when(stringType.getRawClass()).thenReturn(String.class);

         JsonDeserializer<Object> result = withDefault.findDeserializer(ctxt, "unknown");

         assertNotNull("Default impl should provide a deserializer", result);
         assertSame(stringDeserializer, result);
     }

     // ---------- 3. Unknown type id, no default, no handler -> exception ----------
     @Test(expected = JsonMappingException.class)
     public void testFindDeserializerUnknownTypeIdNoDefaultNoHandler() throws Exception {
         when(idResolver.typeFromId(ctxt, "noSuchType")).thenReturn(null);
         when(ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)).thenReturn(true);
         // default context handleUnknownTypeId throws when no handler and feature enabled
         when(ctxt.handleUnknownTypeId(eq(baseType), eql("noSuchType"), eq(idResolver),
anyString()))
             .thenThrow(new JsonMappingException(ctxt.getParser(), "Unknown type id"));

         deserializer.findDeserializer(ctxt, "noSuchType");
     }

     // ---------- 4. Unknown type id handled by problem handler -> must not return null (fix #2221)
----------
     @Test
     public void testFindDeserializerUnknownTypeIdWithProblemHandler() throws Exception {
         when(idResolver.typeFromId(ctxt, "customType")).thenReturn(null);
         when(ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)).thenReturn(true);
         // simulate problem handler returning a valid type
         when(ctxt.handleUnknownTypeId(eq(baseType), eql("customType"), eq(idResolver),
anyString()))
             .thenReturn(unknownType);
         when(ctxt.findContextualValueDeserializer(eq(unknownType), isNull(BeanProperty.class)))
             .thenReturn(ustomDeserializer);

         JsonDeserializer<Object> result = deserializer.findDeserializer(ctxt, "customType");

         // Bug #2221: without the fix, _findDeserializer returns null here when handler returns a
type.
         // After the fix, it must resolve a deserializer (non-null) or throw.
         assertNotNull("Problem handler resolved type must yield a deserializer", result);
         assertSame(ustomDeserializer, result);
     }

     // ---------- 5. Null type id ----------
     @Test
     public void testFindDeserializerNullTypeId() throws Exception {
         when(idResolver.typeFromId(ctxt, null)).thenReturn(null);
         when(ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)).thenReturn(false);
         // with FAIL_ON_INVALID_SUBTYPE disabled and null default impl, NullifyingDeserializer
should be returned
         JsonDeserializer<Object> result = deserializer.findDeserializer(ctxt, null);

         assertTrue("Null type id should result in NullifyingDeserializer",
                 result instanceof NullifyingDeserializer);
     }

     // ---------- 6. Empty type id ----------
     @Test
     public void testFindDeserializerEmptyTypeId() throws Exception {
         when(idResolver.typeFromId(ctxt, "")).thenReturn(null);
         when(ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)).thenReturn(true);
         when(ctxt.handleUnknownTypeId(eq(baseType), eql(""), eq(idResolver), anyString()))
             .thenThrow(new JsonMappingException(ctxt.getParser(), "Empty type id"));

         try {
             deserializer.findDeserializer(ctxt, "");
             fail("Should have thrown for empty type id");
         } catch (JsonMappingException e) {
             // expected
         }
     }

     // ---------- 7. _handleUnknownTypeId delegates to context ----------
     @Test
     public void testHandleUnknownTypeIdDelegates() throws Exception {
         when(idResolver.getDescForKnownTypeIds()).thenReturn("String, Long");
         when(ctxt.handleUnknownTypeId(eq(baseType), eql("abc"), eq(idResolver), contains("String,
Long")))
             .thenReturn(stringType);

         JavaType result = deserializer.handleUnknownTypeId(ctxt, "abc");

         assertNotNull(result);
         assertSame(stringType, result);
         verify(ctxt).handleUnknownTypeId(eq(baseType), eql("abc"), eq(idResolver), anyString());
     }

     // ---------- 8. _handleMissingTypeId delegates to context ----------
     @Test
     public void testHandleMissingTypeIdDelegates() throws Exception {
         String extraDesc = "missing in payload";
         when(ctxt.handleMissingTypeId(eq(baseType), eq(idResolver), eql(extraDesc)))
             .thenReturn(longType);

         JavaType result = deserializer.handleMissingTypeId(ctxt, extraDesc);

         assertNotNull(result);
         assertSame(longType, result);
         verify(ctxt).handleMissingTypeId(eq(baseType), eq(idResolver), eql(extraDesc));
     }

     // ---------- 9. Default impl deserializer returns NullifyingDeserializer when defaultImpl is
null ----------
     @Test
     public void testFindDefaultImplDeserializerWithNullDefault() throws Exception {
         when(ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)).thenReturn(false);

         JsonDeserializer<Object> result = deserializer.findDefaultImplDeserializer(ctxt);

         assertTrue("Null default impl should return NullifyingDeserializer",
                 result instanceof NullifyingDeserializer);
     }

     // ---------- 10. Default impl deserializer returns NullifyingDeserializer for bogus class
----------
     @Test
     public void testFindDefaultImplDeserializerWithBogusClass() throws Exception {
         JavaType voidType = mock(JvaType.class);
         when(voidType.getRawClass()).thenReturn(Void.class);
         TestTypeDeserializer bogusImpl = new TestTypeDeserializer(baseType, idResolver,
                 "type", false, voidType);

         JsonDeserializer<Object> result = bogusImpl.findDefaultImplDeserializer(ctxt);

         assertTrue("Bogus default impl should return NullifyingDeserializer",
                 result instanceof NullifyingDeserializer);
     }

     // ---------- 11. Default impl deserializer caches and returns real deserializer ----------
     @Test
     public void testFindDefaultImplDeserializerCaches() throws Exception {
         when(stringType.getRawClass()).thenReturn(String.class);
         when(ctxt.findContextualValueDeserializer(eq(stringType), isNull(BeanProperty.class)))
             .thenReturn(stringDeserializer);
         TestTypeDeserializer withDefault = new TestTypeDeserializer(baseType, idResolver,
                 "type", false, stringType);

         JsonDeserializer<Object> result1 = withDefault.findDefaultImplDeserializer(ctxt);
         JsonDeserializer<Object> result2 = withDefault.findDefaultImplDeserializer(ctxt);

         assertSame("Default impl deserializer must be cached", result1, result2);
         assertSame(stringDeserializer, result1);
         verify(ctxt, times(1)).findContextualValueDeserializer(eq(stringType),
isNull(BeanProperty.class));
     }

     // ---------- 12. Find deserializer with type that has generic types bypasses specialization
----------
     @Test
     public void testFindDeserializerWithGenericType() throws Exception {
         when(idResolver.typeFromId(ctxt, "generic")).thenReturn(stringType);
         when(stringType.hasGenericTypes()).thenReturn(true); // has generic types -> retains
original type
         when(ctxt.findContextualValueDeserializer(eq(stringType), isNull(BeanProperty.class)))
             .thenReturn(stringDeserializer);

         JsonDeserializer<Object> result = deserializer.findDeserializer(ctxt, "generic");

         assertNotNull(result);
         assertSame(stringDeserializer, result);
         // specialization must NOT have been called
         verify(typeFactory, never()).constructSpecializedType(any(JvaType.class),
any(Class.class));
     }
 }
