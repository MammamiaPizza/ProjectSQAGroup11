TARGETS: BOBYQAOptimizer optimize path, especially constrained Rosenbrock with extra interpolation points.
ORACLES: Trigger test completion without BOBYQAOptimizer$PathIsExploredException; optimizer result from its objective run.
CASES: Constrained Rosenbrock using more interpolation points, matching the existing trigger configuration.
CASES: Verify returned point/value respects supplied bounds and optimization completes normally.
RISKS: Internal doOptimize/bobyqa details are protected/private; test through the public optimizer workflow.
RISKS: Context omits full optimize signature and trigger inputs; derive setup only from available test/source context.