TARGETS: Complex.tan() and Complex.tanh() for infinite real/imaginary parts
ORACLES: Standard identities: tanh(∞)=1, tanh(-∞)=-1, tan(i∞)=i, tan(-i∞)=-i
CASES: tanh(∞+0i) ⇒ real=1 imag=0; tanh(-∞+0i) ⇒ real=-1 imag=0
CASES: tan(0+i∞) ⇒ real=0 imag=1; tan(0-i∞) ⇒ real=0 imag=-1
CASES: tan(∞+0i) (mathematically undefined) should not be NaN via direct formula
CASES: Zero real, very large imaginary: tan(0+1e16i) should approach i, not NaN
CASES: NaN propagation: tan(NaN+i) → NaN; tan(0+NaN) → NaN
CASES: Finite near-pole: tan(π/2+0i) should be large imaginary (approx ∞/NaN?)
RISKS: No fix version available; expected for mixed infinities must be inferred
RISKS: Floating-point overflow in sinh/cosh may cause intermediate NaN for large finite inputs