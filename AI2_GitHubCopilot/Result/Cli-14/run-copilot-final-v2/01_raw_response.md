TARGETS: GroupImpl.validate for argument handling, anonymous argument processing, ClassCast on File
arg
ORACLES: Expected: no ClassCastException; file arguments should not be cast to String; BugCLI144Test
passes
CASES: Normal: valid File arg, valid String arg; Boundary: null/missing arg; Error:
ClassCastException when File returned
RISKS: Limited to given fragments; unclear if fix removes cast or type-checks; may affect other arg
handling in GroupImpl