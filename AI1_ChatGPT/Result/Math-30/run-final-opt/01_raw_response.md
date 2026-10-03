TARGETS: mannWhitneyU(double[],double[]) rank-sum U computation for large sample lengths  
TARGETS: mannWhitneyUTest(double[],double[]) normal-approximation p-value using U and variance  
ORACLES: Mann-Whitney U rank-sum formula; U is the smaller of the two sample U statistics  
ORACLES: Existing MannWhitneyUTestTest::testBigDataSet assertion is the available regression oracle  
CASES: Large equal-sized samples where length arithmetic can exceed int range in rank-offset calculation  
CASES: Large unequal sample sizes; verify U remains finite and within [0, n1*n2]  
CASES: Small normal samples, including ties, to retain rank handling and U-min behavior  
CASES: Invalid null, empty, or NaN-containing inputs through public methods  
RISKS: Integer overflow in products/sums derived from sample lengths can corrupt U and p-values  
RISKS: Context provides no concrete expected numeric values or full existing test contents