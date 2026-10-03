TARGETS: WriteableCommandLineImpl.getOptions() output ordering and inclusion of group options
TARGETS: GroupImpl.getOptions()/getTriggers() enumeration of child options with parent display
TARGETS: OptionImpl.validate() checking required parent when child exists; findOption for nested
triggers
ORACLES: TestGetOptions_Order expected order: [--help (-?,-h)|login <username>] [<target1>
[<target2> …]]
ORACLES: Missing option exception message "Missing option parentOptions" when required parent absent
ORACLES: Maximum restriction failure for parent group when too many children provided
CASES: Normal: single child, multiple children, parent+child combos with various counts
CASES: Boundary: empty group, all options absent, exactly max, over max, missing required parent
CASES: Error: OptionException for missing parent; assertion for max violation; duplicate triggers
RISKS: Inferred behavior from bug symptoms only; no post-fix implementation details available; may
miss edge cases like nested groups