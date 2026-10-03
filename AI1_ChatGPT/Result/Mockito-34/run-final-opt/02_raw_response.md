package org.mockito.internal.invocation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.CapturesArguments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InvocationMatcherGeneratedTest {

    private interface SampleService {
        void one(String value);
        void varargs(String... values);
    }

    @Test
    public void shouldNotCaptureOrThrowWhenThereAreNoInvocationArguments() throws Exception {
        CapturingMatcher captor = new CapturingMatcher();
        InvocationMatcher matcher = new InvocationMatcher(
                invocation(new Object(), SampleService.class.getMethod("one", String.class)),
                Arrays.<Matcher>asList(captor));

        matcher.captureArgumentsFrom(
                invocation(new Object(), SampleService.class.getMethod("one", String.class)));

        assertEquals(0, captor.captureCount);
    }

    @Test
    public void shouldCaptureAvailableArgumentsAndIgnoreMatchersWithoutArguments() throws Exception {
        CapturingMatcher firstCaptor = new CapturingMatcher();
        CapturingMatcher missingCaptor = new CapturingMatcher();
        Method method = SampleService.class.getMethod("one", String.class);

        InvocationMatcher matcher = new InvocationMatcher(
                invocation(new Object(), method, "available"),
                Arrays.<Matcher>asList(firstCaptor, missingCaptor));

        matcher.captureArgumentsFrom(invocation(new Object(), method, "available"));

        assertEquals(1, firstCaptor.captureCount);
        assertEquals("available", firstCaptor.captured);
        assertEquals(0, missingCaptor.captureCount);
    }

    @Test
    public void shouldUseMatcherPositionWhenCapturingArguments() throws Exception {
        CapturingMatcher captor = new CapturingMatcher();
        Method method = SampleService.class.getMethod("varargs", String[].class);

        InvocationMatcher matcher = new InvocationMatcher(
                invocation(new Object(), method, "ignored", "captured"),
                Arrays.<Matcher>asList(new AlwaysMatcher(), captor));

        matcher.captureArgumentsFrom(invocation(new Object(), method, "ignored", "captured"));

        assertEquals(1, captor.captureCount);
        assertEquals("captured", captor.captured);
    }

    @Test
    public void shouldMatchEqualArguments() throws Exception {
        Object mock = new Object();
        Method method = SampleService.class.getMethod("one", String.class);
        InvocationMatcher matcher = new InvocationMatcher(
                invocation(mock, method, "value"),
                Arrays.<Matcher>asList(new EqualMatcher("value")));

        assertTrue(matcher.matches(invocation(mock, method, "value")));
    }

    @Test
    public void shouldNotMatchWhenNonVarargArgumentCountsDiffer() throws Exception {
        Object mock = new Object();
        Method method = SampleService.class.getMethod("one", String.class);
        InvocationMatcher matcher = new InvocationMatcher(
                invocation(mock, method, "value"),
                Arrays.<Matcher>asList(new AlwaysMatcher()));

        assertFalse(matcher.matches(invocation(mock, method)));
    }

    @Test
    public void shouldMatchAnEmptyVarargInvocation() throws Exception {
        Object mock = new Object();
        Method method = SampleService.class.getMethod("varargs", String[].class);
        Invocation wanted = invocation(mock, method);
        InvocationMatcher matcher = new InvocationMatcher(wanted);

        assertTrue(matcher.matches(invocation(mock, method)));
    }

    private Invocation invocation(Object mock, Method method, Object... arguments) {
        Invocation invocation = Mockito.mock(Invocation.class);
        Mockito.when(invocation.getMock()).thenReturn(mock);
        Mockito.when(invocation.getMethod()).thenReturn(method);
        Mockito.when(invocation.getArguments()).thenReturn(arguments);
        Mockito.when(invocation.argumentsToMatchers()).thenReturn(Collections.<Matcher>emptyList());
        return invocation;
    }

    private static class AlwaysMatcher extends BaseMatcher<Object> {
        public boolean matches(Object item) {
            return true;
        }

        public void describeTo(Description description) {
            description.appendText("any value");
        }
    }

    private static class EqualMatcher extends BaseMatcher<Object> {
        private final Object expected;

        EqualMatcher(Object expected) {
            this.expected = expected;
        }

        public boolean matches(Object item) {
            return expected == null ? item == null : expected.equals(item);
        }

        public void describeTo(Description description) {
            description.appendValue(expected);
        }
    }

    private static class CapturingMatcher extends BaseMatcher<Object> implements CapturesArguments {
        private Object captured;
        private int captureCount;

        public boolean matches(Object item) {
            return true;
        }

        public void captureFrom(Object argument) {
            captured = argument;
            captureCount++;
        }

        public void describeTo(Description description) {
            description.appendText("capturing matcher");
        }
    }
}