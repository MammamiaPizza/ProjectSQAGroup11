TARGETS: compare(EventState,EventState) ordering; initIntegration event setup; occurringEvents
iteration+step handler calls
ORACLES: correct event order from DormandPrince853IntegratorTest.testEventsScheduling (implicit);
event times monotonic forward
CASES: no events; single event; distinct-time events; events at identical time; event at integration
start/end; many events
RISKS: orderingSign-based comparator may break transitivity; resetOccurred can skip events; step
size may overshoot; test details hidden