TARGETS: random(int,int,int,boolean,boolean) and random(int,int,int,boolean,boolean,char[])
ORACLES: LANG-807: IAE message for non-positive bound must contain "start" (likely for start>end)
CASES: start==end (IAE), start>end (core bug), start<0, end<start, count<0, count=0
RISKS: No patch visible; exact message format unknown; rely only on trigger test and summary