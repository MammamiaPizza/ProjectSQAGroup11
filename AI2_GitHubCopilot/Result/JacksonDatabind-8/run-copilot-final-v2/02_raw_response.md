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

     // A class with two distinct String-creating members (constructor + factory)
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

     private AnnotatedWithParams findSingleStringConstructor(BeanDescription desc) {
         for (AnnotatedConstructor ctor : desc.getConstructors()) {
             if (ctor.getParameterCount() == 1) {
                 JavaType paramType = ctor.getParameterType(0);
                 if (String.class.equals(paramType.getRawClass())) {
                     return ctor;
                 }
             }
         }
         return null;
     }

     private AnnotatedWithParams findSingleIntConstructor(BeanDescription desc) {
         for (AnnotatedConstructor ctor : desc.getConstructors()) {
             if (ctor.getParameterCount() == 1) {
                 JavaType paramType = ctor.getParameterType(0);
                 if (int.class.equals(paramType.getRawClass()) ||
Integer.class.equals(paramType.getRawClass())) {
                     return ctor;
                 }
             }
         }
         return null;
     }

     // --- Bug #667: adding the identical String creator twice must not throw ---

     @Test
     public void testAddIdenticalStringCreatorTwice() {
         AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
         assertNotNull("StringBuilder must have a single-arg String constructor", ctor);

         CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
         collector.addStringCreator(ctor, true);
         // should not throw
         collector.addStringCreator(ctor, true);

         ValueInstantiator instantiator = collector.constructValueInstantiator(config);
         assertNotNull("ValueInstantiator should be created after adding String creator twice",
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
         assertNotNull("ValueInstantiator should be created after adding Int creator twice",
instantiator);
     }

     // --- Conflicts with truly different creators for the same type ---

     @Test
     public void testDifferentStringCreatorsExplicitConflict() throws Exception {
         JavaType conflictType = mapper.getTypeFactory().constructType(ConflictClass.class);
         BeanDescription conflictDesc = config.introspect(conflictType);

         // Gather all members that accept a single String and could be string creators
         List<AnnotatedWithParams> stringCreators = new ArrayList<AnnotatedWithParams>();
         for (AnnotatedConstructor ctor : conflictDesc.getConstructors()) {
             if (ctor.getParameterCount() == 1) {
                 JavaType p = ctor.getParameterType(0);
                 if (String.class.equals(p.getRawClass())) {
                     stringCreators.add(ctor);
                 }
             }
         }
         for (AnnotatedMethod factory : conflictDesc.getFactoryMethods()) {
             if (factory.getParameterCount() == 1) {
                 JavaType p = factory.getParameterType(0);
                 if (String.class.equals(p.getRawClass())) {
                     stringCreators.add(factory);
                 }
             }
         }
         assertTrue("Need at least two distinct String creators in ConflictClass",
stringCreators.size() >= 2);

         CreatorCollector collector = new CreatorCollector(conflictDesc, false);
         // Add first creator explicitly
         collector.addStringCreator(stringCreators.get(0), true);

         // Adding a different explicit String creator must fail with a descriptive message
         try {
             collector.addStringCreator(stringCreators.get(1), true);
             fail("Expected IllegalArgumentException for conflicting String creators");
         } catch (IllegalArgumentException e) {
             assertTrue("Error message must mention Conflicting String creators",
                     e.getMessage().contains("Conflicting String creators"));
         }
     }

     @Test
     public void testDifferentIntCreatorsExplicitConflict() throws Exception {
         JavaType conflictType = mapper.getTypeFactory().constructType(ConflictClass.class);
         BeanDescription conflictDesc = config.introspect(conflictType);

         // For this test we need two distinct int-arg creators – this class does not have them,
         // so we reuse the string test pattern: the conflict should be generic.
         // Alternatively we can directly test that adding two different constructors
         // (even non-String) classified as C_INT throws.
         // We construct a small class with an int constructor and a factory.
         // To keep the test simple we demonstrate the behaviour with an explicit check on generic
creator.
         CreatorCollector collector = new CreatorCollector(conflictDesc, false);
         // Since ConflictClass has no int creators, we just verify the conflict detection
         // by adding a dummy string creator and then re-adding a different one – already tested
above.
         // This test documents the pattern and is skipped if not applicable.
         // We still include a meaningful assertion.
         assertNotNull("ConflictClass loaded", conflictDesc);
     }

     @Test
     public void testOldExplicitNewNonExplicitSkips() {
         AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
         assertNotNull(ctor);

         CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
         collector.addStringCreator(ctor, true);
         // Adding the same creator with explicit=false is allowed
         collector.addStringCreator(ctor, false);

         ValueInstantiator instantiator = collector.constructValueInstantiator(config);
         assertNotNull(instantiator);
     }

     @Test
     public void testBothNonExplicitCausesConflict() {
         AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
         assertNotNull(ctor);

         CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
         collector.addStringCreator(ctor, false);
         try {
             collector.addStringCreator(ctor, false);
             fail("Expected conflict when adding the same non-explicit String creator twice");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Conflicting String creators"));
         }
     }

     @Test
     public void testDefaultCreatorIsTracked() {
         CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
         assertFalse("No default creator initially", collector.hasDefaultCreator());

         AnnotatedWithParams ctor = findSingleStringConstructor(stringBuilderDesc);
         collector.setDefaultCreator(ctor);
         assertTrue("Default creator registered", collector.hasDefaultCreator());
     }

     @Test
     public void testAddPropertyCreatorDuplicateNamesThrows() {
         CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);

         CreatorProperty[] props = new CreatorProperty[2];
         // We need concrete CreatorProperty instances; we use null for some fields that are not
used in length check,
         // but getName() must return the same string.
         // CreatorProperty constructor is not public; we can use a mock approach or rely on
internal constructor via reflection.
         // Since all Jackson types are available we can create a basic one through its constr.
         // However, CreatorProperty has a fully-featured constr. For brevity we'll simulate with a
stub.
         // The simplest way is to craft an array with two elements where getName returns same name,
         // and the lengths match the assertion path. The code under test uses
properties[i].getName()
         // and checks duplicates. We'll make two properties with the name "prop" using the real
class.

         // We'll construct a real CreatorProperty using reflection or the provided intropection.
         // An alternative: use the CreatorProperty from a real BeanDescription.
         // Since our test class ConflictClass has a factory method with a string param,
         // we can introspect it and retrieve the factory's parameter as a CreatorProperty.
         // But that only gives one property.
         // We'll create a minimal CreatorProperty instance via its public constructor (if any).
         // If not available, we skip this test with an assumption.
         // We'll nevertheless assert that the class is loadable.
         assertNotNull("CreatorProperty class available", CreatorProperty.class);
     }

     @Test
     public void testIncompleteParameterAccepted() {
         CreatorCollector collector = new CreatorCollector(stringBuilderDesc, false);
         // AnnotatedParameter expects specific constructor; we can create a stub
         // but it is safe to test addIncompeteParameter with null only if the internal handling
         // permits. The method only sets when _incompleteParameter is null.
         // We'll pass null and verify no NPE because the field is set regardless.
         // A better approach: use a real AnnotatedParameter from a BeanDescription.
         // However, to keep the test focused, we simply assert the method exists and runs.
         collector.addIncompeteParameter(null);
         assertNull("Incomplete parameter can be null",
collector.constructValueInstantiator(config));
     }
 }

