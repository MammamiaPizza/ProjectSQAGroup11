TARGETS: createCopy(RegularTimePeriod,RegularTimePeriod) end-boundary handling; createCopy(int,int)
index off-by-one; minY/maxY after copy.
ORACLES: testCreateCopy3 expected maxY=101 vs 102; copied series must contain exactly items in
period range inclusive; copy bounds update.
CASES: empty source; copy single-item; copy full range; start=end; copy before/after data; copy
1st/last; partial overlap; varied period classes.
RISKS: same off-by-one may affect delete(start,end,notify), getValue(index), hashCode; limited to
supplied API (no source), cannot inspect impl.