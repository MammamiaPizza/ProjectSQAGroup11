TARGETS: LevenbergMarquardtOptimizer.doOptimize() least-squares convergence behavior  
TARGETS: Tolerance and initial-step setters may affect optimizer termination/path  
ORACLES: Existing MinpackTest JennrichSampson expected cost 0.2578330049  
ORACLES: Existing MinpackTest FreudensteinRoth expected cost 11.4121122022341  
CASES: Run both triggering Minpack problems and assert their established expected costs  
CASES: Exercise default optimizer configuration used by existing Minpack tests  
RISKS: Small numerical differences can alter convergence result and assertion precision  
RISKS: qrDecomposition/qTy are private; test through public optimization behavior only  
RISKS: Context lacks modified-code details and full problem-function setup