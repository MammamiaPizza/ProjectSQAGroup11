TARGETS: LevenbergMarquardtOptimizer.doOptimize() convergence on nonlinear least-squares problems.
TARGETS: Tolerance and step-bound setters affecting termination and accepted parameter updates.
ORACLES: Existing MinpackTest JennrichSampson expected cost 0.2578199266368004.
ORACLES: Existing MinpackTest FreudensteinRoth expected cost 11.41300466147456.
CASES: Run both trigger problems through optimizer and compare final objective values.
CASES: Exercise normal convergence with default optimizer settings used by MinpackTest.
RISKS: Objective/model setup and exact assertion tolerances are not provided in this context.
RISKS: Private QR decomposition behavior is only testable indirectly through doOptimize().