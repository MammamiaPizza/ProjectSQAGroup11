package com.fasterxml.jackson.databind.jsontype.impl;

 import static org.junit.Assert.*;
 import org.junit.Before;
 import org.junit.Test;
 import java.util.Collection;
 import java.util.LinkedHashSet;
 import com.fasterxml.jackson.databind.jsontype.NamedType;

 public class StdSubtypeResolverTest {

     private StdSubtypeResolver resolver;

     @Before
     public void setUp() {
         resolver = new StdSubtypeResolver();
     }

     // Fault-related: Null array triggers NPE in for-each in buggy version
     @Test
     public void testRegisterSubtypesNullArray() {
         try {
             resolver.registerSubtypes((NamedType[]) null);
             fail("Expected NullPointerException");
         } catch (NullPointerException e) {
             // buggy version: for (NamedType t : types) with types=null -> NPE
             assertNull(resolver._registeredSubtypes);
         }
     }

     // Fault-related: Null varargs call (zero args, implicit null)
     @Test
     public void testRegisterSubtypesNoArgs() {
         try {
             resolver.registerSubtypes();
             // If no args passes, _registeredSubtypes may or may not be initialized
             // Buggy version: no-op skips init, _registeredSubtypes stays null
             assertNull(resolver._registeredSubtypes);
         } catch (NullPointerException e) {
             fail("registerSubtypes() should not throw NPE");
         }
     }

     // Boundary: empty array
     @Test
     public void testRegisterSubtypesEmpty() {
         resolver.registerSubtypes(new NamedType[0]);
         assertNotNull(resolver._registeredSubtypes);
         assertTrue(resolver._registeredSubtypes.isEmpty());
     }

     // Normal: valid array
     @Test
     public void testRegisterSubtypesValid() {
         resolver.registerSubtypes(
             new NamedType(String.class),
             new NamedType(Integer.class)
         );
         assertNotNull(resolver._registeredSubtypes);
         assertEquals(2, resolver._registeredSubtypes.size());
     }

     // Boundary: array with null entries
     @Test
     public void testRegisterSubtypesNullEntries() {
         NamedType[] types = new NamedType[] { new NamedType(String.class), null };
         resolver.registerSubtypes(types);
         assertNotNull(resolver._registeredSubtypes);
         assertEquals(2, resolver._registeredSubtypes.size());
     }

     // Fault-related: null Class[] causes NPE on .length in buggy version
     @Test
     public void testRegisterClassesNullArray() {
         try {
             resolver.registerSubtypes((Class<?>[]) null);
             fail("Expected NullPointerException");
         } catch (NullPointerException e) {
             // buggy version: classes.length throws NPE
         }
     }

     // Normal: register via Class varargs
     @Test
     public void testRegisterClassesValid() {
         resolver.registerSubtypes(String.class, Integer.class);
         assertNotNull(resolver._registeredSubtypes);
         assertEquals(2, resolver._registeredSubtypes.size());
     }

     // Boundary: register duplicate subtypes
     @Test
     public void testRegisterSubtypesDuplicate() {
         resolver.registerSubtypes(new NamedType(String.class));
         resolver.registerSubtypes(new NamedType(String.class));
         assertEquals(1, resolver._registeredSubtypes.size());
     }

     // Normal: verify subtype iteration does not NPE after registration
     @Test
     public void testIterateAfterRegistration() {
         resolver.registerSubtypes(new NamedType(String.class));
         int count = 0;
         for (NamedType nt : resolver._registeredSubtypes) {
             assertNotNull(nt);
             count++;
         }
         assertEquals(1, count);
     }

     // Boundary: register named and unnamed subtypes
     @Test
     public void testRegisterNamedAndUnnamed() {
         resolver.registerSubtypes(
             new NamedType(String.class, "string"),
             new NamedType(Integer.class)
         );
         assertEquals(2, resolver._registeredSubtypes.size());
     }

     // Fault-related: verify _registeredSubtypes starts null
     @Test
     public void testInitialRegisteredSubtypesNull() {
         assertNull(resolver._registeredSubtypes);
     }

     // Boundary: large registration
     @Test
     public void testRegisterSubtypesLarge() {
         NamedType[] types = new NamedType[100];
         for (int i = 0; i < 100; i++) {
             types[i] = new NamedType(String.class, "name" + i);
         }
         resolver.registerSubtypes(types);
         assertEquals(100, resolver._registeredSubtypes.size());
     }
 }