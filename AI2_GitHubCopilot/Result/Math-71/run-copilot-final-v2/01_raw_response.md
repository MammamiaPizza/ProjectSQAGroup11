TARGETS: RungeKuttaIntegrator.acceptStep call — must use dt=eventTime-stepStart not original h
ORACLES: Test expects final t == eventTime 1.8782503799999986E9; overshoot indicates wrong h used
CASES: testMissedEndEvent: end event at known time, integrator overshoots because acceptStep uses
full step
RISKS: Fix might break step-interpolation for event steps; must ensure rki.stepStart/stepSize
correct
TARGETS: EmbeddedRungeKuttaIntegrator event-detected branch; adjust step before calling super
CASES: Check event exactly at step start, within tolerance, just after step start
ORACLES: Event time obtained via StepHandler.reset(…), recorded; compare after integration
RISKS: Without seeing RungeKuttaIntegrator code, exact fix location uncertain