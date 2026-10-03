TARGETS: verify(), handleVerifyException(), canRecoverFromFailure(), constructors with/without Timer
ORACLES: delegate VerificationMode result; polling retry until success or duration elapsed;
AssertionError propagation
CASES: delegate passes immediately; delegate fails then passes within duration; delegate fails until
timeout; returnOnSuccess=true/false; zero duration; negative polling
RISKS: No source for delegate behavior; timing-dependent retry logic not fully specified; Timer API
unknown