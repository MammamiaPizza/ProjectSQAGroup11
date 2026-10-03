TARGETS: addPropertyCreator, verifyNonDup, constructValueInstantiator property-based path
ORACLES: Expected no "Could not find creator property with name 'intField'" error; trigger test
expects successful deserialization
CASES: Duplicate property names via verifyNonDup; explicit vs implicit creators; mixed
delegating/property creators; missing creator property
RISKS: Cannot inspect SimplePojo or test class internals; only public API signatures; no spec for
verifyNonDup behavior on duplicates