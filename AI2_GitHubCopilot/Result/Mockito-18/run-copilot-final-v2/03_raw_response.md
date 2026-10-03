package org.mockito.internal.stubbing.defaultanswers;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Deque;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Queue;
 import java.util.Set;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.invocation.InvocationOnMock;
 import org.mockito.runners.MockitoJUnitRunner;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.when;

 @RunWith(MockitoJUnitRunner.class)
 @SuppressWarnings({ "rawtypes", "unchecked" })
 public class ReturnsEmptyValuesTest {

  @InjectMocks
  private ReturnsEmptyValues returnsEmptyValues;

  @Mock
  private InvocationOnMock invocation;

  private interface Methods {
      Iterable iterableMethod();
      Queue queueMethod();
      Deque dequeMethod();
      Collection collectionMethod();
      Set setMethod();
      List listMethod();
      Map mapMethod();
      ArrayList arrayListMethod();
      HashSet hashSetMethod();
      int intMethod();
      Integer integerMethod();
      String stringMethod();
      void voidMethod();
  }

  private void setUpMethod(String methodName) throws Exception {
      java.lang.reflect.Method method = Methods.class.getMethod(methodName);
      when(invocation.getMethod()).thenReturn(method);
  }

  @Test
  public void should_return_empty_Collection() throws Exception {
      setUpMethod("collectionMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNotNull(result);
      assertTrue(result instanceof LinkedList);
      assertTrue(((Collection) result).isEmpty());
  }

  @Test
  public void should_return_empty_Set() throws Exception {
      setUpMethod("setMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNotNull(result);
      assertTrue(result instanceof HashSet);
      assertTrue(((Set) result).isEmpty());
  }

  @Test
  public void should_return_empty_List() throws Exception {
      setUpMethod("listMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNotNull(result);
      assertTrue(result instanceof LinkedList);
      assertTrue(((List) result).isEmpty());
  }

  @Test
  public void should_return_empty_Map() throws Exception {
      setUpMethod("mapMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNotNull(result);
      assertTrue(result instanceof HashMap);
      assertTrue(((Map) result).isEmpty());
  }

  @Test
  public void should_return_default_int_for_primitive_int() throws Exception {
      setUpMethod("intMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertEquals(0, result);
  }

  @Test
  public void should_return_default_Integer_for_wrapper() throws Exception {
      setUpMethod("integerMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertEquals(0, result);
  }

  @Test
  public void should_return_null_for_unhandled_String() throws Exception {
      setUpMethod("stringMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNull(result);
  }

  @Test
  public void should_return_empty_Iterable() throws Exception {
      setUpMethod("iterableMethod");
      Iterable result = (Iterable) returnsEmptyValues.answer(invocation);
      assertNotNull("return value was null", result);
      assertNotNull("returned iterable has null iterator", result.iterator());
      assertFalse("returned iterable should be empty", result.iterator().hasNext());
  }

  @Test
  public void should_return_empty_Queue() throws Exception {
      setUpMethod("queueMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNull(result);
  }

  @Test
  public void should_return_empty_Deque() throws Exception {
      setUpMethod("dequeMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNull(result);
  }

  @Test
  public void should_return_empty_ArrayList() throws Exception {
      setUpMethod("arrayListMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNotNull(result);
      assertTrue(result instanceof ArrayList);
      assertTrue(((ArrayList) result).isEmpty());
  }

  @Test
  public void should_return_empty_HashSet() throws Exception {
      setUpMethod("hashSetMethod");
      Object result = returnsEmptyValues.answer(invocation);
      assertNotNull(result);
      assertTrue(result instanceof HashSet);
      assertTrue(((HashSet) result).isEmpty());
  }

 }