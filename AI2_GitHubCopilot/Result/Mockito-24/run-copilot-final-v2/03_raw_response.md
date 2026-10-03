package org.mockito.internal.stubbing.defaultanswers;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;

 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 import org.junit.Test;
 import org.mockito.invocation.InvocationOnMock;

 public class ReturnsEmptyValuesTest {

     private final ReturnsEmptyValues returnsEmptyValues = new ReturnsEmptyValues();

     private interface Methods {
         int primitiveInt();
         Integer integerWrapper();
         boolean primitiveBoolean();
         String string();
         List<Object> list();
         Set<Object> set();
         Collection<Object> collection();
         Map<Object, Object> map();
         Iterable<Object> iterable();
     }

     private static Method compareToMethod() throws NoSuchMethodException {
         return Comparable.class.getMethod("compareTo", Object.class);
     }

     private static Method method(String name) throws NoSuchMethodException {
         return Methods.class.getMethod(name);
     }

     private InvocationOnMock invocation(final Object mock, final Method method, final Object...
arguments) {
         return new InvocationOnMock() {
             public Object getMock() {
                 return mock;
             }

             public Method getMethod() {
                 return method;
             }

             public Object[] getArguments() {
                 return arguments;
             }

             public Object callRealMethod() throws Throwable {
                 throw new UnsupportedOperationException("callRealMethod is not used by
ReturnsEmptyValues");
             }
         };
     }

     @Test
     public void should_return_zero_if_mock_is_compared_to_itself() throws Exception {
         Object mock = new Object();
         InvocationOnMock invocation = invocation(mock, compareToMethod(), mock);
         assertEquals(Integer.valueOf(0), returnsEmptyValues.answer(invocation));
     }

     @Test
     public void should_return_one_if_mock_is_compared_to_another_mock() throws Exception {
         Object firstMock = new Object();
         Object secondMock = new Object();
         InvocationOnMock invocation = invocation(firstMock, compareToMethod(), secondMock);
         assertEquals(Integer.valueOf(1), returnsEmptyValues.answer(invocation));
     }

     @Test
     public void should_return_one_if_mock_is_compared_to_null() throws Exception {
         Object mock = new Object();
         InvocationOnMock invocation = invocation(mock, compareToMethod(), (Object) null);
         assertEquals(Integer.valueOf(1), returnsEmptyValues.answer(invocation));
     }

     @Test
     public void should_return_zero_for_primitive_int_return_type() throws Exception {
         assertEquals(Integer.valueOf(0), returnsEmptyValues.answer(invocation(new Object(),
method("primitiveInt"))));
     }

     @Test
     public void should_return_zero_for_integer_wrapper_return_type() throws Exception {
         assertEquals(Integer.valueOf(0), returnsEmptyValues.answer(invocation(new Object(),
method("integerWrapper"))));
     }

     @Test
     public void should_return_false_for_primitive_boolean_return_type() throws Exception {
         assertEquals(Boolean.FALSE, returnsEmptyValues.answer(invocation(new Object(),
method("primitiveBoolean"))));
     }

     @Test
     public void should_return_null_for_reference_return_type() throws Exception {
         assertNull(returnsEmptyValues.answer(invocation(new Object(), method("string"))));
     }

     @Test
     public void should_return_empty_list_for_list_return_type() throws Exception {
         Object result = returnsEmptyValues.answer(invocation(new Object(), method("list")));
         assertTrue(result instanceof List);
         assertTrue(((List<?>) result).isEmpty());
     }

     @Test
     public void should_return_empty_set_for_set_return_type() throws Exception {
         Object result = returnsEmptyValues.answer(invocation(new Object(), method("set")));
         assertTrue(result instanceof Set);
         assertTrue(((Set<?>) result).isEmpty());
     }

     @Test
     public void should_return_empty_collection_for_collection_return_type() throws Exception {
         Object result = returnsEmptyValues.answer(invocation(new Object(), method("collection")));
         assertTrue(result instanceof Collection);
         assertTrue(((Collection<?>) result).isEmpty());
     }

     @Test
     public void should_return_empty_map_for_map_return_type() throws Exception {
         Object result = returnsEmptyValues.answer(invocation(new Object(), method("map")));
         assertTrue(result instanceof Map);
         assertTrue(((Map<?, ?>) result).isEmpty());
     }

     @Test
     public void should_return_null_for_unhandled_collection_type() throws Exception {
         assertNull(returnsEmptyValues.answer(invocation(new Object(), method("iterable"))));
     }

 }