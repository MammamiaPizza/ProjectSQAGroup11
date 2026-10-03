TARGETS: findWrapPos, printWrapped, renderOptions; text-wrap and line-break logic
ORACLES: No infinite loop when token exceeds width; RuntimeException thrown per CLI‑162
ORACLES: Wrap at last space ≤ width; indentation via nextLineTabStop; correct line count
CASES: Single token > width; token = width; token < width; multi‑word fits; empty/null text
CASES: width = 0, width negative; nextLineTabStop = 0; Unicode long token; leading/trailing spaces
RISKS: Cannot verify exact wrap index for token > width (break or exception unknown without fix)