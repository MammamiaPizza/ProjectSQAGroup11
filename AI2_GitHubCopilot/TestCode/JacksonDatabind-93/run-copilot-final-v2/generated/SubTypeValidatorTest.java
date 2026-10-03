package com.fasterxml.jackson.databind.jsontype.impl;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.powermock.api.mockito.PowerMockito;
 import org.powermock.core.classloader.annotations.PrepareForTest;
 import org.powermock.modules.junit4.PowerMockRunner;

 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.JsonMappingException;

 @RunWith(PowerMockRunner.class)
 @PrepareForTest({SubTypeValidator.class})
 public class SubTypeValidatorTest {

     // A validator that allows customisation of illegal class names for testing
     private static class TestValidator extends SubTypeValidator {
         TestValidator(Set<String> illegalNames) {
             this._cfgIllegalClassNames = illegalNames;
         }
     }

     // Helper to create a mock JavaType returning the given raw class
     private JavaType javaTypeFor(final Class<?> raw) {
         JavaType type = mock(JavaType.class);
         when(type.getRawClass()).thenReturn(raw);
         return type;
     }

     // Helper for mocked raw Class used in spring-prefix tests
     private Class<?> springInterfaceMock() {
         Class<?> raw = PowerMockito.mock(Class.class);
         when(raw.getName()).thenReturn("org.springframework.aop.Advisor");
         when(raw.getSuperclass()).thenReturn(null);             // interface
         when(raw.getSimpleName()).thenReturn("Advisor");
         return raw;
     }

     // ---------------------------------------------------------------------
     // Tests
     // ---------------------------------------------------------------------

     @Test
     public void testNonDangerousTypeReturnsNormally() throws Exception {
         JavaType type = javaTypeFor(String.class);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         SubTypeValidator.instance().validateSubType(ctxt, type);
         // no exception expected
     }

     @Test(expected = JsonMappingException.class)
     public void testIllegalTypeThrowsException() throws Exception {
         Set<String> illegal = new HashSet<String>();
         illegal.add("java.lang.String");
         TestValidator validator = new TestValidator(illegal);
         JavaType type = javaTypeFor(String.class);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         validator.validateSubType(ctxt, type);
     }

     @Test
     public void testNullRawDoesNotThrowNPE() {
         JavaType type = mock(JavaType.class);
         when(type.getRawClass()).thenReturn(null);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         try {
             SubTypeValidator.instance().validateSubType(ctxt, type);
         } catch (NullPointerException e) {
             fail("NPE thrown when raw class is null");
         } catch (Exception ignored) {
             // any other exception is acceptable
         }
     }

     @Test
     public void testSpringInterfaceDoesNotThrowNPE() {
         JavaType type = javaTypeFor(springInterfaceMock());
         DeserializationContext ctxt = mock(DeserializationContext.class);
         try {
             SubTypeValidator.instance().validateSubType(ctxt, type);
         } catch (NullPointerException e) {
             fail("NPE thrown for spring interface (interface with null superclass)");
         } catch (Exception ignored) {
             // any other exception is acceptable
         }
     }

     @Test
     public void testObjectClassLoopSkip() throws Exception {
         // Object.class itself causes the loop condition to fail immediately
         Class<?> raw = PowerMockito.mock(Class.class);
         when(raw.getName()).thenReturn("org.springframework.ShouldSkip");
         when(raw.getSuperclass()).thenReturn(Object.class); // chain leads to Object
         when(raw.getSimpleName()).thenReturn("ShouldSkip");
         JavaType type = javaTypeFor(raw);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         SubTypeValidator.instance().validateSubType(ctxt, type);
         // no exception expected
     }

     @Test
     public void testDeepHierarchyNoNPE() throws Exception {
         // Simulate: MySpringClass -> Middle -> Object
         Class<?> middle = PowerMockito.mock(Class.class);
         when(middle.getSuperclass()).thenReturn(Object.class);
         Class<?> leaf = PowerMockito.mock(Class.class);
         when(leaf.getName()).thenReturn("org.springframework.Deep");
         when(leaf.getSuperclass()).thenReturn(middle);
         when(leaf.getSimpleName()).thenReturn("Deep");
         JavaType type = javaTypeFor(leaf);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         SubTypeValidator.instance().validateSubType(ctxt, type);
     }

     @Test
     public void testArrayClassNoNPE() throws Exception {
         JavaType type = javaTypeFor(String[].class);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         SubTypeValidator.instance().validateSubType(ctxt, type);
     }

     @Test
     public void testPrimitiveWrapperNoNPE() throws Exception {
         JavaType type = javaTypeFor(Integer.class);
         DeserializationContext ctxt = mock(DeserializationContext.class);
         SubTypeValidator.instance().validateSubType(ctxt, type);
     }

     @Test
     public void testNullContextWithSafeType() throws Exception {
         // safe type should never touch the context
         JavaType type = javaTypeFor(String.class);
         SubTypeValidator.instance().validateSubType(null, type);
     }

     @Test
     public void testDefaultIllegalSetNotEmpty() {
         assertFalse(SubTypeValidator.DEFAULT_NO_DESER_CLASS_NAMES.isEmpty());
     }

     @Test
     public void testVlidatorIsSingleton() {
         assertSame(SubTypeValidator.instance(), SubTypeValidator.instance());
     }
 }
