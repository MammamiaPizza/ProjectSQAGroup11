Copilot BYOK v2
- Keeps AI2 user prompt templates unchanged.
- Uses a clean temporary custom Copilot agent with tools: [] and strict prompt-only behavior.
- Keeps session-wide tool allowlist empty and disables built-in MCP/custom instructions.
- Does NOT retry tool-call-only model outputs; failures remain measurable model outputs.
- Pilot run id: copilot-pilot-v2
- Final run id: copilot-final-v2 (fresh namespace; does not mix with first failed pilot).
