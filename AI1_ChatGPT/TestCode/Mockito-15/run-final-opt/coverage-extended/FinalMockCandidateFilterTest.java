package org.mockito.internal.configuration.injection;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FinalMockCandidateFilterTest {

    @Test
    public void shouldUsePropertySetterBeforeDirectFieldAccess() throws Exception {
        Dependency dependency = new Dependency();
        SetterPreferredTarget target = new SetterPreferredTarget();
        Field field = SetterPreferredTarget.class.getDeclaredField("dependency");

        OngoingInjecter injecter = new FinalMockCandidateFilter().filterCandidate(
                Collections.<Object>singletonList(dependency), field, target);

        assertTrue(injecter.thenInject());
        assertTrue(target.setterCalled);
        assertNull(target.dependency);
    }

    @Test
    public void shouldInjectDirectlyIntoFieldWhenNoPropertySetterExists() throws Exception {
        Dependency dependency = new Dependency();
        FieldOnlyTarget target = new FieldOnlyTarget();
        Field field = FieldOnlyTarget.class.getDeclaredField("dependency");

        OngoingInjecter injecter = new FinalMockCandidateFilter().filterCandidate(
                Collections.<Object>singletonList(dependency), field, target);

        assertTrue(injecter.thenInject());
        assertSame(dependency, target.dependency);
    }

    @Test
    public void shouldNotInjectWhenThereIsNoCandidate() throws Exception {
        FieldOnlyTarget target = new FieldOnlyTarget();
        Field field = FieldOnlyTarget.class.getDeclaredField("dependency");

        OngoingInjecter injecter = new FinalMockCandidateFilter().filterCandidate(
                Collections.<Object>emptyList(), field, target);

        assertFalse(injecter.thenInject());
        assertNull(target.dependency);
    }

    public static class Dependency {
    }

    public static class SetterPreferredTarget {
        private Dependency dependency;
        private boolean setterCalled;

        public void setDependency(Dependency dependency) {
            setterCalled = true;
        }
    }

    public static class FieldOnlyTarget {
        private Dependency dependency;
    }
}
