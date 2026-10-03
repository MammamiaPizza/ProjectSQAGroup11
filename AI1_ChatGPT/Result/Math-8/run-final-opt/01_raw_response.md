TARGETS: DiscreteDistribution.sample(int); array runtime component type and sampled element storage  
TARGETS: sample() supplies individual values; constructors retain provided sample pairs  
ORACLES: Existing trigger testIssue942 is the only stated expected-result source  
ORACLES: Trigger expectation: sample(int) must not throw ArrayStoreException for its configured samples  
CASES: Trigger-equivalent sample list with differing runtime value classes; positive sampleSize  
CASES: Homogeneous values with positive sampleSize; verify requested array length  
CASES: Boundary sampleSize=1 and invalid non-positive size expecting NotStrictlyPositiveException  
RISKS: Current allocation uses first singleton's runtime class, unsafe for later incompatible sampled values  
RISKS: No full trigger data or method bodies provided; do not assume array component-type semantics