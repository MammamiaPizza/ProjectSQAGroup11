package org.mockitousage.annotation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class PropertyAndSetterInjectionBug19Test {

    public interface Dependency {
    }

    public interface OtherDependency {
    }

    @Before
    public void resetStaticState() {
        MixedFieldsHolder.staticDependency = MixedFieldsHolder.STATIC_SENTINEL;
    }

    @Test
    public void shouldInjectMockIntoFieldWhoseNameMatchesWhenEarlierSameTypeFieldDoesNotMatch() {
        ReservedNameFixture fixture = new ReservedNameFixture();
        MockitoAnnotations.initMocks(fixture);

        assertNull(fixture.holder.candidate1);
        assertSame(fixture.candidate2, fixture.holder.candidate2);
    }

    @Test
    public void shouldNotInjectArbitraryMockWhenSeveralSameTypeMocksHaveNoMatchingName() {
        AmbiguousFixture fixture = new AmbiguousFixture();
        MockitoAnnotations.initMocks(fixture);

        assertNull(fixture.holder.unmatched);
    }

    @Test
    public void shouldInjectUniqueTypeCompatibleMockEvenWhenItsNameDoesNotMatchField() {
        UniqueFixture fixture = new UniqueFixture();
        MockitoAnnotations.initMocks(fixture);

        assertSame(fixture.serviceMock, fixture.holder.dependency);
    }

    @Test
    public void shouldUseSetterForUniqueMockCandidate() {
        SetterFixture fixture = new SetterFixture();
        MockitoAnnotations.initMocks(fixture);

        assertSame(fixture.serviceMock, fixture.holder.getDependency());
        assertEquals(1, fixture.holder.getSetterCalls());
    }

    @Test
    public void shouldIgnoreStaticAndFinalFieldsWhileInjectingMutableField() {
        MixedFieldsFixture fixture = new MixedFieldsFixture();
        MockitoAnnotations.initMocks(fixture);

        assertSame(MixedFieldsHolder.STATIC_SENTINEL, MixedFieldsHolder.staticDependency);
        assertSame(MixedFieldsHolder.FINAL_SENTINEL, fixture.holder.finalDependency);
        assertSame(fixture.dependency, fixture.holder.dependency);
    }

    public static class ReservedNameFixture {
        @Mock(name = "candidate2")
        Dependency candidate2;

        @Mock
        OtherDependency unrelated;

        @InjectMocks
        ReservedNameHolder holder;
    }

    public static class ReservedNameHolder {
        Dependency candidate1;
        Dependency candidate2;
    }

    public static class AmbiguousFixture {
        @Mock(name = "first")
        Dependency first;

        @Mock(name = "second")
        Dependency second;

        @InjectMocks
        AmbiguousHolder holder;
    }

    public static class AmbiguousHolder {
        Dependency unmatched;
    }

    public static class UniqueFixture {
        @Mock(name = "serviceMock")
        Dependency serviceMock;

        @InjectMocks
        UniqueHolder holder;
    }

    public static class UniqueHolder {
        Dependency dependency;
    }

    public static class SetterFixture {
        @Mock(name = "serviceMock")
        Dependency serviceMock;

        @InjectMocks
        SetterHolder holder;
    }

    public static class SetterHolder {
        private Dependency dependency;
        private int setterCalls;

        public void setDependency(Dependency dependency) {
            this.dependency = dependency;
            setterCalls++;
        }

        public Dependency getDependency() {
            return dependency;
        }

        public int getSetterCalls() {
            return setterCalls;
        }
    }

    public static class MixedFieldsFixture {
        @Mock(name = "dependency")
        Dependency dependency;

        @InjectMocks
        MixedFieldsHolder holder;
    }

    public static class MixedFieldsHolder {
        static final Dependency STATIC_SENTINEL = new Dependency() {
        };
        static final Dependency FINAL_SENTINEL = new Dependency() {
        };

        static Dependency staticDependency = STATIC_SENTINEL;
        final Dependency finalDependency = FINAL_SENTINEL;
        Dependency dependency;
    }
}
