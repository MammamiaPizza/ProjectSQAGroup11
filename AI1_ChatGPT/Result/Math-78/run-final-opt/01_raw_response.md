TARGETS: EventState.evaluateStep event bracketing across maxCheckInterval subintervals  
TARGETS: reinitializeBegin, getEventTime, stepAccepted, stop, reset behavior around detected events  
ORACLES: EventStateTest::closeEvents and reported endpoint-sign solver failure  
CASES: Close successive roots within one interpolated step; ensure no invalid same-sign solver bracket  
CASES: Forward and backward interpolation; subinterval boundaries near convergence tolerance  
CASES: No sign change, exact-zero handler values, and multiple maxCheckInterval partitions  
RISKS: Expected event timing/action semantics are not fully specified by the provided context  
RISKS: Avoid relying on another version; available interpolator/EventHandler setup details are absent