{"TARGETS": "OptionGroup.getSelected() returning null for long-only options;
OptionGroup.setSelected() internal state.", "ORACLES": "Trigger tests expect getSelected() equals
'bar' after parsing group with --bar; should not be null.", "CASES": "Long-only option (--bar);
short-only (-b); mixed group; getSelected() after parse; AlreadySelectedException on re-select;
required group.", "RISKS": "Only OptionGroup class visible; parser calls to setSelected() unseen;
multiple parsers affected."}