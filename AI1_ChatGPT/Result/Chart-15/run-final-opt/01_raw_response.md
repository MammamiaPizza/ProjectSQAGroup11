TARGETS: PiePlot draw behavior when constructed with or set to a null PieDataset.  
ORACLES: Existing trigger PiePlot3DTests::testDrawWithNullDataset is the only stated expected-result source.  
CASES: Draw null dataset using a Graphics2D and valid plot/data areas; assert trigger-compatible nonfailure/result.  
CASES: Exercise PiePlot() default null dataset versus PiePlot(null) and setDataset(null), if draw is accessible.  
RISKS: Draw signature/body and expected rendering/exception semantics are truncated; do not infer image details.  
RISKS: PiePlot3DTests targets a 3D plot while modified class is PiePlot; applicability may be indirect.