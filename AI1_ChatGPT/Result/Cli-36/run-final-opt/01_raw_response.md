TARGETS: OptionGroup addOption/getNames/getOptions ordering; Options addOptionGroup and option lookup/group mapping.
ORACLES: Existing BugCLI266Test expects first inserted group option name "p", not later "x".
CASES: Add group options in p,x order; assert getNames and getOptions preserve insertion order.
CASES: Add group to Options; verify getOptionGroup for each option and getOptions encounter order.
CASES: Empty and single-option groups; repeated addOption behavior if observable.
RISKS: HashMap-backed OptionGroup makes iteration order unspecified; avoid assuming undocumented duplicate semantics.
RISKS: Context lacks Option constructors/details and broader expected ordering contract beyond trigger.