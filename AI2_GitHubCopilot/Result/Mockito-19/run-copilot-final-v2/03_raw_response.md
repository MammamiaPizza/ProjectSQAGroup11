package org.mockito.internal.configuration.injection.filter;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;

 import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;

 import org.junit.Test;
 import org.mockito.Mockito;
 import org.mockito.internal.configuration.injection.PropertyAndSetterInjection;

 public class MockInjectionFilterTest {

     // ---- holders ----------------------------------------------------------

     public static class TypeHolder {
         public Runnable collaborator;
     }

     public static class NameHolder {
         public Object collaborator;
     }

     public static class FinalHolder {
         public Runnable collaborator;
     }

     public static class NeedsInjection {
         public Runnable collaborator;
     }

     public static class InjectMocksHolder {
         public NeedsInjection needsInjection = new NeedsInjection();
     }

     // ---- helpers ----------------------------------------------------------

     private static Field field(Class<?> type, String name) throws Exception {
         return type.getField(name);
     }

     private static final class RecordingFilter implements MockCandidateFilter {
         Collection<Object> received = Collections.emptyList();

         public OngoingInjecter filterCandidate(Collection<Object> mocks, Field fieldToBeInjected,
                 Object fieldInstance) {
             received = new ArrayList<Object>(mocks);
             return new OngoingInjecter() {
                 public Object thenInject() {
                     return null;
                 }
             };
         }
     }

     // ---- TypeBasedCandidateFilter ----------------------------------------

     @Test
     public void typeBasedFilterKeepsOnlyAssignableMocks() throws Exception {
         Runnable runnable = Mockito.mock(Runnable.class);
         List<?> list = Mockito.mock(List.class);
         Field target = field(TypeHolder.class, "collaborator");

         RecordingFilter next = new RecordingFilter();
         new TypeBasedCandidateFilter(next)
                 .filterCandidate(Arrays.<Object>asList(runnable, list), target, new TypeHolder());

         assertEquals(1, next.received.size());
         assertSame(runnable, next.received.iterator().next());
     }

     @Test
     public void typeBasedFilterDelegatesEmptyWhenNoTypeMatches() throws Exception {
         List<?> list = Mockito.mock(List.class);
         Field target = field(TypeHolder.class, "collaborator");

         RecordingFilter next = new RecordingFilter();
         new TypeBasedCandidateFilter(next)
                 .filterCandidate(Collections.<Object>singleton(list), target, new TypeHolder());

         assertTrue(next.received.isEmpty());
     }

     // ---- NameBasedCandidateFilter ----------------------------------------

     @Test
     public void nameBasedFilterSelectsOnlyTheMatchingName() throws Exception {
         Object named = Mockito.mock(Object.class, "collaborator");
         Object other = Mockito.mock(Object.class, "other");
         Field target = field(NameHolder.class, "collaborator");

         RecordingFilter next = new RecordingFilter();
         new NameBasedCandidateFilter(next)
                 .filterCandidate(Arrays.asList(named, other), target, new NameHolder());

         assertEquals(1, next.received.size());
         assertSame(named, next.received.iterator().next());
     }

     @Test
     public void nameBasedFilterDelegatesEmptyWhenNoNameMatches() throws Exception {
         Object first = Mockito.mock(Object.class, "first");
         Object second = Mockito.mock(Object.class, "second");
         Field target = field(NameHolder.class, "collaborator");

         RecordingFilter next = new RecordingFilter();
         new NameBasedCandidateFilter(next)
                 .filterCandidate(Arrays.asList(first, second), target, new NameHolder());

         assertTrue(next.received.isEmpty());
     }

     @Test
     public void nameBasedFilterPassesSingleMockThroughRegardlessOfName() throws Exception {
         Object single = Mockito.mock(Object.class, "unrelated");
         Field target = field(NameHolder.class, "collaborator");

         RecordingFilter next = new RecordingFilter();
         new NameBasedCandidateFilter(next)
                 .filterCandidate(Collections.<Object>singleton(single), target, new NameHolder());

         assertEquals(1, next.received.size());
         assertSame(single, next.received.iterator().next());
     }

     // ---- FinalMockCandidateFilter ----------------------------------------

     @Test
     public void finalFilterInjectsAndReturnsSingleCandidate() throws Exception {
         Runnable mock = Mockito.mock(Runnable.class);
         Field target = field(FinalHolder.class, "collaborator");
         FinalHolder holder = new FinalHolder();

         Object result = new FinalMockCandidateFilter()
                 .filterCandidate(Collections.<Object>singleton(mock), target, holder)
                 .thenInject();

         assertSame(mock, result);
         assertSame(mock, holder.collaborator);
     }

     // ---- full PropertyAndSetterInjection chain ----------------------------

     @Test
     public void propertyInjectionUsesNameMatchAmongSameTypeMocks() throws Exception {
         Runnable named = Mockito.mock(Runnable.class, "collaborator");
         Runnable other = Mockito.mock(Runnable.class, "candidate2");
         Set<Object> candidates = new LinkedHashSet<Object>(Arrays.<Object>asList(other, named));

         InjectMocksHolder holder = new InjectMocksHolder();
         Field injectMocksField = field(InjectMocksHolder.class, "needsInjection");

         new PropertyAndSetterInjection().processInjection(injectMocksField, holder, candidates);

         assertSame(named, holder.needsInjection.collaborator);
     }

     @Test
     public void propertyInjectionDoesNotInjectWhenNoNameMatchesSeveralSameTypeMocks() throws
Exception {
         Runnable first = Mockito.mock(Runnable.class, "candidate1");
         Runnable second = Mockito.mock(Runnable.class, "candidate2");
         Set<Object> candidates = new LinkedHashSet<Object>(Arrays.<Object>asList(first, second));

         InjectMocksHolder holder = new InjectMocksHolder();
         Field injectMocksField = field(InjectMocksHolder.class, "needsInjection");

         new PropertyAndSetterInjection().processInjection(injectMocksField, holder, candidates);

         assertNull(holder.needsInjection.collaborator);
     }

     @Test
     public void propertyInjectionInjectsSingleTypeMatchAsFallback() throws Exception {
         Runnable only = Mockito.mock(Runnable.class, "unrelatedName");
         Set<Object> candidates = new LinkedHashSet<Object>(Collections.<Object>singleton(only));

         InjectMocksHolder holder = new InjectMocksHolder();
         Field injectMocksField = field(InjectMocksHolder.class, "needsInjection");

         new PropertyAndSetterInjection().processInjection(injectMocksField, holder, candidates);

         assertSame(only, holder.needsInjection.collaborator);
     }

     @Test
     public void propertyInjectionUsesFieldWhenNoSetterIsAvailable() throws Exception {
         Runnable only = Mockito.mock(Runnable.class);
         Set<Object> candidates = new LinkedHashSet<Object>(Collections.<Object>singleton(only));

         InjectMocksHolder holder = new InjectMocksHolder();
         Field injectMocksField = field(InjectMocksHolder.class, "needsInjection");

         new PropertyAndSetterInjection().processInjection(injectMocksField, holder, candidates);

         assertSame(only, holder.needsInjection.collaborator);
     }

 }