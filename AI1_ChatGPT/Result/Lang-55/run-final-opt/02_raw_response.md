package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StopWatchLang315Test {

    @Test
    public void stopWhileSuspendedDoesNotCountSuspendedInterval() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        Thread.sleep(30L);
        watch.suspend();

        long timeAtSuspend = watch.getTime();
        Thread.sleep(150L);

        watch.stop();

        assertEquals("Stopping a suspended watch must retain the time recorded at suspension",
                timeAtSuspend, watch.getTime());
    }

    @Test
    public void resumeExcludesTimeSpentSuspended() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        Thread.sleep(25L);
        watch.suspend();

        long timeAtSuspend = watch.getTime();
        Thread.sleep(150L);

        watch.resume();
        long timeImmediatelyAfterResume = watch.getTime();

        assertTrue("The suspended interval must not be added on resume",
                timeImmediatelyAfterResume <= timeAtSuspend + 30L);

        Thread.sleep(25L);
        watch.stop();

        assertTrue("Running time after resume should be accumulated",
                watch.getTime() >= timeAtSuspend);
    }

    @Test
    public void repeatedSuspendResumeCyclesPreservePreviouslyAccumulatedTime() throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        Thread.sleep(20L);
        watch.suspend();
        long firstSuspendTime = watch.getTime();

        Thread.sleep(80L);
        watch.resume();
        assertTrue(watch.getTime() <= firstSuspendTime + 30L);

        Thread.sleep(20L);
        watch.suspend();
        long secondSuspendTime = watch.getTime();

        assertTrue(secondSuspendTime >= firstSuspendTime);

        Thread.sleep(80L);
        watch.resume();
        assertTrue(watch.getTime() <= secondSuspendTime + 30L);

        watch.stop();
        assertTrue(watch.getTime() >= secondSuspendTime);
    }

    @Test
    public void immediateSuspendResumeAndStopProducesNonNegativeTime() {
        StopWatch watch = new StopWatch();

        watch.start();
        watch.suspend();
        watch.resume();
        watch.stop();

        assertTrue(watch.getTime() >= 0L);
    }

    @Test
    public void invalidStateTransitionsAreRejected() {
        final StopWatch watch = new StopWatch();

        assertIllegalState(new Action() {
            public void run() {
                watch.stop();
            }
        });
        assertIllegalState(new Action() {
            public void run() {
                watch.resume();
            }
        });

        watch.start();

        assertIllegalState(new Action() {
            public void run() {
                watch.start();
            }
        });

        watch.suspend();

        assertIllegalState(new Action() {
            public void run() {
                watch.suspend();
            }
        });

        watch.resume();
        watch.stop();

        assertIllegalState(new Action() {
            public void run() {
                watch.stop();
            }
        });
    }

    @Test
    public void resetClearsRecordedTimeAndAllowsRestart() {
        StopWatch watch = new StopWatch();
        watch.start();
        watch.stop();

        watch.reset();

        assertEquals(0L, watch.getTime());

        watch.start();
        watch.stop();

        assertTrue(watch.getTime() >= 0L);
    }

    private static void assertIllegalState(Action action) {
        try {
            action.run();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // expected
        }
    }

    private interface Action {
        void run();
    }
}