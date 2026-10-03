TARGETS: isCandidateFunction, trimCanidatesNotMeetingMinimumRequirements,
resolveInlineConflictsForFunction, decomposeExpressions, checkNameUsage
ORACLES: Expected inlining decisions derived from testIssue423 input/output (compiled JS strings)
CASES: Normal: single-call function; Boundary: recursive, uses this, has inner functions, called
from multiple modules; Error: function with blockInliningReferences, conflicting cross-module
references
RISKS: Incorrect inlining when function has inner functions or this; loop inlining due to cost
estimation; module boundaries ignored