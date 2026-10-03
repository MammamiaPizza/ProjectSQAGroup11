TARGETS: GJChronology cutover fields: year and weekyear add/set conversion across Julian/Gregorian cutover  
ORACLES: Trigger assertions; expected -0002-06-30 for positive-to-negative year/weekyear additions  
ORACLES: No exception when addition crosses into year zero; year zero must not be set  
CASES: Pre-zero date behavior around cutover (test_cutoverPreZero)  
CASES: Add years from positive year to zero and negative year across cutover  
CASES: Add weekyears from positive year to zero and negative year across cutover  
CASES: Verify resulting dates for positive-to-negative transitions, including -0002-06-30  
RISKS: Gregorian/Julian conversion may preserve wrong day or map through unsupported year 0  
RISKS: Exact test_cutoverPreZero expected value is not provided in this context