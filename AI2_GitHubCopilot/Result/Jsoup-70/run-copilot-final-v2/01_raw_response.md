TARGETS: Element.html() — parse/serialize inner HTML for <pre> tags
ORACLES: testKeepsPreTextAtDepth asserts newlines/spaces are preserved in <pre> <code> blocks
CASES: <pre><code> with nested text at depth; leading/trailing whitespace; empty <pre>; <pre> with
only newlines
RISKS: limited to a single reported test; whitespace normalization may differ between parsing and
output