TARGETS: FastMath.max(float,float), FastMath.min(float,float)
ORACLES: java.lang.Math.max/min(float,float); IEEE 754: -0.0 < +0.0; NaN yields NaN
CASES: (50f,-50f), (-10f,20f), (3f,5f), (-3f,-5f); zeros: (0f,0f), (-0f,0f), (0f,-0f); NaN+normal;
(+Inf,-Inf); (Float.MIN_VALUE, Float.MAX_VALUE); equal args
RISKS: Sign-bit handling may invert ordering for -0.0/+0.0; max may return smaller value; min may
have symmetric defect; double variants may share same code pattern