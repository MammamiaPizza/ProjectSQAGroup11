TARGETS: FunctionToBlockMutator.makeLocalNamesUnique with labels;
RenameLabels.ProcessLabels.visitLabel/visitBreakOrContinue
ORACLES: Inlined function with same label name as caller scope must have renamed labels;
break/continue must target renamed label
CASES: Inline function with label matching outer label (conflict); break inside inlined body; nested
labeled blocks; loop-in-body labels
RISKS: AssertionFailedError suggests label renaming incomplete; makeLocalNamesUnique may not invoke
RenameLabels for all label-bearing nodes