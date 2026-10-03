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
}
