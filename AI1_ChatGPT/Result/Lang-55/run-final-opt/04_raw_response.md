@Test
public void splitAndUnsplitEnforceSplitState() {
    StopWatch watch = new StopWatch();

    try {
        watch.split();
        fail("Splitting an unstarted stopwatch should fail");
    } catch (IllegalStateException expected) {
    }

    watch.start();

    try {
        watch.unsplit();
        fail("Unsplit should fail when no split is active");
    } catch (IllegalStateException expected) {
    }

    watch.split();
    assertTrue("A recorded split time should be non-negative", watch.getSplitTime() >= 0L);

    watch.unsplit();

    try {
        watch.getSplitTime();
        fail("Getting a split time after unsplit should fail");
    } catch (IllegalStateException expected) {
    }
}

@Test
public void stoppedWatchCannotBeStartedAgainWithoutReset() {
    StopWatch watch = new StopWatch();
    watch.start();
    watch.stop();

    try {
        watch.start();
        fail("A stopped stopwatch must be reset before being restarted");
    } catch (IllegalStateException expected) {
    }
}

@Test
public void stoppedSplitFormatsSameRecordedDurationAsStopwatch() {
    StopWatch watch = new StopWatch();
    watch.start();
    watch.split();
    watch.stop();

    assertEquals(watch.toString(), watch.toSplitString());
}

@Test
public void getTimeBeforeStartingThrowsRuntimeException() {
    StopWatch watch = new StopWatch();

    try {
        watch.getTime();
        fail("Getting time from an unstarted stopwatch should fail");
    } catch (RuntimeException expected) {
    }
}