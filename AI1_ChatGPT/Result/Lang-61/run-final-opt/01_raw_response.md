TARGETS: StrBuilder.indexOf(String,int) and replace/replaceAll paths implicated by LANG-294 triggers.  
ORACLES: Existing trigger assertions: indexOf result must be -1, and replacement must not throw AIOOBE.  
CASES: Search a nonmatching string after a partial match near the buffer end; expect -1.  
CASES: Replace using the LANG-294 pattern/input boundary that previously reaches buffer end; verify resulting text.  
RISKS: Exact LANG-294 input/pattern and intended replacement output are absent from the provided context.