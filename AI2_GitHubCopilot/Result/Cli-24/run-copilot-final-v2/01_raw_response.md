TARGETS: renderOptions, printWrapped, findWrapPos handling indented long-line chunking
ORACLES: No IllegalStateException; description wraps correctly with leftPad+descPad applied
CASES: Normal: short desc fits. Boundary: desc length = width-leftPad-descPad (exact fit)
CASES: Edge: desc length > width-leftPad-descPad triggers "no room" error path
CASES: Extreme: width ≤ leftPad+descPad; very small/large padding; zero/negative width
CASES: Multiple options, OptionGroup, varied descPad and leftPad per option
RISKS: Only API signatures available; no method bodies; exact fix location inferred from trigger
RISKS: Trigger test alone may not cover all chunking/indent paths; wrap logic depends on findWrapPos