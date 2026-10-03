TARGETS: Reporter.safelyGetMockName(Object) and reporting paths that render mock names.  
TARGETS: no-more-interactions (including in-order) and injection-failure reporting.  
ORACLES: Trigger expectations require NoInteractionsWanted / VerificationInOrderFailure, not ClassCastException.  
ORACLES: Injection-failure reporting requires MockitoException, not NullPointerException.  
CASES: Mock with bogus default answer; verify zero/no-more interactions after an interaction.  
CASES: Same bogus mock in ordered verification; assert expected verification failure type.  
CASES: Bogus mock name rendering during injection failure; assert MockitoException type.  
RISKS: Expected message text and setup APIs are not fully provided; avoid asserting unspecified wording.