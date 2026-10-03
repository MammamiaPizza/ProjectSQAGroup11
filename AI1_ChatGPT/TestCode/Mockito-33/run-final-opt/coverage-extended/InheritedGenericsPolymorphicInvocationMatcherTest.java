package org.mockitousage.bugs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class InheritedGenericsPolymorphicInvocationMatcherTest {

    public interface Parent<T> {
        T value();
    }

    public interface Child extends Parent<String> {
        String value();
    }

    @Test
    public void shouldUseStubbingCreatedThroughChildSignatureForParentGenericCall() {
        Child child = mock(Child.class);
        Parent<String> parent = child;

        when(child.value()).thenReturn("stubbed");

        assertEquals("stubbed", parent.value());
    }

    @Test
    public void shouldUseStubbingCreatedThroughParentGenericCallForChildSignature() {
        Child child = mock(Child.class);
        Parent<String> parent = child;

        when(parent.value()).thenReturn("stubbed");

        assertEquals("stubbed", child.value());
    }

    @Test
    public void shouldVerifyParentGenericInvocationUsingChildSignature() {
        Child child = mock(Child.class);
        Parent<String> parent = child;

        parent.value();

        verify(child).value();
    }

    @Test
    public void shouldVerifyChildInvocationUsingParentGenericSignature() {
        Child child = mock(Child.class);
        Parent<String> parent = child;

        child.value();

        verify(parent).value();
    }

@org.junit.Test
public void shouldCreateInvocationMatchersForEveryInvocation() {
    org.mockito.internal.invocation.Invocation first =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.Invocation.class);
    org.mockito.internal.invocation.Invocation second =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.Invocation.class);

    java.util.List<org.mockito.internal.invocation.InvocationMatcher> matchers =
            org.mockito.internal.invocation.InvocationMatcher.createFrom(
                    java.util.Arrays.asList(first, second));

    org.junit.Assert.assertEquals(2, matchers.size());
    org.junit.Assert.assertSame(first, matchers.get(0).getInvocation());
    org.junit.Assert.assertSame(second, matchers.get(1).getInvocation());
}

@org.junit.Test
public void shouldRetainExplicitMatchersProvidedAtConstruction() {
    org.mockito.internal.invocation.Invocation invocation =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.Invocation.class);
    org.hamcrest.Matcher matcher = org.mockito.Mockito.mock(org.hamcrest.Matcher.class);
    java.util.List<org.hamcrest.Matcher> matchers =
            java.util.Collections.singletonList(matcher);

    org.mockito.internal.invocation.InvocationMatcher invocationMatcher =
            new org.mockito.internal.invocation.InvocationMatcher(invocation, matchers);

    org.junit.Assert.assertSame(invocation, invocationMatcher.getInvocation());
    org.junit.Assert.assertSame(matchers, invocationMatcher.getMatchers());
}

@org.junit.Test
public void shouldCaptureArgumentDuringVerification() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);
    org.mockito.ArgumentCaptor<String> captor =
            org.mockito.ArgumentCaptor.forClass(String.class);

    list.add("captured");

    org.mockito.Mockito.verify(list).add(captor.capture());

    org.junit.Assert.assertEquals("captured", captor.getValue());
}
}
