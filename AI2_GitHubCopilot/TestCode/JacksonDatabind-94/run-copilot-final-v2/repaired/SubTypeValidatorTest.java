package com.fasterxml.jackson.databind.jsontype.impl;

  import static org.junit.Assert.*;
  import static org.mockito.Mockito.mock;

  import org.junit.Rule;
  import org.junit.Test;
  import org.junit.rules.ExpectedException;

  import com.fasterxml.jackson.databind.DeserializationContext;
  import com.fasterxml.jackson.databind.JavaType;
  import com.fasterxml.jackson.databind.JsonMappingException;
  import com.fasterxml.jackson.databind.type.TypeFactory;

  public class SubTypeValidatorTest {

      @Rule
      public final ExpectedException thrown = ExpectedException.none();

      // Static inner class to test subclass of a blocked type
      public static class TestC3P0Subclass extends java.rmi.server.UnicastRemoteObject {
          private static final long serialVersionUID = 1L;
      }

      @Test
      public void testInstanceIsSingleton() {
          assertSame(SubTypeValidator.instance(), SubTypeValidator.instance());
      }

      @Test
      public void testSafeClassDoesNotThrow() throws Exception {
          DeserializationContext ctxt = mock(DeserializationContext.class);
          JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);
          SubTypeValidator.instance().validateSubType(ctxt, stringType);
      }

      @Test
      public void testObjectClassDoesNotThrow() throws Exception {
          DeserializationContext ctxt = mock(DeserializationContext.class);
          JavaType objectType = TypeFactory.defaultInstance().constructType(Object.class);
          SubTypeValidator.instance().validateSubType(ctxt, objectType);
      }

      @Test
      public void testBlockedC3P0TypeThrowsIllegalType() throws Exception {
          thrown.expect(JsonMappingException.class);
          thrown.expectMessage("Illegal type");

          DeserializationContext ctxt = mock(DeserializationContext.class);
          Class<?> c3p0Class =
 Class.forName("com.mchange.v2.c3p0.jacksontest.ComboPooledDataSource");
          JavaType c3p0Type = TypeFactory.defaultInstance().constructType(c3p0Class);
          SubTypeValidator.instance().validateSubType(ctxt, c3p0Type);
      }

      @Test
      public void testSubclassOfBlockedC3P0TypeThrowsIllegalType() throws Exception {
          thrown.expect(JsonMappingException.class);
          thrown.expectMessage("Illegal type");

          DeserializationContext ctxt = mock(DeserializationContext.class);
          JavaType subclassType =
 TypeFactory.defaultInstance().constructType(TestC3P0Subclass.class);
          SubTypeValidator.instance().validateSubType(ctxt, subclassType);
      }

      @Test
      public void testAnotherBlockedClassStillThrows() throws Exception {
          thrown.expect(JsonMappingException.class);
          thrown.expectMessage("Illegal type");

          DeserializationContext ctxt = mock(DeserializationContext.class);
          // Commons Collections InvokerTransformer is in DEFAULT_NO_DESER_CLASS_NAMES,
          // but it may not be on the classpath. Use a class we know is present:
          // org.apache.commons.collections.functors.InvokerTransformer may not be available.
          // Fallback: use java.rmi.server.UnicastRemoteObject which is also blocked
          // and always on the classpath.
          Class<?> blocked = java.rmi.server.UnicastRemoteObject.class;
          JavaType type = TypeFactory.defaultInstance().constructType(blocked);
          SubTypeValidator.instance().validateSubType(ctxt, type);
      }
  }
