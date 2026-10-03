package com.fasterxml.jackson.databind.deser.impl;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.io.IOException;
 import java.util.LinkedHashMap;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.cfg.MapperConfig;
 import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
 import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
 import com.fasterxml.jackson.databind.type.SimpleType;

 /**
  * Tests for the property-based creator handling in {@link CreatorCollector}
  * focusing on the bug where an implicit creator overwrites
  * the property array of an existing explicit creator, causing
  * "Could not find creator property with name '...' " errors.
  */
 public class CreatorCollectorBug1476Test {

     private CreatorCollector collector;
     private MapperConfig<?> mapperConfig;
     private BeanDescription beanDesc;
     private DeserializationConfig deserConfig;
     private DeserializationContext ctxt;

     @Before
     public void setUp() {
         mapperConfig = mock(MapperConfig.class);
         when(mapperConfig.canOverrideAccessModifiers()).thenReturn(true);

when(mapperConfig.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)).thenReturn(false);

         beanDesc = mock(BeanDescription.class);
         when(beanDesc.getType()).thenReturn(SimpleType.constructUnsafe(Object.class));

         collector = new CreatorCollector(beanDesc, mapperConfig);

         deserConfig = mock(DeserializationConfig.class);
         ctxt = mock(DeserializationContext.class);
     }

     @Test
     public void explicitCreatorPropertiesShouldNotBeOverwrittenByImplicit()
             throws IOException {

         // Explicit property-based creator with parameter "intField"
         AnnotatedWithParams explicitCreator = mock(AnnotatedWithParams.class);
         when(explicitCreator.getParameterCount()).thenReturn(1);
         when(explicitCreator.getRawParameterType(0)).thenReturn((Class) int.class);
         when(explicitCreator.getParameterType(0))
                 .thenReturn(SimpleType.constructUnsafe(int.class));
         // Make sure the creator can be invoked (via the instantiator)
         when(explicitCreator.call(any(Object[].class))).thenReturn(new Object());

         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("intField");

         collector.addPropertyCreator(explicitCreator, true, new SettableBeanProperty[] { prop });

         assertTrue("Explicit property-based creator should be registered",
                 collector.hasPropertyBasedCreator());

         // Implicit property-based creator with no properties (e.g., default no-arg)
         AnnotatedWithParams implicitCreator = mock(AnnotatedWithParams.class);
         when(implicitCreator.getParameterCount()).thenReturn(0);
         when(implicitCreator.call(any(Object[].class))).thenReturn(new Object());

         collector.addPropertyCreator(implicitCreator, false, new SettableBeanProperty[0]);

         // The explicit creator should still be the active one
         assertTrue("Explicit creator should remain after adding an implicit one",
                 collector.hasPropertyBasedCreator());

         // Build the instantiator and try to instantiate with the argument
         // In the buggy version this throws JsonMappingException: "Could not find creator property
..."
         ValueInstantiator inst = collector.constructValueInstantiator(deserConfig);
         try {
             Object result = inst.createFromObjectWith(ctxt, new Object[] { 42 });
             assertNotNull("Object should be created successfully", result);
         } catch (JsonMappingException e) {
             if (e.getMessage().contains("Could not find creator property")) {
                 fail("Bug reproduced: implicit creator incorrectly overwrote "
                         + "the explicit creator's properties");
             }
             throw e; // unexpected exception type
         }
     }

     @Test
     public void duplicatePropertyNamesShouldBeRejected() {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         when(creator.getParameterCount()).thenReturn(2);
         when(creator.getRawParameterType(0)).thenReturn((Class) String.class);
         when(creator.getRawParameterType(1)).thenReturn((Class) String.class);
         when(creator.getParameterType(0)).thenReturn(SimpleType.constructUnsafe(String.class));
         when(creator.getParameterType(1)).thenReturn(SimpleType.constructUnsafe(String.class));

         SettableBeanProperty p1 = mock(SettableBeanProperty.class);
         when(p1.getName()).thenReturn("field");
         SettableBeanProperty p2 = mock(SettableBeanProperty.class);
         when(p2.getName()).thenReturn("field"); // duplicate

         try {
             collector.addPropertyCreator(creator, false, new SettableBeanProperty[] { p1, p2 });
             fail("Duplicate property names should be rejected");
         } catch (IllegalArgumentException expected) {
             assertTrue(expected.getMessage().contains("Duplicate creator property"));
         }
     }

     @Test
     public void mixedExplicitImplicitMaintainsCorrectCreator() {
         // Explicit first
         AnnotatedWithParams explicitCreator = mock(AnnotatedWithParams.class);
         when(explicitCreator.getParameterCount()).thenReturn(1);
         when(explicitCreator.getRawParameterType(0)).thenReturn((Class) String.class);

when(explicitCreator.getParameterType(0)).thenReturn(SimpleType.constructUnsafe(String.class));
         when(explicitCreator.call(any(Object[].class))).thenReturn(new Object());

         SettableBeanProperty eProp = mock(SettableBeanProperty.class);
         when(eProp.getName()).thenReturn("name");
         collector.addPropertyCreator(explicitCreator, true, new SettableBeanProperty[] { eProp });

         // Implicit different creator
         AnnotatedWithParams implicitCreator = mock(AnnotatedWithParams.class);
         when(implicitCreator.getParameterCount()).thenReturn(1);
         when(implicitCreator.getRawParameterType(0)).thenReturn((Class) int.class);

when(implicitCreator.getParameterType(0)).thenReturn(SimpleType.constructUnsafe(int.class));
         when(implicitCreator.call(any(Object[].class))).thenReturn(new Object());

         SettableBeanProperty iProp = mock(SettableBeanProperty.class);
         when(iProp.getName()).thenReturn("value");
         collector.addPropertyCreator(implicitCreator, false, new SettableBeanProperty[] { iProp });

         // Verify the explicit one is still in place
         assertTrue(collector.hasPropertyBasedCreator());
         ValueInstantiator inst = collector.constructValueInstantiator(deserConfig);
         // Instantiation using explicit creator's parameters should succeed
         try {
             inst.createFromObjectWith(ctxt, new Object[] { "test" });
         } catch (JsonMappingException e) {
             if (e.getMessage().contains("Could not find creator property")) {
                 fail("Explicit creator properties should not be lost");
             }
             throw new AssertionError("Unexpected exception", e);
         }
     }

     @Test
     public void onlyPropertyCreatorShouldWork() throws IOException {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         when(creator.getParameterCount()).thenReturn(1);
         when(creator.getRawParameterType(0)).thenReturn((Class) String.class);
         when(creator.getParameterType(0)).thenReturn(SimpleType.constructUnsafe(String.class));
         when(creator.call(any(Object[].class))).thenReturn(new Object());

         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("data");
         collector.addPropertyCreator(creator, false, new SettableBeanProperty[] { prop });

         assertTrue(collector.hasPropertyBasedCreator());
         ValueInstantiator inst = collector.constructValueInstantiator(deserConfig);
         assertNotNull(inst.createFromObjectWith(ctxt, new Object[] { "test" }));
     }

     @Test
     public void defaultAndPropertyCreatorMix() throws IOException {
         // Set a default creator
         AnnotatedWithParams defaultCreator = mock(AnnotatedWithParams.class);
         collector.setDefaultCreator(defaultCreator);

         // Add property creator
         AnnotatedWithParams propCreator = mock(AnnotatedWithParams.class);
         when(propCreator.getParameterCount()).thenReturn(1);
         when(propCreator.getRawParameterType(0)).thenReturn((Class) int.class);
         when(propCreator.getParameterType(0)).thenReturn(SimpleType.constructUnsafe(int.class));
         when(propCreator.call(any(Object[].class))).thenReturn(new Object());

         SettableBeanProperty prop = mock(SettableBeanProperty.class);
         when(prop.getName()).thenReturn("id");
         collector.addPropertyCreator(propCreator, false, new SettableBeanProperty[] { prop });

         assertTrue(collector.hasDefaultCreator());
         assertTrue(collector.hasPropertyBasedCreator());

         ValueInstantiator inst = collector.constructValueInstantiator(deserConfig);
         assertNotNull(inst.createFromObjectWith(ctxt, new Object[] { 123 }));
     }

     @Test
     public void hasPropertyBasedCreatorAfterAdd() {
         assertFalse("Initially no property creator", collector.hasPropertyBasedCreator());

         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         when(creator.getParameterCount()).thenReturn(0);
         when(creator.getRawParameterType(0)).thenReturn((Class) Object.class);
         when(creator.getParameterType(0)).thenReturn(SimpleType.constructUnsafe(Object.class));
         // Even with empty properties, creator should be registered
         collector.addPropertyCreator(creator, false, new SettableBeanProperty[0]);
         assertTrue(collector.hasPropertyBasedCreator());
     }

     @Test
     public void constructValueInstantiatorForVanillaCollection() {
         when(beanDesc.getType()).thenReturn(SimpleType.constructUnsafe(java.util.ArrayList.class));
         // No creators added → should return a Vanilla instance
         ValueInstantiator inst = collector.constructValueInstantiator(deserConfig);
         assertNotNull(inst);
         assertTrue(inst.canCreateUsingDefault());
     }

     @Test
     public void constructValueInstantiatorForVanillaMap() {
         when(beanDesc.getType()).thenReturn(SimpleType.constructUnsafe(LinkedHashMap.class));
         ValueInstantiator inst = collector.constructValueInstantiator(deserConfig);
         assertNotNull(inst);
         assertTrue(inst.canCreateUsingDefault());
     }
 }
