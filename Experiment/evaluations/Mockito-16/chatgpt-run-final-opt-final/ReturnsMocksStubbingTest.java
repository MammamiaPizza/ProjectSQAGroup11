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
        when(mock(Root.class).label()).thenReturn("configured");
        when((String) null);
    }

@org.junit.Test
public void shouldResetStubbingOnAMock() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.when(list.size()).thenReturn(4);
    org.junit.Assert.assertEquals(4, list.size());

    org.mockito.Mockito.reset(list);

    org.junit.Assert.assertEquals(0, list.size());
}

@org.junit.Test(expected = IllegalStateException.class)
public void shouldThrowExceptionConfiguredForVoidMethod() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.doThrow(new IllegalStateException()).when(list).clear();

    list.clear();
}

@org.junit.Test
public void shouldSupportVerificationModesForRecordedInvocations() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    list.add("first");
    list.add("second");

    org.mockito.Mockito.verify(list, org.mockito.Mockito.atLeastOnce()).add("first");
    org.mockito.Mockito.verify(list, org.mockito.Mockito.atLeast(1)).add("first");
    org.mockito.Mockito.verify(list, org.mockito.Mockito.atMost(1)).add("second");
    org.mockito.Mockito.verify(list, org.mockito.Mockito.never()).add("missing");
}

@org.junit.Test
public void shouldCallRealMethodsOnSpy() {
    java.util.ArrayList<String> list = org.mockito.Mockito.spy(new java.util.ArrayList<String>());

    list.add("value");

    org.junit.Assert.assertEquals(1, list.size());
    org.mockito.Mockito.verify(list).add("value");
}
}
