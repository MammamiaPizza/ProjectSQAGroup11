TARGETS: Line.revert(), especially reversed direction and recomputed origin/zero precision  
ORACLES: Existing LineTest::testRevert assertion values and Line public getters/conversions  
CASES: Revert a non-axis-aligned line; verify direction is reversed and geometric points remain on line  
CASES: Compare pointAt/getAbscissa and toSpace/toSubSpace before versus after revert  
CASES: Use coordinates exposing the reported first-component precision mismatch  
RISKS: Exact/near-exact floating-point assertions may fail from origin recomputation rounding  
RISKS: Context omits test inputs and intended tolerance/precision policy