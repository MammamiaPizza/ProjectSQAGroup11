TARGETS: Matchers.eq(int), Matchers.eq(Integer), Matchers.same(Integer)
ORACLES: Method returns normally (no NullPointerException)
CASES: int literal to eq(int); Integer(5) to eq(Integer)/same; null Integer to eq/same
RISKS: No internal matcher verification; only verify absence of NPE