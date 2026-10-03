package org.mockito.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;

public class TimerNegativeDurationTest {

    @Test
    public void shouldRejectNegativeTimerDurationWithFriendlyMessage() {
        try {
            new Timer(-1);
            fail("Expected Timer to reject a negative duration");
        } catch (MockitoException e) {
            assertEquals(
                    "It is forbidden to create timer with negative value of timer's duration.",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRejectNegativeTimeoutDurationWithFriendlyMessage() {
        try {
            Mockito.timeout(-1);
            fail("Expected Mockito.timeout() to reject a negative duration");
        } catch (MockitoException e) {
            assertEquals(
                    "It is forbidden to invoke Mockito.timeout() with negative value.",
                    e.getMessage());
        }
    }

    @Test
    public void shouldRejectNegativeAfterDurationWithFriendlyMessage() {
        try {
            Mockito.after(-1);
            fail("Expected Mockito.after() to reject a negative duration");
        } catch (MockitoException e) {
            assertEquals(
                    "It is forbidden to invoke Mockito.after() with negative value.",
                    e.getMessage());
        }
    }

    @Test
    public void shouldCountAfterBeingStartedWithPositiveDuration() {
        Timer timer = new Timer(Long.MAX_VALUE);

        timer.start();

        assertTrue(timer.isCounting());
    }
}
