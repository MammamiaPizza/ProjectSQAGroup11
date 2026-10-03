TARGETS: AbstractIntegrator.integrate(), sanityChecks(), EventState, step handlers, event-time
reinit logic, step direction sign via interpolator.isForward()
ORACLES: Javadoc: integrate() must not reverse time unless integrating backward; stepStart/stepSize
consistent with direction
CASES: Forward integration with event causing step reject; backward integration; stepSize<0;
multiple events at same t; step near end time
RISKS: Only buggy code available; event handler/step handler impl unknown; dependent on concrete
ODE; cannot inspect test Issue695 source directly