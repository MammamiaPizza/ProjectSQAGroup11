TARGETS: TimeSeries.createCopy(int,int) and createCopy(RegularTimePeriod,RegularTimePeriod) item-range copying.  
ORACLES: Trigger testCreateCopy3 expects copied value 101.0, not 102.0.  
ORACLES: Copy item count, periods, values, and source-series contents provide observable results.  
CASES: Copy a middle inclusive index range; verify first/last copied entries and source remains unchanged.  
CASES: Copy by matching start/end periods; compare values/order with index-range copy.  
CASES: Boundary ranges: single item, first-to-last, and adjacent indices/periods.  
RISKS: Exact intended behavior for invalid indices, absent periods, and empty ranges is not provided.  
RISKS: Bug report is UNKNOWN; only failing-copy evidence is available.