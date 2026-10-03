TARGETS: TreeBuilderState.process(Token, TreeBuilder), especially data-only tag handling and token consumption.
TARGETS: TreeBuilderState.handleRcData and handleRawtext state transitions.
ORACLES: ParserTest::handlesDataOnlyTags expects text output "Hello There".
ORACLES: Script-like content must not appear as parsed document text when handled as data-only content.
CASES: Normal data-only tag content between visible text: preserve surrounding "Hello" and "There".
CASES: Boundary: data-only content containing markup-like characters or JavaScript punctuation.
CASES: Error: closing-token/state handling must not leak data-only content into subsequent text.
RISKS: Only failing assertion and TreeBuilderState signatures are provided; exact tag/input coverage is unavailable.