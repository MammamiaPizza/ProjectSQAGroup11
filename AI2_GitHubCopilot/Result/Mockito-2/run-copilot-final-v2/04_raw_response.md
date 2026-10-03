@Test
    public void isCounting_returns_false_once_duration_has_elapsed() throws InterruptedException {
        org.mockito.internal.util.Timer timer = new org.mockito.internal.util.Timer(0L);
        timer.start();
        Thread.sleep(5L);
        org.junit.Assert.assertFalse(timer.isCounting());
    }