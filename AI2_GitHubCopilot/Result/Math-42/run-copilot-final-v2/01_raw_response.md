TARGETS: SimplexTableau.normalize() handling negative lower bound; createTableau() split vars.
ORACLES: testMath713NegativeVariable assertion expected solution point values.
CASES: one var lb<0; all vars lb<0; mixed signs; equality with neg lb; zero lb.
RISKS: no access to test source; only public SimplexTableau API visible; normalization may vary.