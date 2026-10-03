TARGETS: constructor validation (population>0, 0<=successes<=population, 0<sample<=population);
getDomain/getLowerDomain/getUpperDomain; getSupportLowerBound/UpperBound.
TARGETS: probability(x), cumulativeProbability(x), upperCumulativeProbability(x); inherited sample()
returning within support.
ORACLES: support for Hypergeometric(N,m,k) = [max(0, k-(N-m)), min(k,m)]; formulas
C(m,x)*C(N-m,k-x)/C(N,k).
ORACLES: cumulativeProbability monotone nondecreasing, equals sum probability(i) from lower to x;
upperCumulative(x)=1-cumulative(x-1).
ORACLES: probability = 0 outside support; cumulative/survival saturate at 0/1 outside [lower,
upper].
CASES: N=10,m=3,k=5 support [0,3]; m=0 support {0}; m=N support {k}; k=N support {m}.
CASES: boundary x=lower-1, lower, upper, upper+1; repeated sample() must never yield sample < lower
or > upper (bug showed sample=-50).
CASES: invalid params: N<=0, m<0, m>N, k<=0, k>N should throw documented exceptions.
RISKS: getDomain is private and sample() lives in AbstractIntegerDistribution; only observable via
public support/probability/sample.
RISKS: no supplied expected values; rely on known distribution formulas, not another program
version.