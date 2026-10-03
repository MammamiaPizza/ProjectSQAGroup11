package org.mockito.internal.verification;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.util.Arrays;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.Mock;
 import org.mockito.exceptions.base.MockitoAssertionError;
 import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
 import org.mockito.internal.util.Timer;
 import org.mockito.internal.verification.api.VerificationData;
 import org.mockito.invocation.Invocation;
 import org.mockito.runners.MockitoJUnitRunner;
 import org.mockito.verification.VerificationMode;

 /**
  * Tests for {@link VerificationOverTimeImpl} covering polling retry logic,
  * immediate and delayed success, exception propagation, recoverable vs
  * unrecoverable delegates, and constructor variants.
  */
 @RunWith(MockitoJUnitRunner.class)
 public class VerificationOverTimeImplTest {

     @Mock
     private Timer timer;

     @Mock
     private VerificationData data;

     @Mock
     private VerificationMode delegate;

     @Mock
     private Invocation invocation;

     @Test
     public void whenDelegatePassesAndReturnOnSuccessTrue_thenReturnsImmediately() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(10, 100, delegate, true,
timer);
         when(timer.isCounting()).thenReturn(true);
         doNothing().when(delegate).verify(data);

         impl.verify(data);

         verify(timer).start();
         verify(delegate).verify(data);
     }

     @Test
     public void whenDelegatePassesAndReturnOnSuccessFalse_thenRetriesUntilTimerStops() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(10, 100, delegate, false,
timer);
         doNothing().when(delegate).verify(data);
         when(timer.isCounting()).thenReturn(true, true, false);

         impl.verify(data);

         verify(timer).start();
         verify(delegate, times(2)).verify(data);
     }

     @Test
     public void whenDelegateFailsThenPassesWithReturnOnSuccessTrue_thenRetriesAndReturns() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(1, 100, delegate, true,
timer);
         MockitoAssertionError firstError = new MockitoAssertionError("first failure");
         doThrow(firstError).doNothing().when(delegate).verify(data);
         when(timer.isCounting()).thenReturn(true, true);

         impl.verify(data);

         verify(delegate, times(2)).verify(data);
     }

     @Test
     public void whenDelegateAlwaysFails_thenThrowsLastErrorAfterTimeout() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(1, 100, delegate, false,
timer);
         MockitoAssertionError lastError = new MockitoAssertionError("final error");
         doThrow(lastError).when(delegate).verify(data);
         when(timer.isCounting()).thenReturn(true, true, false);

         try {
             impl.verify(data);
             fail("Expected MockitoAssertionError");
         } catch (MockitoAssertionError e) {
             assertSame(lastError, e);
         }

         verify(delegate, times(2)).verify(data);
     }

     @Test
     public void whenDelegateIsUnrecoverable_thenThrowsImmediatelyWithoutSleeping() {
         AtMost atMost = new AtMost(0);
         when(data.getAllInvocations()).thenReturn(Arrays.asList(invocation));

         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(10, 100, atMost, true, timer);
         when(timer.isCounting()).thenReturn(true);

         try {
             impl.verify(data);
             fail("Expected MockitoAssertionError");
         } catch (MockitoAssertionError e) {
             // expected because AtMost(0) fails with any invocation and is unrecoverable
         }

         verify(timer).start();
     }

     @Test
     public void whenDelegateThrowsArgumentsAreDifferent_thenHandledAsRecoverableError() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(1, 100, delegate, true,
timer);
         ArgumentsAreDifferent diffError = new ArgumentsAreDifferent("arg differ", "expected",
"actual");
         doThrow(diffError).doNothing().when(delegate).verify(data);
         when(timer.isCounting()).thenReturn(true, true);

         // should not throw because the error is caught and a retry succeeds
         impl.verify(data);

         verify(delegate, times(2)).verify(data);
     }

     @Test
     public void whenDurationIsZero_thenDelegateNeverCalled() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(10, 0, delegate, true, timer);
         when(timer.isCounting()).thenReturn(false);

         impl.verify(data);

         verify(delegate, never()).verify(data);
     }

     @Test
     public void whenDelegateFailsAndReturnOnSuccessFalse_thenContinuesPollingUntilTimeout() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(1, 100, delegate, false,
timer);
         MockitoAssertionError first = new MockitoAssertionError("first");
         doThrow(first).doThrow(first).doNothing().when(delegate).verify(data);
         when(timer.isCounting()).thenReturn(true, true, true, false);

         impl.verify(data);

         verify(delegate, times(3)).verify(data);
     }

     @Test
     public void getPollingPeriodAndDurationShouldReturnConstructorValues() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(17, 35, delegate, true);
         assertEquals(17, impl.getPollingPeriod());
         assertEquals(35, impl.getDuration());
         assertSame(delegate, impl.getDelegate());
     }

     @Test
     public void defaultTimerConstructorShouldWorkWhenDelegatePasses() {
         VerificationOverTimeImpl impl = new VerificationOverTimeImpl(10, 100, delegate, true);
         doNothing().when(delegate).verify(data);

         impl.verify(data);

         verify(delegate).verify(data);
     }
 }