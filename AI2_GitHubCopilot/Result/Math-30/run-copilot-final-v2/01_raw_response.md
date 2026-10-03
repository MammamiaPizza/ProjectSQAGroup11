TARGETS: mannWhitneyU – U1 = sumRankX - (x.length*(x.length+1))/2; VarU = n1n2prod*(n1+n2+1)/12.0;
mannWhitneyUTest uses it for normal approx.
ORACLES: Expected p‑values from large‑N testBigDataSet (repr. MATH‑790); manual computation with
long arithmetic; known statistical tables.
CASES: Large equal samples (n≥46341 triggers overflow); tiny samples (2 vs 2); all ties; all x›y;
empty/null inputs (ensureDataConformance).
RISKS: int overflow in (x.length*(x.length+1)) for big n; Umin sign flip if U1 negative; VarU
division order may truncate; normal approx unstable for small N.