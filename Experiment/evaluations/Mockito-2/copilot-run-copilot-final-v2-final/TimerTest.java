package org.mockito.internal.util;

 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;
 import org.mockito.exceptions.misusing.FriendlyReminderException;

 public class TimerTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void should_throw_friendly_reminder_exception_when_duration_is_negative() {
      thrown.expect(FriendlyReminderException.class);
      thrown.expectMessage("negative");
      new Timer(-1);
  }

  @Test
  public void should_throw_for_negative_100() {
      thrown.expect(FriendlyReminderException.class);
      thrown.expectMessage("negative");
      new Timer(-100);
  }

  @Test
  public void should_throw_for_long_min_value() {
      thrown.expect(FriendlyReminderException.class);
      thrown.expectMessage("negative");
      new Timer(Long.MIN_VALUE);
  }

  @Test
  public void should_not_throw_for_zero_duration() {
      new Timer(0);
  }

  @Test
  public void should_not_throw_for_positive_duration() {
      new Timer(1);
  }

  @Test
  public void should_not_throw_for_long_max_value() {
      new Timer(Long.MAX_VALUE);
  }

  @Test
  public void should_throw_assertion_error_if_is_counting_called_before_start() {
      Timer timer = new Timer(1000);
      assertFalse(timer.isCounting());
  }

  @Test
  public void is_counting_returns_true_after_start() {
      Timer timer = new Timer(Long.MAX_VALUE);
      timer.start();
      assertTrue(timer.isCounting());
  }

  @Test
  public void should_not_throw_when_calling_start() {
      Timer timer = new Timer(1000);
      timer.start();
  }

@Test
    public void isCounting_returns_false_once_duration_has_elapsed() throws InterruptedException {
        org.mockito.internal.util.Timer timer = new org.mockito.internal.util.Timer(0L);
        timer.start();
        Thread.sleep(5L);
        org.junit.Assert.assertFalse(timer.isCounting());
    }
}
