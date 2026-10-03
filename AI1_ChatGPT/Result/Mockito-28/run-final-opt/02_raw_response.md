package org.mockito.internal.configuration;

import org.junit.Test;
import org.mockito.InjectMocks;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DefaultInjectionEngineTest {

    public interface Service {
    }

    public interface FirstDependency {
    }

    public interface SecondDependency {
    }

    public static class SameClassTarget {
        public Object broadDependency = new Object();
        public Service exactDependency;
    }

    public static class ParentTarget {
        public Object broadDependency = new Object();
    }

    public static class ChildTarget extends ParentTarget {
        public Service exactDependency;
    }

    public static class MultipleDependenciesTarget {
        public FirstDependency firstDependency;
        public SecondDependency secondDependency;
    }

    public static class TwoMatchingFieldsTarget {
        public Service firstService;
        public Service secondService;
    }

    public static class UnrelatedDependencyTarget {
        public Runnable runnableDependency = new Runnable() {
            public void run() {
            }
        };
    }

    public static class InitiallyNullTarget {
        public Service service;
    }

    public static class SameClassHolder {
        @InjectMocks
        public SameClassTarget target = new SameClassTarget();
    }

    public static class HierarchyHolder {
        @InjectMocks
        public ChildTarget target = new ChildTarget();
    }

    public static class MultipleDependenciesHolder {
        @InjectMocks
        public MultipleDependenciesTarget target = new MultipleDependenciesTarget();
    }

    public static class TwoMatchingFieldsHolder {
        @InjectMocks
        public TwoMatchingFieldsTarget target = new TwoMatchingFieldsTarget();
    }

    public static class UnrelatedDependencyHolder {
        @InjectMocks
        public UnrelatedDependencyTarget target = new UnrelatedDependencyTarget();
    }

    public static class InitiallyNullHolder {
        @InjectMocks
        public InitiallyNullTarget target;
    }

    @Test
    public void shouldInjectMockIntoExactTypeBeforeCompatibleObjectFieldInSameClass() throws Exception {
        SameClassHolder holder = new SameClassHolder();
        Object originalBroadDependency = holder.target.broadDependency;
        Service service = mock(Service.class);

        inject(holder, service);

        assertSame(service, holder.target.exactDependency);
        assertSame(originalBroadDependency, holder.target.broadDependency);
    }

    @Test
    public void shouldInjectMockIntoSubclassFieldBeforeCompatibleSuperclassField() throws Exception {
        HierarchyHolder holder = new HierarchyHolder();
        Object originalBroadDependency = holder.target.broadDependency;
        Service service = mock(Service.class);

        inject(holder, service);

        assertSame(service, holder.target.exactDependency);
        assertSame(originalBroadDependency, holder.target.broadDependency);
    }

    @Test
    public void shouldInjectEachDistinctMockIntoItsMatchingField() throws Exception {
        MultipleDependenciesHolder holder = new MultipleDependenciesHolder();
        FirstDependency first = mock(FirstDependency.class);
        SecondDependency second = mock(SecondDependency.class);

        inject(holder, first, second);

        assertSame(first, holder.target.firstDependency);
        assertSame(second, holder.target.secondDependency);
    }

    @Test
    public void shouldInjectOneMockOnlyOnceWhenSeveralFieldsCanAcceptIt() throws Exception {
        TwoMatchingFieldsHolder holder = new TwoMatchingFieldsHolder();
        Service service = mock(Service.class);

        inject(holder, service);

        boolean injectedInFirst = holder.target.firstService == service;
        boolean injectedInSecond = holder.target.secondService == service;
        assertTrue("a mock must be injected into exactly one matching field",
                injectedInFirst ^ injectedInSecond);
    }

    @Test
    public void shouldLeaveUnrelatedExistingFieldUntouched() throws Exception {
        UnrelatedDependencyHolder holder = new UnrelatedDependencyHolder();
        Runnable original = holder.target.runnableDependency;

        inject(holder, mock(Service.class));

        assertSame(original, holder.target.runnableDependency);
    }

    @Test
    public void shouldInitializeNullInjectMocksFieldAndInjectCandidate() throws Exception {
        InitiallyNullHolder holder = new InitiallyNullHolder();
        Service service = mock(Service.class);

        inject(holder, service);

        assertNotNull(holder.target);
        assertSame(service, holder.target.service);
    }

    @Test
    public void shouldLeaveSecondMatchingFieldNullAfterSingleMockIsConsumed() throws Exception {
        TwoMatchingFieldsHolder holder = new TwoMatchingFieldsHolder();
        Service service = mock(Service.class);

        inject(holder, service);

        if (holder.target.firstService == service) {
            assertNull(holder.target.secondService);
        } else {
            assertSame(service, holder.target.secondService);
            assertNull(holder.target.firstService);
        }
    }

    private void inject(Object holder, Object... mocks) throws Exception {
        Field field = holder.getClass().getDeclaredField("target");
        Set<Field> fields = new HashSet<Field>();
        fields.add(field);

        Set<Object> candidates = new HashSet<Object>(Arrays.asList(mocks));
        new DefaultInjectionEngine().injectMocksOnFields(fields, candidates, holder);
    }
}