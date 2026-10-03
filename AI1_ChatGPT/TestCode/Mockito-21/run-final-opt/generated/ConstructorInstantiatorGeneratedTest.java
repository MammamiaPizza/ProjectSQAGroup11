package org.mockito.internal.creation.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ConstructorInstantiatorGeneratedTest {

    public static class Outer {
        public class Inner {
            public Outer enclosingInstance() {
                return Outer.this;
            }
        }
    }

    public static class DerivedOuter extends Outer {
    }

    public static class NoArgumentClass {
        public NoArgumentClass() {
        }
    }

    public static class RequiresArgument {
        public RequiresArgument(String value) {
        }
    }

    @Test
    public void createsInnerClassUsingOuterInstanceAssignableToEnclosingType() {
        DerivedOuter outer = new DerivedOuter();

        Outer.Inner created = new ConstructorInstantiator(outer).newInstance(Outer.Inner.class);

        assertNotNull(created);
        assertSame(outer, created.enclosingInstance());
    }

    @Test
    public void createsInnerClassUsingItsExactOuterInstance() {
        Outer outer = new Outer();

        Outer.Inner created = new ConstructorInstantiator(outer).newInstance(Outer.Inner.class);

        assertNotNull(created);
        assertSame(outer, created.enclosingInstance());
    }

    @Test
    public void createsNoArgumentClassWhenNoOuterInstanceIsProvided() {
        NoArgumentClass created = new ConstructorInstantiator(null).newInstance(NoArgumentClass.class);

        assertNotNull(created);
    }

    @Test
    public void reportsOuterTypeProblemWhenOuterInstanceIsIncompatible() {
        try {
            new ConstructorInstantiator(new Object()).newInstance(Outer.Inner.class);
            fail("Expected an InstantationException");
        } catch (InstantationException e) {
            assertEquals(
                    "Unable to create mock instance of 'Inner'.\n"
                            + "Please ensure that the outer instance has correct type and that the target class has parameter-less constructor.",
                    e.getMessage());
        }
    }

    @Test
    public void reportsMissingNoArgumentConstructorWhenNoOuterInstanceIsProvided() {
        try {
            new ConstructorInstantiator(null).newInstance(RequiresArgument.class);
            fail("Expected an InstantationException");
        } catch (InstantationException e) {
            assertEquals(
                    "Unable to create mock instance of 'RequiresArgument'.\n"
                            + "Please ensure it has parameter-less constructor.",
                    e.getMessage());
        }
    }
}
