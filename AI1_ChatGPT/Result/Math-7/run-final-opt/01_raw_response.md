TARGETS: AbstractIntegrator.acceptStep event ordering, event-time handling, and final-step/reset behavior.
ORACLES: EventHandler callbacks/actions and StepHandler calls define observable integration scheduling.
CASES: Multiple events within one accepted step; verify chronological scheduling in forward integration.
CASES: Events at/near step boundaries; verify no missed, duplicated, or wrongly ordered occurrence.
CASES: Coincident/close event times and remaining-event re-evaluation after each event.
CASES: Event actions causing reset/stop; verify subsequent state and last-step notification.
RISKS: AbstractIntegrator is protected/abstract; exercise via existing concrete integrator test paths.
RISKS: Only failing oracle supplied is DormandPrince853IntegratorTest.testEventsScheduling; no alternate version.