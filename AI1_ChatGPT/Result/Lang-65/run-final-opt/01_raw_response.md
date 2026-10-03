TARGETS: DateUtils.truncate(Calendar,int), truncate(Date,int), and shared modify(Calendar,int,boolean).  
ORACLES: Existing trigger expects SECOND truncation to retain MDT offset at 2004-10-31 01:02:03.  
CASES: Calendar in DST fall-back overlap; truncate to SECOND with nonzero milliseconds.  
CASES: Verify resulting Calendar instant/zone offset, not only displayed local fields.  
CASES: Date overload for same instant, if source Calendar's overlap instant can be represented.  
CASES: Normal non-DST truncation to SECOND as regression control.  
RISKS: Ambiguous local time can resolve to MDT or MST depending on Calendar reconstruction behavior.  
RISKS: Context supplies only LANG-59 trigger; no broader expected behavior or patch details.