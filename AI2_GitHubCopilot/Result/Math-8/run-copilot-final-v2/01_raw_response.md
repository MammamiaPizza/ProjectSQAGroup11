TARGETS: DiscreteDistribution.sample(int) when samples contain items of different subclasses of a
common T
ORACLES: No ArrayStoreException; returned array component type is T (not a subclass); each element
is instanceof T
CASES: 2+ distinct T subclasses in samples; sample(1); sample(5); sample(0) → empty array; negative
sampleSize → NotStrictlyPositiveException
RISKS: Only bug report & trigger failure available; no fixed code; must infer correct array creation
from spec, not from source