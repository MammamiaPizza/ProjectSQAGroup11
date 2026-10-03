package org.mockito.internal.verification.argumentmatching;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.internal.matchers.ContainsExtraTypeInformation;

public class ArgumentMatchingToolNullSafetyTest {

    interface NullConsumer {
        void accept(String value);
    }

    @Test
    public void shouldHandleNullActualArgumentWithoutThrowing() {
        ArgumentMatchingTool tool = new ArgumentMatchingTool();

        Integer[] suspicious = tool.getSuspiciouslyNotMatchingArgsIndexes(
                matchers(new TextMatcher("wanted", false, false)),
                new Object[] { null });

        assertArrayEquals(new Integer[0], suspicious);
    }

    @Test
    public void shouldReturnNoSuspiciousIndexesForMatchingArguments() {
        ArgumentMatchingTool tool = new ArgumentMatchingTool();

        Integer[] suspicious = tool.getSuspiciouslyNotMatchingArgsIndexes(
                matchers(new TextMatcher("same", true, false)),
                new Object[] { new TextValue("same") });

        assertArrayEquals(new Integer[0], suspicious);
    }

    @Test
    public void shouldIdentifyDifferentTypesWithSameStringRepresentation() {
        ArgumentMatchingTool tool = new ArgumentMatchingTool();

        Integer[] suspicious = tool.getSuspiciouslyNotMatchingArgsIndexes(
                matchers(new TextMatcher("same", false, false)),
                new Object[] { new TextValue("same") });

        assertArrayEquals(new Integer[] { 0 }, suspicious);
    }

    @Test
    public void shouldNotIdentifyArgumentsAsSuspiciousWhenTypesMatch() {
        ArgumentMatchingTool tool = new ArgumentMatchingTool();

        Integer[] suspicious = tool.getSuspiciouslyNotMatchingArgsIndexes(
                matchers(new TextMatcher("same", false, true)),
                new Object[] { new TextValue("same") });

        assertArrayEquals(new Integer[0], suspicious);
    }

    @Test
    public void shouldReturnNoIndexesWhenMatcherAndArgumentCountsDiffer() {
        ArgumentMatchingTool tool = new ArgumentMatchingTool();

        Integer[] suspicious = tool.getSuspiciouslyNotMatchingArgsIndexes(
                matchers(new TextMatcher("one", false, false)),
                new Object[] { "one", "two" });

        assertArrayEquals(new Integer[0], suspicious);
    }

    @Test
    public void shouldAllowVerifyingAnInvocationWithANullArgument() {
        NullConsumer consumer = mock(NullConsumer.class);

        consumer.accept(null);

        verify(consumer).accept((String) null);
    }

    @Test
    public void shouldReportVerificationFailureInsteadOfThrowingNullPointerExceptionForNullActualArgument() {
        NullConsumer consumer = mock(NullConsumer.class);
        consumer.accept(null);

        try {
            verify(consumer).accept("wanted");
            fail("Expected verification to fail because the invocation argument was null");
        } catch (AssertionError expected) {
            // A normal Mockito verification failure is expected.
        }
    }

    private static List<Matcher> matchers(Matcher... matchers) {
        return Arrays.asList(matchers);
    }

    private static class TextValue {
        private final String text;

        TextValue(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static class TextMatcher extends BaseMatcher<Object> implements ContainsExtraTypeInformation {
        private final String text;
        private final boolean matches;
        private final boolean typeMatches;

        TextMatcher(String text, boolean matches, boolean typeMatches) {
            this.text = text;
            this.matches = matches;
            this.typeMatches = typeMatches;
        }

        @Override
        public boolean matches(Object item) {
            return matches;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(text);
        }

        @Override
        public boolean typeMatches(Object actual) {
            return typeMatches;
        }

        @Override
        public Object getWanted() {
            return text;
        }
    }
}