package org.apache.commons.math.ode.events;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.apache.commons.math.ode.DerivativeException;
 import org.apache.commons.math.ode.sampling.StepInterpolator;
 import org.junit.Test;

 /**
  * Tests for {@link EventState} focusing on the MATH-322 bug where
  * evaluateStep throws MathRuntimeException when g(t0) and g(t1)
  * have the same sign (no bracketing for BrentSolver).
  */
 public class EventStateTest {

     /* ---- Minimal StepInterpolator stub ---- */
     private static class StubStepInterpolator implements StepInterpolator {
         private final double t0;
         private final double t1;
         private final double[] y;
         private final boolean forward;
         private double interpTime;

         StubStepInterpolator(double t0, double t1, double[] y, boolean forward) {
             this.t0 = t0;
             this.t1 = t1;
             this.y = (y == null ? new double[] {0.0} : y.clone());
             this.forward = forward;
             this.interpTime = t0;
         }

         public double getPreviousTime() { return t0; }
         public double getCurrentTime()  { return t1; }
         public double getInterpolatedTime() { return interpTime; }
         public void   setInterpolatedTime(double time) { this.interpTime = time; }
         public double[] getInterpolatedState() throws DerivativeException { return y; }
         public double[] getInterpolatedDerivatives() throws DerivativeException { return new
double[y.length]; }
         public boolean isForward() { return forward; }
         public StepInterpolator copy() throws DerivativeException { return this; }
         public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {}
         public void readExternal(java.io.ObjectInput in) throws java.io.IOException,
ClassNotFoundException {}
     }

     /* ---- Minimal EventHandler that delegates g(t,y) to a simple function of t ---- */
     private interface GFunction {
         double g(double t);
     }

     private static class StubEventHandler implements EventHandler {
         private final GFunction gFunc;
         int eventCount;
         double lastEventTime = Double.NaN;
         boolean lastIncreasing;
         int nextAction = EventHandler.CONTINUE;

         StubEventHandler(GFunction gf) { this.gFunc = gf; }

         public double g(double t, double[] y) { return gFunc.g(t); }
         public int eventOccurred(double t, double[] y, boolean increasing) {
             eventCount++;
             lastEventTime = t;
             lastIncreasing = increasing;
             return nextAction;
         }
         public void resetState(double t, double[] y) {}
     }

     /* ---- Helper ---- */
     private EventState createEventState(SubEventHandler h, double maxCheck, double conv, int
maxIter) {
         return new EventState(h, maxCheck, conv, maxIter);
     }

     private static double[] Y = new double[] {0.0};

     // ================================================================
     // 1. Normal sign change: positive to negative
     // ================================================================
     @Test
     public void testSignChangePositiveToNegative() throws Exception {
         // g(t) = 10 - t; from 0→20 g goes 10→-10, root at 10
         StubEventHandler h = new StubEventHandler(t -> 10.0 - t);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 20.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertTrue("event expected", found);
         assertEquals("root", 10.0, es.getEventTime(), 1e-8);
         assertEquals("handler called", 1, h.eventCount);
     }

     // ================================================================
     // 2. Normal sign change: negative to positive
     // ================================================================
     @Test
     public void testSignChangeNegativeToPositive() throws Exception {
         // g(t) = t - 7; from 0→20 g goes -7→13, root at 7
         StubEventHandler h = new StubEventHandler(t -> t - 7.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 20.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertTrue("event expected", found);
         assertEquals("root", 7.0, es.getEventTime(), 1e-8);
     }

     // ================================================================
     // 3. Both positive: no sign change → no event, no exception
     // ================================================================
     @Test
     public void testBothPositiveNoEvent() throws Exception {
         // g(t) = t + 1; always positive
         StubEventHandler h = new StubEventHandler(t -> t + 1.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertFalse("no event", found);
         assertEquals("handler never called", 0, h.eventCount);
     }

     // ================================================================
     // 4. Both negative: no sign change → no event, no exception
     // ================================================================
     @Test
     public void testBothNegativeNoEvent() throws Exception {
         // g(t) = -t - 5; always negative
         StubEventHandler h = new StubEventHandler(t -> -t - 5.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertFalse("no event", found);
         assertEquals("handler never called", 0, h.eventCount);
     }

     // ================================================================
     // 5. g0 == 0 at start, both non-negative → no sign change
     // ================================================================
     @Test
     public void testZeroAtStart() throws Exception {
         // g(t) = t; g(0)=0, g(10)=10; g0Positive=true, gb>=0=true → no crossing
         StubEventHandler h = new StubEventHandler(t -> t);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertFalse("g0=0 and g stays positive → no sign-change event", found);
     }

     // ================================================================
     // 6. g(t1) == 0 at end, same sign as g0 → no event
     // ================================================================
     @Test
     public void testZeroAtEnd() throws Exception {
         // g(t) = -t + 10; g(0)=10, g(10)=0; both non-negative
         StubEventHandler h = new StubEventHandler(t -> -t + 10.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertFalse("no event when endpoint zero has same sign", found);
     }

     // ================================================================
     // 7. Close-events: two crossings in one step, after first event
     //    remaining interval has same-sign endpoints (MATH-322 trigger)
     // ================================================================
     @Test
     public void testCloseEventsSameSignRemaining() throws Exception {
         // Parabola g(t) = -(t-3)*(t-7) = -t^2+10t-21
         // Roots at 3 and 7.  g(0)=-21 <0, g(3)=0, g(5)=4 >0, g(7)=0, g(10)=-21 <0
         // After the event at t=3 (neg→pos), the interval [3,10] has g(3)=0, g(10)=-21
         // i.e. non-negative → negative: a sign change, so event at 7 should be found.
         // The MATH-322 bug involves the part AFTER both events are processed:
         // from t=7 onward, both g(7)=0 and g(10)=-21 are non-positive,
         // i.e. same sign, yet the buggy code invokes BrentSolver → exception.
         StubEventHandler h = new StubEventHandler(t -> -(t - 3.0) * (t - 7.0));
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);

         // First event at t≈3
         boolean found1 = es.evaluateStep(ip);
         assertTrue("first event found", found1);
         double t1 = es.getEventTime();
         assertEquals(3.0, t1, 1e-8);

         // Accept and reinitialise
         es.stepAccepted(t1, Y);
         es.reinitializeBegin(t1, Y);

         // Second event at t≈7
         StubStepInterpolator ip2 = new StubStepInterpolator(t1, 10.0, Y, true);
         boolean found2 = es.evaluateStep(ip2);
         assertTrue("second event found", found2);
         double t2 = es.getEventTime();
         assertEquals(7.0, t2, 1e-8);

         // Accept and reinitialise — now [7,10] has same sign (g=0 and g=-21)
         es.stepAccepted(t2, Y);
         es.reinitializeBegin(t2, Y);

         // Bug: evaluateStep on [7,10] must NOT throw, should find no event
         StubStepInterpolator ip3 = new StubStepInterpolator(t2, 10.0, Y, true);
         boolean found3 = es.evaluateStep(ip3);
         assertFalse("no event after second crossing (same-sign endpoints)", found3);
     }

     // ================================================================
     // 8. Same-sign endpoints after a single event (no second crossing)
     // ================================================================
     @Test
     public void testSameSignAfterSingleEvent() throws Exception {
         // g(t) = t - 2; crosses at 2. After event, g is positive.
         StubEventHandler h = new StubEventHandler(t -> t - 2.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 5.0, Y, true);

         boolean found = es.evaluateStep(ip);
         assertTrue(found);
         double evt = es.getEventTime();
         es.stepAccepted(evt, Y);
         es.reinitializeBegin(evt, Y);

         // [2,5]: g(2)=0, g(5)=3 — both non-negative, no sign change
         StubStepInterpolator ip2 = new StubStepInterpolator(evt, 5.0, Y, true);
         boolean found2 = es.evaluateStep(ip2);
         assertFalse("same-sign endpoints after event must not throw", found2);
     }

     // ================================================================
     // 9. Near-zero same-sign g values (close but same sign)
     // ================================================================
     @Test
     public void testNearZeroSameSign() throws Exception {
         // g(t) = 1e-14 (constant tiny positive)
         StubEventHandler h = new StubEventHandler(t -> 1e-14);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertFalse("no event for near-zero constant same sign", found);
     }

     // ================================================================
     // 10. Constant negative → no event, no exception
     // ================================================================
     @Test
     public void testConstantNegative() throws Exception {
         StubEventHandler h = new StubEventHandler(t -> -7.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 10.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertFalse("no event for constant negative", found);
     }

     // ================================================================
     // 11. Backward integration (t1 < t0) with sign change
     // ================================================================
     @Test
     public void testBackwardIntegration() throws Exception {
         // g(t)=t-5; backward 10→0; root still at 5
         StubEventHandler h = new StubEventHandler(t -> t - 5.0);
         EventState es = new EventState(h, 100.0, 1e-12, 100);
         es.reinitializeBegin(10.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(10.0, 0.0, Y, false);
         boolean found = es.evaluateStep(ip);
         assertTrue("event in backward integration", found);
         assertEquals(5.0, es.getEventTime(), 1e-8);
     }

     // ================================================================
     // 12. Large step with maxCheckInterval subdivision; sign change
     //     in a middle sub-interval
     // ================================================================
     @Test
     public void testSubdivisionSignChange() throws Exception {
         // g(t)=t-50; step 0→100, maxCheck=30 → subdivided.
         // Only the second sub-interval [~33,~67] contains the root at 50.
         StubEventHandler h = new StubEventHandler(t -> t - 50.0);
         EventState es = new EventState(h, 30.0, 1e-12, 100);
         es.reinitializeBegin(0.0, Y);
         StubStepInterpolator ip = new StubStepInterpolator(0.0, 100.0, Y, true);
         boolean found = es.evaluateStep(ip);
         assertTrue("event in subdivided step", found);
         assertEquals(50.0, es.getEventTime(), 1e-8);
     }
 }