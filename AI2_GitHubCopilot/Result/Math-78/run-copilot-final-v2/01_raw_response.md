TARGETS: EventState.evaluateStep handling when g(t0) and g(t1) have same sign; root solving
fallback.
ORACLES: No MathRuntimeException; correct event time or no-event; stop/reset flags match expected
behaviour.
CASES: Normal sign change; positive-positive g values; negative-negative g values; exact zero at
t0/t1; near-equal values.
RISKS: Closed source of actual fix; unclear if skipping event is intended; BrentSolver may still
throw on no-bracketing.