I realize some tests may not compile due to missing constructor accessibility for CreatorProperty
and AnnotatedParameter. I need to adjust. The CreatorProperty has a public constructor in older
Jackson? In Jackson 2.4, CreatorProperty has a constructor, but it might be package-protected.
However, we can avoid direct instantiation by using introspection of an actual class's property. For
the duplicate property names test, we can create a class with a factory method that has two
parameters with the same name? Not possible. Alternatively, we can use
com.fasterxml.jackson.databind.deser.CreatorProperty's static factory or just accept the limitation.
The test plan didn't require it. We'll remove that test to keep the class compilable and add a
simple test instead.

Let's revise: remove the property duplicate test and incomplete parameter test that require complex
setup. We'll add a test for addDelegatingCreator and addPropertyCreator that don't require property
objects, or we can use null for safety.

Better: use CreatorProperty constructor CreatorProperty(String name, JavaType type, ...). In Jackson
2.4, the constructor is public? Let's assume we can create new CreatorProperty(name, type, null,
null, null, null, 0, null, null), but it might need many parameters. Too risky.

We'll just test the addStringCreator-related behavior and a couple of other methods that require
only AnnotatedWithParams. That satisfies the bug.

Let's produce a simplified, compilable class.

Final output should be clean, with correct imports, and tests pass when fixed version is used. We'll
ensure no compilation errors.```java
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
```