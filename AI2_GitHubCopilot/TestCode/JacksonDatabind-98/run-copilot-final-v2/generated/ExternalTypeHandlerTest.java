package com.fasterxml.jackson.databind.deser.impl;

 import static org.junit.Assert.*;
 import static org.mockito.Matchers.any;
 import static org.mockito.Matchers.anyString;
 import static org.mockito.Matchers.eq;
 import static org.mockito.Mockito.*;

 import java.io.IOException;
 import java.lang.reflect.Field;
 import java.util.*;

 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.invocation.InvocationOnMock;
 import org.mockito.stubbing.Answer;
 import org.powermock.api.mockito.PowerMockito;
 import org.powermock.core.classloader.annotations.PrepareForTest;
 import org.powermock.modules.junit4.PowerMockRunner;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
 import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
 import com.fasterxml.jackson.databind.util.TokenBuffer;

 @RunWith(PowerMockRunner.class)
 @PrepareForTest({ TypeDeserializer.class })
 public class ExternalTypeHandlerTest {

     private JavaType beanType;
     private DeserializationContext ctxt;
     private JsonParser parser;

     @Before
     public void setUp() throws Exception {
         beanType = mock(JavaType.class);
         ctxt = mock(DeserializationContext.class);
         parser = mock(JsonParser.class);
         // default: no FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY
         when(ctxt.isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)).then
Return(false);
     }

     // ---------- Builder / build() tests ----------

     @Test
     public void testBuilderLinksTypePropertyFromOtherProps() throws Exception {
         // scenario: otherProps contains the type property
         SettableBeanProperty mainProp = createMockProperty("animal");
         TypeDeserializer typeDeser = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(mainProp, typeDeser);

         // create BeanPropertyMap containing "type" property
         SettableBeanProperty typeProp = createMockProperty("type");
         BeanPropertyMap otherProps = mock(BeanPropertyMap.class);
         when(otherProps.find("type")).thenReturn(typeProp);

         ExternalTypeHandler handler = builder.build(otherProps);
         ExtTypedProperty[] properties = getExtTypedProperties(handler);
         assertEquals(1, properties.length);
         assertNotNull("typeProperty should be linked from otherProps",
                 properties[0].getTypeProperty());
     }

     @Test
     public void testBuilderLinksTypePropertyFromOwnExternalProperties() throws Exception {
         // critical bug scenario: type property is also external, not in otherProps
         SettableBeanProperty mainProp = createMockProperty("animal");
         TypeDeserializer typeDeser = createMockTypeDeserializer("type", null);

         SettableBeanProperty typeProp = createMockProperty("type");
         // "type" property itself may not need a type deserializer, but we add it
         // so that "type" name appears in builder's own property index.
         TypeDeserializer dummyTypeDeser = mock(TypeDeserializer.class);
         when(dummyTypeDeser.getPropertyName()).thenReturn("type");

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(typeProp, dummyTypeDeser); // add the type property as external
         builder.addExternal(mainProp, typeDeser);      // add the main property

         // otherProps does not contain "type"
         BeanPropertyMap otherProps = mock(BeanPropertyMap.class);
         when(otherProps.find(anyString())).thenReturn(null);

         ExternalTypeHandler handler = builder.build(otherProps);
         ExtTypedProperty[] properties = getExtTypedProperties(handler);
         assertEquals(2, properties.length);

         // The main property (index 1 in add order) should have typeProperty linked
         // to the external type property added earlier.
         // In buggy version, build() only checks otherProps, so typeProperty will be null.
         assertNotNull("typeProperty should be linked from builder's own external properties",
                 properties[1].getTypeProperty());
     }

     // ---------- handleTypePropertyValue / handlePropertyValue ----------

     @Test
     public void testHandleTypePropertyValueSetsTypeId() throws Exception {
         SettableBeanProperty mainProp = createMockProperty("animal");
         TypeDeserializer typeDeser = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(mainProp, typeDeser);
         ExternalTypeHandler handler = builder.build(null).start();

         Object bean = new HashMap<>();
         when(parser.getText()).thenReturn("Dog");

         boolean handled = handler.handleTypePropertyValue(parser, ctxt, "type", bean);
         assertTrue("type property should be recognised", handled);

         // typeId should be stored
         String[] typeIds = getField(handler, "_typeIds", String[].class);
         assertEquals("Dog", typeIds[0]);
     }

     @Test
     public void testHandlePropertyValueSetsToken() throws Exception {
         SettableBeanProperty mainProp = createMockProperty("animal");
         TypeDeserializer typeDeser = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(mainProp, typeDeser);
         ExternalTypeHandler handler = builder.build(null).start();

         Object bean = new HashMap<>();
         // parser needs to provide a simple scalar token so TokenBuffer can be created
         when(parser.getText()).thenReturn("not used");
         when(parser.nextToken()).thenReturn(JsonToken.VALUE_STRING, (JsonToken) null);
         when(parser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         // simulate parser copyCurrentStructure
         doAnswer(new Answer<Void>() {
             @Override
             public Void answer(InvocationOnMock invocation) throws Throwable {
                 // copy does nothing in mock
                 return null;
             }
         }).when(parser).copyCurrentStructure(any(JsonParser.class));

         boolean handled = handler.handlePropertyValue(parser, ctxt, "animal", bean);
         assertTrue("property should be handled", handled);

         TokenBuffer[] tokens = getField(handler, "_tokens", TokenBuffer[].class);
         assertNotNull("token buffer should be stored", tokens[0]);
     }

     @Test
     public void testHandlePropertyValueDeserializesWhenBothReady() throws Exception {
         // setup property and type deserializer so that deserializeAndSet can be verified
         SettableBeanProperty mainProp = mock(SettableBeanProperty.class);
         when(mainProp.getName()).thenReturn("animal");
         JavaType propType = mock(JavaType.class);
         when(mainProp.getType()).thenReturn(propType);
         when(mainProp.isRequired()).thenReturn(false);
         // capture bean set
         final Object[] capturedBean = { null };
         final Object[] capturedValue = { null };
         doAnswer(new Answer<Void>() {
             @Override
             public Void answer(InvocationOnMock invocation) {
                 capturedBean[0] = invocation.getArguments()[0];
                 capturedValue[0] = invocation.getArguments()[1];
                 return null;
             }
         }).when(mainProp).set(any(Object.class), any());

         TypeDeserializer typeDeser = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(mainProp, typeDeser);
         ExternalTypeHandler handler = builder.build(null).start();

         Object bean = "bean";

         // first, simulate type property handling
         when(parser.getText()).thenReturn("Dog");
         handler.handleTypePropertyValue(parser, ctxt, "type", bean);

         // then value property handling
         when(parser.getText()).thenReturn("ignored");
         when(parser.nextToken()).thenReturn(JsonToken.VALUE_STRING, (JsonToken) null);
         when(parser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         doNothing().when(parser).copyCurrentStructure(any(JsonParser.class));
         // We also need to stub deserializeAndSet to actually set the value (simulate
deserialization)
         doAnswer(new Answer<Void>() {
             @Override
             public Void answer(InvocationOnMock invocation) {
                 JsonParser p = invocation.getArgumentAt(0, JsonParser.class);
                 DeserializationContext ctx = invocation.getArgumentAt(1,
DeserializationContext.class);
                 Object b = invocation.getArgumentAt(2, Object.class);
                 mainProp.set(b, "deserializedValue");
                 return null;
             }
         }).when(mainProp).deserializeAndSet(any(JsonParser.class),
any(DeserializationContext.class), any(Object.class));

         boolean handled = handler.handlePropertyValue(parser, ctxt, "animal", bean);
         assertTrue(handled);
         // after handlePropertyValue, both typeId and tokens were already present, so
         // _deserializeAndSet should have been called, and set() invoked on bean
         assertEquals("bean should be set", bean, capturedBean[0]);
         assertEquals("deserializedValue", capturedValue[0]);
     }

     @Test
     public void testHandlePropertyValueList() throws Exception {
         // test that when a property name maps to multiple indices (shared type id / token)
         // handlePropertyValue processes all
         SettableBeanProperty prop1 = createMockProperty("prop1");
         SettableBeanProperty prop2 = createMockProperty("prop2");
         TypeDeserializer td1 = createMockTypeDeserializer("type1", null);
         TypeDeserializer td2 = createMockTypeDeserializer("type2", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop1, td1);
         builder.addExternal(prop2, td2);
         // manually force the index list
         Field idxField =
ExternalTypeHandler.Builder.class.getDeclaredField("_nameToPropertyIndex");
         idxField.setAccessible(true);
         @SuppressWarnings("unchecked")
         Map<String, Object> idxMap = (Map<String, Object>) idxField.get(builder);
         List<Integer> idxList = new ArrayList<>();
         idxList.add(0);
         idxList.add(1);
         idxMap.put("sharedProp", idxList);

         ExternalTypeHandler handler = builder.build(null).start();
         Object bean = new HashMap<>();

         when(parser.getText()).thenReturn("ignored");
         when(parser.nextToken()).thenReturn(JsonToken.VALUE_STRING, (JsonToken) null);
         when(parser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
         doNothing().when(parser).copyCurrentStructure(any(JsonParser.class));

         boolean handled = handler.handlePropertyValue(parser, ctxt, "sharedProp", bean);
         assertTrue(handled);
         // both indices should have the same token buffer
         TokenBuffer[] tokens = getField(handler, "_tokens", TokenBuffer[].class);
         assertNotNull(tokens[0]);
         assertNotNull(tokens[1]);
         assertSame(tokens[0], tokens[1]);
     }

     // ---------- complete() tests ----------

     @Test
     public void testCompleteBothTypeIdAndTokenPresentSetsValue() throws Exception {
         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("animal");
         when(prop.isRequired()).thenReturn(false);
         JavaType propType = mock(JavaType.class);
         when(prop.getType()).thenReturn(propType);

         TypeDeserializer td = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop, td);
         ExternalTypeHandler handler = builder.build(null).start();

         // manually inject typeId and token
         injectField(handler, "_typeIds", new String[] { "Dog" });
         final TokenBuffer tb = new TokenBuffer(null, null, false); // safe for test
         tb.writeStartArray();
         tb.writeEndArray();
         injectField(handler, "_tokens", new TokenBuffer[] { tb });

         // mock deserializeAndSet
         doAnswer(new Answer<Void>() {
             @Override
             public Void answer(InvocationOnMock invocation) {
                 Object b = invocation.getArgumentAt(2, Object.class);
                 prop.set(b, "deserialized");
                 return null;
             }
         }).when(prop).deserializeAndSet(any(JsonParser.class), any(DeserializationContext.class),
any(Object.class));

         Object bean = "bean";
         handler.complete(parser, ctxt, bean);

         verify(prop).deserializeAndSet(any(JsonParser.class), eq(ctxt), eq(bean));
         verify(prop).set(bean, "deserialized");
     }

     @Test
     public void testCompleteMissingTypeIdWithDefaultImplSetsDefault() throws Exception {
         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("animal");
         when(prop.isRequired()).thenReturn(false);
         JavaType propType = mock(JavaType.class);
         when(prop.getType()).thenReturn(propType);

         // type deserializer with a default impl
         TypeDeserializer td = mock(TypeDeserializer.class);
         when(td.getPropertyName()).thenReturn("type");
         when(td.getDefaultImpl()).thenReturn(Object.class); // has default

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop, td);
         ExternalTypeHandler handler = builder.build(null).start();

         // typeId null, token present
         TokenBuffer tb = new TokenBuffer(null, null, false);
         tb.writeStartArray();
         tb.writeEndArray();
         injectField(handler, "_typeIds", new String[] { null });
         injectField(handler, "_tokens", new TokenBuffer[] { tb });

         doAnswer(new Answer<Void>() {
             @Override
             public Void answer(InvocationOnMock invocation) {
                 Object b = invocation.getArgumentAt(2, Object.class);
                 prop.set(b, "defaultDeserialized");
                 return null;
             }
         }).when(prop).deserializeAndSet(any(JsonParser.class), any(DeserializationContext.class),
any(Object.class));

         Object bean = "bean";
         handler.complete(parser, ctxt, bean);

         verify(prop).deserializeAndSet(any(JsonParser.class), eq(ctxt), eq(bean));
     }

     @Test
     public void testCompleteMissingTypeIdNoDefaultThrowsException() throws Exception {
         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("animal");
         when(prop.isRequired()).thenReturn(false);
         JavaType propType = mock(JavaType.class);
         when(prop.getType()).thenReturn(propType);

         TypeDeserializer td = createMockTypeDeserializer("type", null); // no default

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop, td);
         ExternalTypeHandler handler = builder.build(null).start();

         TokenBuffer tb = new TokenBuffer(null, null, false);
         tb.writeStartArray();
         tb.writeEndArray();
         injectField(handler, "_typeIds", new String[] { null });
         injectField(handler, "_tokens", new TokenBuffer[] { tb });

         when(ctxt.reportInputMismatch(any(Class.class), anyString(), anyString()))
                 .thenThrow(new JsonMappingException(ctxt, "Missing external type id"));
         Object bean = "bean";
         try {
             handler.complete(parser, ctxt, bean);
             fail("Should throw JsonMappingException when type id missing and no default");
         } catch (JsonMappingException e) {
             // expected
         }
     }

     @Test
     public void testCompleteMissingTokenWithRequiredPropertyThrows() throws Exception {
         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("animal");
         when(prop.isRequired()).thenReturn(true); // required

         TypeDeserializer td = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop, td);
         ExternalTypeHandler handler = builder.build(null).start();

         injectField(handler, "_typeIds", new String[] { "Dog" });
         injectField(handler, "_tokens", new TokenBuffer[] { null });

         when(ctxt.reportInputMismatch(any(Class.class), anyString(), anyString()))
                 .thenThrow(new JsonMappingException(ctxt, "Missing property"));
         try {
             handler.complete(parser, ctxt, "bean");
             fail("Should throw for missing required property");
         } catch (JsonMappingException e) {
             // expected
         }
     }

     @Test
     public void testCompleteNaturalTypeWithScalarToken() throws Exception {
         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("animal");
         when(prop.isRequired()).thenReturn(false);
         JavaType propType = mock(JavaType.class);
         when(prop.getType()).thenReturn(propType);

         TypeDeserializer td = mock(TypeDeserializer.class);
         when(td.getPropertyName()).thenReturn("type");
         when(td.getDefaultImpl()).thenReturn(null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop, td);
         ExternalTypeHandler handler = builder.build(null).start();

         // create a TokenBuffer with a scalar value (VALUE_STRING "natural")
         TokenBuffer tb = new TokenBuffer(null, null, false);
         tb.writeString("naturalValue");
         injectField(handler, "_typeIds", new String[] { null });
         injectField(handler, "_tokens", new TokenBuffer[] { tb });

         // mock static TypeDeserializer.deserializeIfNatural to return some object
         PowerMockito.spy(TypeDeserializer.class);
         Object naturalResult = new Object();
         PowerMockito.when(TypeDeserializer.deserializeIfNatural(any(JsonParser.class),
                 any(DeserializationContext.class), any(JavaType.class))).thenReturn(naturalResult);

         Object bean = "bean";
         handler.complete(parser, ctxt, bean);

         // prop.set should be called with the natural result
         verify(prop).set(bean, naturalResult);
     }

     @Test
     public void testStartCreatesCopy() throws Exception {
         SettableBeanProperty prop = createMockProperty("animal");
         TypeDeserializer td = createMockTypeDeserializer("type", null);

         ExternalTypeHandler.Builder builder = ExternalTypeHandler.builder(beanType);
         builder.addExternal(prop, td);
         ExternalTypeHandler base = builder.build(null);
         ExternalTypeHandler copy = base.start();

         assertNotSame(base, copy);
         // copy should have null typeIds and null tokens
         String[] copyTypeIds = getField(copy, "_typeIds", String[].class);
         assertNotNull(copyTypeIds);
         for (String s : copyTypeIds) {
             assertNull(s);
         }
         TokenBuffer[] copyTokens = getField(copy, "_tokens", TokenBuffer[].class);
         assertNotNull(copyTokens);
         for (TokenBuffer t : copyTokens) {
             assertNull(t);
         }
         // but properties array should be the same length
         ExtTypedProperty[] baseProps = getExtTypedProperties(base);
         ExtTypedProperty[] copyProps = getExtTypedProperties(copy);
         assertEquals(baseProps.length, copyProps.length);
     }

     // ---------- helpers ----------

     private SettableBeanProperty createMockProperty(String name) {
         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn(name);
         when(prop.isRequired()).thenReturn(false);
         JavaType type = mock(JavaType.class);
         when(prop.getType()).thenReturn(type);
         return prop;
     }

     private TypeDeserializer createMockTypeDeserializer(String propertyName, Class<?> defaultImpl)
{
         TypeDeserializer td = mock(TypeDeserializer.class);
         when(td.getPropertyName()).thenReturn(propertyName);
         when(td.getDefaultImpl()).thenReturn(defaultImpl);
         return td;
     }

     @SuppressWarnings("unchecked")
     private ExtTypedProperty[] getExtTypedProperties(ExternalTypeHandler handler) throws Exception
{
         return getField(handler, "_properties", ExtTypedProperty[].class);
     }

     private <T> T getField(Object target, String fieldName, Class<T> fieldType) throws Exception {
         Field f = ExternalTypeHandler.class.getDeclaredField(fieldName);
         f.setAccessible(true);
         return fieldType.cast(f.get(target));
     }

     private void injectField(Object target, String fieldName, Object value) throws Exception {
         Field f = ExternalTypeHandler.class.getDeclaredField(fieldName);
         f.setAccessible(true);
         f.set(target, value);
     }
 }
