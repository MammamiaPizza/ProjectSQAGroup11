package org.mockito.internal.configuration.injection.filter;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.lang.reflect.Field;
 import java.util.*;

 import org.junit.Test;
 import org.mockito.internal.configuration.injection.PropertyAndSetterInjection;
 import org.mockito.internal.configuration.injection.filter.FinalMockCandidateFilter;
 import org.mockito.internal.configuration.injection.filter.MockCandidateFilter;
 import org.mockito.internal.configuration.injection.filter.NameBasedCandidateFilter;
 import org.mockito.internal.configuration.injection.filter.OngoingInjecter;
 import org.mockito.internal.configuration.injection.filter.TypeBasedCandidateFilter;

 /**
  * Tests for mock injection filter chain and {@link PropertyAndSetterInjection}.
  */
 public class FilterAndInjectionTest {

     // ---------------------------------------------------------------
     // Helper target classes for injection
     // ---------------------------------------------------------------

     public static class Target {
         public Object field1;
         public Object field2;
         public String stringField;
     }

     public static class ParentWithField {
         public Object parentField;
     }

     public static class ChildTarget extends ParentWithField {
         public Object childField;
     }

     // A field in the test class to act as the @InjectMocks target holder
     private Target target = new Target();
     private ChildTarget childTarget = new ChildTarget();

     // ---------------------------------------------------------------
     // FinalMockCandidateFilter tests
     // ---------------------------------------------------------------

     @Test
     public void shouldInjectSingleMock() throws Exception {
         FinalMockCandidateFilter filter = new FinalMockCandidateFilter();
         Object mock = mock(Object.class, "mock1");
         Field field = Target.class.getDeclaredField("field1");
         Target t = new Target();

         OngoingInjecter injecter = filter.filterCandidate(
                 Collections.singleton(mock), field, t);
         Object result = injecter.thenInject();

         assertSame(mock, result);
         assertSame(mock, t.field1);
     }

     @Test
     public void shouldReturnNullWhenMultipleMocks() throws Exception {
         FinalMockCandidateFilter filter = new FinalMockCandidateFilter();
         Object mock1 = mock(Object.class, "m1");
         Object mock2 = mock(Object.class, "m2");
         Field field = Target.class.getDeclaredField("field1");
         Target t = new Target();

         OngoingInjecter injecter = filter.filterCandidate(
                 Arrays.asList(mock1, mock2), field, t);
         Object result = injecter.thenInject();

         assertNull(result);
         assertNull(t.field1);
     }

     @Test
     public void shouldReturnNullWhenNoMocks() throws Exception {
         FinalMockCandidateFilter filter = new FinalMockCandidateFilter();
         Field field = Target.class.getDeclaredField("field1");
         Target t = new Target();

         OngoingInjecter injecter = filter.filterCandidate(
                 Collections.emptyList(), field, t);
         Object result = injecter.thenInject();

         assertNull(result);
         assertNull(t.field1);
     }

     // ---------------------------------------------------------------
     // TypeBasedCandidateFilter tests
     // ---------------------------------------------------------------

     @Test
     public void shouldFilterByAssignableType() throws Exception {
         // spy on the next filter to record passed candidates
         final List<Object> captured = new ArrayList<>();
         MockCandidateFilter next = new MockCandidateFilter() {
             @Override
             public OngoingInjecter filterCandidate(Collection<Object> mocks,
                     Field field, Object fieldInstance) {
                 captured.addAll(mocks);
                 return new OngoingInjecter() {
                     @Override public Object thenInject() { return null; }
                 };
             }
         };
         TypeBasedCandidateFilter typeFilter = new TypeBasedCandidateFilter(next);

         Object stringMock = mock(String.class, "str");
         Object intMock = mock(Integer.class, "int");
         Field stringField = Target.class.getDeclaredField("stringField");

         typeFilter.filterCandidate(
                 Arrays.asList(stringMock, intMock),
                 stringField, new Target());

         assertTrue("String mock should pass type filter", captured.contains(stringMock));
         assertFalse("Integer mock should be excluded", captured.contains(intMock));
     }

     // ---------------------------------------------------------------
     // NameBasedCandidateFilter tests
     // ---------------------------------------------------------------

     @Test
     public void shouldFilterByNameWhenMultipleMocks() throws Exception {
         final List<Object> captured = new ArrayList<>();
         MockCandidateFilter next = new MockCandidateFilter() {
             @Override
             public OngoingInjecter filterCandidate(Collection<Object> mocks,
                     Field field, Object fieldInstance) {
                 captured.addAll(mocks);
                 return new OngoingInjecter() {
                     @Override public Object thenInject() { return null; }
                 };
             }
         };
         NameBasedCandidateFilter nameFilter = new NameBasedCandidateFilter(next);

         Object mockMatching = mock(Object.class, "field1");
         Object mockOther = mock(Object.class, "other");
         Field field1 = Target.class.getDeclaredField("field1");

         nameFilter.filterCandidate(
                 Arrays.asList(mockMatching, mockOther),
                 field1, new Target());

         assertEquals("Only the name-matching mock should be passed to next",
                 1, captured.size());
         assertSame(mockMatching, captured.get(0));
     }

     @Test
     public void shouldNotFilterWhenSingleMock() throws Exception {
         final List<Object> captured = new ArrayList<>();
         MockCandidateFilter next = new MockCandidateFilter() {
             @Override
             public OngoingInjecter filterCandidate(Collection<Object> mocks,
                     Field field, Object fieldInstance) {
                 captured.addAll(mocks);
                 return new OngoingInjecter() {
                     @Override public Object thenInject() { return null; }
                 };
             }
         };
         NameBasedCandidateFilter nameFilter = new NameBasedCandidateFilter(next);

         Object singleMock = mock(Object.class, "anyName");
         Field field = Target.class.getDeclaredField("field1");

         nameFilter.filterCandidate(
                 Collections.singleton(singleMock),
                 field, new Target());

         assertEquals(1, captured.size());
         assertSame(singleMock, captured.get(0));
     }

     // ---------------------------------------------------------------
     // Full chain integration (Type -> Name -> Final)
     // ---------------------------------------------------------------

     @Test
     public void shouldInjectByNameMatchThroughFullChain() throws Exception {
         MockCandidateFilter chain = new TypeBasedCandidateFilter(
                 new NameBasedCandidateFilter(
                         new FinalMockCandidateFilter()));

         Object mockMatching = mock(Object.class, "field1");
         Object mockOther = mock(Object.class, "other");
         Field field1 = Target.class.getDeclaredField("field1");
         Target t = new Target();

         chain.filterCandidate(
                 Arrays.asList(mockMatching, mockOther),
                 field1, t).thenInject();

         assertSame(mockMatching, t.field1);
     }

     @Test
     public void shouldReturnNullWhenNoNameMatch() throws Exception {
         MockCandidateFilter chain = new TypeBasedCandidateFilter(
                 new NameBasedCandidateFilter(
                         new FinalMockCandidateFilter()));

         Object mock = mock(Object.class, "something");
         Field field2 = Target.class.getDeclaredField("field2");
         Target t = new Target();

         Object result = chain.filterCandidate(
                 Collections.singleton(mock),
                 field2, t).thenInject();

         assertNull(result);
         assertNull(t.field2);
     }

     // ---------------------------------------------------------------
     // PropertyAndSetterInjection integration (multi-field scenarios)
     // ---------------------------------------------------------------

     @Test
     public void shouldInjectMultipleFieldsWithMatchingNames() throws Exception {
         Field injectField = FilterAndInjectionTest.class.getDeclaredField("target");
         Target t = new Target();
         injectField.set(this, t); // update the test field to use a fresh instance

         PropertyAndSetterInjection injection = new PropertyAndSetterInjection();
         Object mock1 = mock(Object.class, "field1");
         Object mock2 = mock(Object.class, "field2");
         Set<Object> mocks = new HashSet<>(Arrays.asList(mock1, mock2));

         injection.processInjection(injectField, this, mocks);

         assertSame(mock1, t.field1);
         assertSame(mock2, t.field2);
     }

     @Test
     public void shouldNotInjectSecondFieldWhenNoNameMatches()
             throws Exception {
         Field injectField = FilterAndInjectionTest.class.getDeclaredField("target");
         Target t = new Target();
         injectField.set(this, t);

         PropertyAndSetterInjection injection = new PropertyAndSetterInjection();
         Object mockMatching = mock(Object.class, "field1");
         Object mockOther = mock(Object.class, "other");
         Set<Object> mocks = new HashSet<>(Arrays.asList(mockMatching, mockOther));

         injection.processInjection(injectField, this, mocks);

         assertSame(mockMatching, t.field1);
         // Bug: if injection wrongly reuses a mock for field2, this fails
         assertNull("Field2 should remain null when no mock name matches", t.field2);
     }

     @Test
     public void shouldInjectIntoSuperclassField() throws Exception {
         Field injectField = FilterAndInjectionTest.class.getDeclaredField("childTarget");
         ChildTarget t = new ChildTarget();
         injectField.set(this, t);

         PropertyAndSetterInjection injection = new PropertyAndSetterInjection();
         Object mockParent = mock(Object.class, "parentField");
         Set<Object> mocks = new HashSet<>(Collections.singleton(mockParent));

         injection.processInjection(injectField, this, mocks);

         assertSame(mockParent, t.parentField);
         assertNull(t.childField);
     }
 }
