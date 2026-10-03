TARGETS: InlineFunctions.process; candidate discovery, reference checks, conflict resolution, expression decomposition.  
ORACLES: Existing trigger InlineFunctionsTest::testIssue423; its assertion is the only stated expected source.  
CASES: Named functions, function variables, and function expressions considered as inline candidates.  
CASES: Candidate usages versus non-candidate name usages; references requiring aliases or block inlining.  
CASES: Functions referencing this, containing inner functions, or with conflicting called-function dependencies.  
CASES: Minimum-requirement trimming and cost-based decisions for inline/remove eligibility.  
RISKS: Bug report supplies no input JavaScript, expected transformed output, or asserted condition.  
RISKS: Most relevant methods are private/internal; tests likely require compiler-pass integration rather than unit calls.