TARGETS: MakeDeclaredNamesUnique.ContextualRenamer.getReplacementName renaming logic
ORACLES: assertEquals on compiled output strings in *Test classes (expected=inline/rename outcome)
CASES: nested functions with same-named locals; inlined functions reusing parameter/variable names;
loop-body renames
RISKS: no diff access to pre/post-bug source; Renamer subclasses InlineRenamer/BoilerplateRenamer
have no listed methods