package com.fasterxml.jackson.databind.misc;

import static org.junit.Assert.;
import org.junit.;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.MapperFeature;

import java.security.Permission;

/**

 - Regression test for [databind#877]: BeanDeserializerFactory should not attempt
 - to force access on Throwable.cause, preventing SecurityExceptions under a
 - restrictive SecurityManager.
  */
 public class AccessFixTest {
  /**
  - Minimal Throwable subclass used as deserialization target.
    */
   public static class SimpleException extends Exception {
   private static final long serialVersionUID = 1L;
   public SimpleException() { super(); }
   public SimpleException(String msg) { super(msg); }
   }
  /**
  - Core regression: deserialize a Throwable subclass with a {@code cause} property
  - while a SecurityManager that prohibits suppressAccessChecks is active.
  - The expected behaviour (post-fix) is success with the cause set via
  - initCause rather than a failed field-access attempt.
    */
   @Test
   public void testCauseOfThrowableIgnoral() throws Exception {
   final ObjectMapper mapper = new ObjectMapper();
   // Enable the feature that would otherwise trigger fixAccess on the
   // private Throwable.cause field.
   mapper.configure(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS, true);
   // SecurityManager that denies ReflectPermission("suppressAccessChecks")
   final SecurityManager restrictive = new SecurityManager() {
   @Override
   public void checkPermission(Permission perm) {
       if ("suppressAccessChecks".equals(perm.getName())) {
           throw new SecurityException("Access denied to " + perm.getName());
       }
   }
   };
   final SecurityManager previous = System.getSecurityManager();
   System.setSecurityManager(restrictive);
   try {
   // JSON representing a SimpleException with a nested cause.
   // The fix should ignore the raw cause field and rely on initCause.
   String json = "{"message":"outer","cause":{"message":"inner"}}";
   SimpleException ex = mapper.readValue(json, SimpleException.class);
   assertNotNull("Deserialized exception should not be null", ex);
   assertEquals("outer", ex.getMessage());
   assertNotNull("Cause should have been set via initCause", ex.getCause());
   assertEquals("inner", ex.getCause().getMessage());
   } finally {
   System.setSecurityManager(previous);
   }
  }
  /**
  - Without a restrictive SecurityManager the normal deserialization
  - path should work unchanged.
    */
   @Test
   public void testCauseOfThrowableIgnoral_looseSecurityManager() throws Exception {
   final ObjectMapper mapper = new ObjectMapper();
   mapper.configure(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS, true);
   // No custom SecurityManager – depends on the current JVM settings.
   String json = "{"message":"a","cause":{"message":"b"}}";
   SimpleException ex = mapper.readValue(json, SimpleException.class);
   assertNotNull(ex);
   assertEquals("a", ex.getMessage());
   assertNotNull(ex.getCause());
   assertEquals("b", ex.getCause().getMessage());
  }
  /**
  - Throwable without a cause should be handled without any attempt
  - to access the {@code cause} field.
    */
   @Test
   public void testThrowableWithoutCause() throws Exception {
   ObjectMapper mapper = new ObjectMapper();
   mapper.configure(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS, true);
   String json = "{"message":"standalone"}";
   SimpleException ex = mapper.readValue(json, SimpleException.class);
   assertNotNull(ex);
   assertEquals("standalone", ex.getMessage());
   assertNull(ex.getCause());
  }
  /**
  - When OVERRIDE_PUBLIC_ACCESS_MODIFIERS is disabled (default), the
  - problematic fixAccess path is avoided entirely and deserialization
  - must succeed regardless of the SecurityManager.
    */
   @Test
   public void testCauseOfThrowableIgnoral_disabledOverride() throws Exception {
   ObjectMapper mapper = new ObjectMapper();
   // feature disabled
   assertFalse(mapper.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
   SecurityManager restrictive = new SecurityManager() {
   @Override
   public void checkPermission(Permission perm) {
       if ("suppressAccessChecks".equals(perm.getName())) {
           throw new SecurityException("Access denied to " + perm.getName());
       }
   }
   };
   SecurityManager previous = System.getSecurityManager();
   System.setSecurityManager(restrictive);
   try {
   String json = "{"message":"x","cause":{"message":"y"}}";
   SimpleException ex = mapper.readValue(json, SimpleException.class);
   assertNotNull(ex);
   assertEquals("x", ex.getMessage());
   assertNotNull(ex.getCause());
   assertEquals("y", ex.getCause().getMessage());
   } finally {
   System.setSecurityManager(previous);
   }
  }

}
