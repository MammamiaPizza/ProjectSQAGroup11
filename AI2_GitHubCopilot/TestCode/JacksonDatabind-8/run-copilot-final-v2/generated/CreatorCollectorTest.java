package com.fasterxml.jackson.databind.deser.impl;

import static org.junit.Assert.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class CreatorCollectorTest {

 private ObjectMapper mapper;
 private DeserializationConfig config;
 private JavaType stringBuilderType;
 private BeanDescription stringBuilderDesc;

 /**
  * Helper class that provides two distinct {@link AnnotatedWithParams} candidates
  * for a String creator: a constructor and a static factory method with a String parameter.
  */
 public static class ConflictClass {
     public ConflictClass(String s) {
     }

     @JsonCreator
     public static ConflictClass fromString(String s) {
         return null;
     }
 }

 @Before
 public void setUp() throws Exception {
     mapper = new ObjectMapper();
     config = mapper.getDeserializationConfig();
     stringBuilderType = mapper.getTypeFactory().constructType(StringBuilder.class);
     stringBuilderDesc = config.introspect(stringBuilderType);
 }

 // ---------- utility methods ----------

 private AnnotatedWithParams findSingleStringConstructor(BeanDescription desc) {
     for (AnnotatedConstructor ctor : desc.getConstructors()) {
         if (ctor.getParameterCount() == 1) {
             JavaType param = ctor.getParameterType(0);
             if (String.class == param.getRawClass()) {
                 return ctor;
             }
         }
     }
     return null;
 }

 private AnnotatedWithParams findSingleIntConstructor(BeanDescription desc) {
     for (AnnotatedConstructor ctor : desc.getConstructors()) {
         if (ctor.getParameterCount() == 1) {
             JavaType param = ctor.getParameterType(0);
             if (int.class == param.getRawClass() || Integer.class == param.getRawClass()) {
                 return ctor;
             }
         }
     }
     return null;
 }

 private List<AnnotatedWithParams> findAllStringCreators(BeanDescription desc) {
     List<AnnotatedWithParams> result = new ArrayList<AnnotatedWithParams>();
     for (AnnotatedConstructor ctor : desc.getConstructors()) {
         if (ctor.getParameterCount() == 1) {
             JavaType param = ctor.getParameterType(0);
             if (String.class == param.getRawClass()) {
                 result.add(ctor);
             }
         }
     }
     for (AnnotatedMethod factory : desc.getFactoryMethods()) {
         if (factory.getParameterCount() == 1) {
             JavaType param = factory.getParameterType(0);
             if (String.class == param.getRawClass()) {
                 result.add(factory);
             }
         }
     }
     return result;
 }

 // ---------- bug #667: duplicate identical creators ----------

 @Test
 public void testAddIdenticalStringCreatorTwice() {
     AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
     assertNotNull("StringBuilder must have a single-arg String constructor", ctor);

     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     collector.addStringCreator(ctor, true);
     // second call with exactly the same creator: must not throw
     collector.addStringCreator(ctor, true);

     ValueInstantiator instantiator = collector.constructValueInstantiator(config);
     assertNotNull("ValueInstantiator must be produced after adding the same String creator twice",
                   instantiator);
 }

 @Test
 public void testAddIdenticalIntCreatorTwice() {
     AnnotatedWithParams ctor = findSingleIntConstructor(stringBuilderDesc);
     assertNotNull("StringBuilder must have a single-arg int constructor", ctor);

     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     collector.addIntCreator(ctor, true);
     collector.addIntCreator(ctor, true);

     ValueInstantiator instantiator = collector.constructValueInstantiator(config);
     assertNotNull("ValueInstantiator must be produced after adding the same Int creator twice",
                   instantiator);
 }

 // ---------- genuine conflict: two *different* creators for the same type ----------

 @Test
 public void testDifferentStringCreatorsCauseConflict() throws Exception {
     JavaType conflictType = mapper.getTypeFactory().constructType(ConflictClass.class);
     BeanDescription desc = config.introspect(conflictType);
     List<AnnotatedWithParams> creators = findAllStringCreators(desc);
     assertTrue("Need at least two distinct String creator candidates in ConflictClass",
                creators.size() >= 2);

     CreatorCollector collector = new CreatorCollector(desc, false);
     collector.addStringCreator(creators.get(0), true);

     try {
         collector.addStringCreator(creators.get(1), true);
         fail("Expected IllegalArgumentException for conflicting String creators");
     } catch (IllegalArgumentException e) {
         assertTrue("Error message must contain 'Conflicting String creators'",
                    e.getMessage().contains("Conflicting String creators"));
     }
 }

 // ---------- additional explicit/non-explicit interactions ----------

 @Test
 public void testOldExplicitNewNonExplicitIsIgnored() {
     AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
     assertNotNull(ctor);

     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     collector.addStringCreator(ctor, true);
     // adding the same creator again, but non-explicit, must be silently ignored
     collector.addStringCreator(ctor, false);

     ValueInstantiator instantiator = collector.constructValueInstantiator(config);
     assertNotNull(instantiator);
 }

 @Test
 public void testBothNonExplicitTriggersConflict() {
     AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
     assertNotNull(ctor);

     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     collector.addStringCreator(ctor, false);
     try {
         collector.addStringCreator(ctor, false);
         fail("Adding the same non-explicit String creator twice should conflict");
     } catch (IllegalArgumentException e) {
         assertTrue(e.getMessage().contains("Conflicting String creators"));
     }
 }

 // ---------- general creator-collector behavior ----------

 @Test
 public void testDefaultCreatorTracking() {
     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     assertFalse("No default creator initially", collector.hasDefaultCreator());

     AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
     collector.setDefaultCreator(ctor);
     assertTrue("Default creator registered as expected", collector.hasDefaultCreator());
 }

 @Test
 public void testAddDelegatingCreatorAndConstructInstantiator() {
     AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
     assertNotNull(ctor);

     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     collector.addDelegatingCreator(ctor, true, null);

     ValueInstantiator instantiator = collector.constructValueInstantiator(config);
     assertNotNull("Delegating creator should produce a valid instantiator", instantiator);
 }

 @Test
 public void testAddPropertyCreatorWithNullProperties() {
     AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
     assertNotNull(ctor);

     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     // null property array is accepted (indicates no property-based creation)
     collector.addPropertyCreator(ctor, true, null);

     ValueInstantiator instantiator = collector.constructValueInstantiator(config);
     assertNotNull("Instantiator should be created when property-based creator is added with null
props",
                   instantiator);
 }

 @Test
 public void testConstructValueInstantiatorWithNoCreatorsReturnsVanilla() {
     CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
     ValueInstantiator instantiator = collector.constructValueInstantiator(config);
     assertNotNull("Even without any creators, a vanilla instantiator should be returned",
                   instantiator);
     assertTrue("Must be able to instantiate default value", instantiator.canInstantiate());
     assertTrue("Must be able to create using default", instantiator.canCreateUsingDefault());
 }

}
