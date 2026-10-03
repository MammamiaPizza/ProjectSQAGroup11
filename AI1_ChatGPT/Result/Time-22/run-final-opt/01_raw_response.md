TARGETS: BasePeriod(long,long,PeriodType,Chronology) fixed-zone decomposition of elapsed instants  
TARGETS: BasePeriod(long,PeriodType,Chronology) duration-to-period conversion with fixed-zone chronology  
ORACLES: Trigger assertions expect field value 0; buggy conversion produces 64  
CASES: Duration.toPeriod with fixed zone should yield zero for the trigger’s asserted field  
CASES: Period(long, fixed-zone chronology) should yield zero for the trigger’s asserted field  
CASES: Compare equivalent long-duration and instant-range construction under the same fixed zone  
RISKS: Only trigger failure values are supplied; the affected field and exact instants are unspecified  
RISKS: Do not infer behavior for variable zones, partials, nulls, or unsupported PeriodType fields