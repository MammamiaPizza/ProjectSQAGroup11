package org.mockito.internal.configuration.injection;

 import static org.junit.Assert.*;

 import java.lang.reflect.Field;
 import java.util.*;

 import org.junit.Test;
 import org.mockito.exceptions.base.MockitoException;

 public class FinalMockCandidateFilterTest {

     private final FinalMockCandidateFilter filter = new FinalMockCandidateFilter();

     // helper beans
     public static class BeanWithSetter {
         private Object value;
         private boolean setterCalled = false;

         public void setValue(Object v) {
             this.value = v;
             this.setterCalled = true;
         }
         public Object getValue() { return value; }
         public boolean wasSetterCalled() { return setterCalled; }
     }

     public static class PrimitiveFieldBean {
         private int number;
         public int getNumber() { return number; }
     }

     public static class SimpleBean {
         private Object dependency;
         public Object getDependency() { return dependency; }
     }

     // Normal: exactly one mock → injection via field, returns true
     @Test
     public void singleMockInjectsViaFieldAndReturnsTrue() throws Exception {
         Object mock = new Object();
         List<Object> mocks = Collections.<Object>singletonList(mock);
         SimpleBean bean = new SimpleBean();
         Field field = SimpleBean.class.getDeclaredField("dependency");

         OngoingInjecter injecter = filter.filterCandidate(mocks, field, bean);
         boolean result = injecter.thenInject();

         assertTrue("thenInject should return true for single mock", result);
         assertSame("mock should be set directly into field", mock, bean.getDependency());
     }

     // Normal: setter exists, but buggy code still injects via field (setter not invoked)
     @Test
     public void singleMockWithSetterDoesNotUseSetter() throws Exception {
         Object mock = new Object();
         List<Object> mocks = Collections.<Object>singletonList(mock);
         BeanWithSetter bean = new BeanWithSetter();
         Field field = BeanWithSetter.class.getDeclaredField("value");

         filter.filterCandidate(mocks, field, bean).thenInject();

         assertFalse("BUG: setter should have been called but was not", bean.wasSetterCalled());
         assertSame("direct field injection still sets the value", mock, bean.getValue());
     }

     // Boundary: empty mocks → returning injecter returns false
     @Test
     public void emptyMocksReturnsInjecterThatDoesNothing() throws Exception {
         List<Object> mocks = Collections.emptyList();
         SimpleBean bean = new SimpleBean();
         Field field = SimpleBean.class.getDeclaredField("dependency");

         OngoingInjecter injecter = filter.filterCandidate(mocks, field, bean);
         boolean result = injecter.thenInject();

         assertFalse("thenInject should return false for empty mocks", result);
         assertNull("field should remain null", bean.getDependency());
     }

     // Boundary: multiple mocks → returning injecter returns false
     @Test
     public void multipleMocksReturnsInjecterThatDoesNothing() throws Exception {
         List<Object> mocks = Arrays.asList(new Object(), new Object());
         SimpleBean bean = new SimpleBean();
         Field field = SimpleBean.class.getDeclaredField("dependency");

         OngoingInjecter injecter = filter.filterCandidate(mocks, field, bean);
         boolean result = injecter.thenInject();

         assertFalse("thenInject should return false when more than one mock", result);
         assertNull("field must not be set", bean.getDependency());
     }

     // Boundary: exactly one mock, correct mock identity in field
     @Test
     public void injectedMockIsExactSameInstance() throws Exception {
         Object mock1 = new Object();
         Object mock2 = new Object();
         // only mock1 is in collection
         List<Object> mocks = Collections.<Object>singletonList(mock1);
         BeanWithSetter bean = new BeanWithSetter();
         Field field = BeanWithSetter.class.getDeclaredField("value");

         filter.filterCandidate(mocks, field, bean).thenInject();

         assertSame("injected value must be the exact mock instance", mock1, bean.getValue());
     }

     // Boundary: no setter → falls back to field injection (already covered by normal case above,
     // but we also verify that a bean without setter works fine)
     @Test
     public void fieldOnlyBeanInjectionSucceeds() throws Exception {
         Object mock = new Object();
         List<Object> mocks = Collections.<Object>singletonList(mock);
         SimpleBean bean = new SimpleBean();
         Field field = SimpleBean.class.getDeclaredField("dependency");

         OngoingInjecter injecter = filter.filterCandidate(mocks, field, bean);
         boolean result = injecter.thenInject();

         assertTrue("injection must succeed", result);
         assertSame(mock, bean.getDependency());
     }

     // Error: type mismatch between mock and field → MockitoException
     @Test(expected = MockitoException.class)
     public void typeMismatchThrowsMockitoException() throws Exception {
         Object mock = new Object(); // not assignable to int
         List<Object> mocks = Collections.<Object>singletonList(mock);
         PrimitiveFieldBean bean = new PrimitiveFieldBean();
         Field field = PrimitiveFieldBean.class.getDeclaredField("number");

         filter.filterCandidate(mocks, field, bean).thenInject();
     }

     // Invalid input: null mocks collection → NullPointerException
     @Test(expected = NullPointerException.class)
     public void nullMocksThrowsNullPointerException() throws Exception {
         SimpleBean bean = new SimpleBean();
         Field field = SimpleBean.class.getDeclaredField("dependency");
         filter.filterCandidate(null, field, bean);
     }

     // Invalid input: null fieldInstance → thenInject throws MockitoException (wraps underlying
NPE)
     @Test(expected = MockitoException.class)
     public void nullFieldInstanceCausesMockitoExceptionOnInject() throws Exception {
         Object mock = new Object();
         List<Object> mocks = Collections.<Object>singletonList(mock);
         Field field = SimpleBean.class.getDeclaredField("dependency");

         filter.filterCandidate(mocks, field, null).thenInject();
     }

     // filterCandidate always returns non-null injecter
     @Test
     public void filterCandidateAlwaysReturnsNonNullInjecter() throws Exception {
         SimpleBean bean = new SimpleBean();
         Field field = SimpleBean.class.getDeclaredField("dependency");

         assertNotNull(filter.filterCandidate(Collections.emptyList(), field, bean));
         assertNotNull(filter.filterCandidate(Collections.singletonList(new Object()), field,
bean));
         assertNotNull(filter.filterCandidate(Arrays.asList(new Object(), new Object()), field,
bean));
     }

     // Boundary: field with null mock in single-element list → injects null
     @Test
     public void singleNullMockInjectsNull() throws Exception {
         List<Object> mocks = new ArrayList<Object>();
         mocks.add(null);
         SimpleBean bean = new SimpleBean();
         bean.dependency = new Object(); // pre-set value
         Field field = SimpleBean.class.getDeclaredField("dependency");

         filter.filterCandidate(mocks, field, bean).thenInject();

         assertNull("null mock should set field to null", bean.getDependency());
     }
 }