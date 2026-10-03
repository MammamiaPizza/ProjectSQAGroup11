package org.mockitousage.bugs;

import org.junit.Test;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReturnsMocksStubbingTest {

    interface Root {
        Child child();
        String label();
    }

    interface Child {
        String value();
    }

    @Test
    public void shouldAllowStubbingMethodConfiguredToReturnMock() {
        Child expectedChild = mock(Child.class);
        Root root = mock(Root.class, RETURNS_MOCKS);

        when(root.child()).thenReturn(expectedChild);

        assertSame(expectedChild, root.child());
    }

    @Test
    public void shouldAllowStubbingMethodsOnAutomaticallyReturnedMocks() {
        Root root = mock(Root.class, RETURNS_MOCKS);
        Child child = root.child();

        assertNotNull(child);
        when(child.value()).thenReturn("configured");

        assertEquals("configured", child.value());
    }

    @Test
    public void shouldAllowDirectStubbingOnMockConfiguredToReturnMocks() {
        Root root = mock(Root.class, RETURNS_MOCKS);

        when(root.label()).thenReturn("configured label");

        assertEquals("configured label", root.label());
    }

    @Test(expected = MissingMethodInvocationException.class)
    public void shouldRejectWhenWithoutAMockMethodInvocation() {
        when((String) null);
    }
}