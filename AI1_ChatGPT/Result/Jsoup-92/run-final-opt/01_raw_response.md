TARGETS: Attributes duplicate-key detection/removal; Token.Tag attribute finalisation and ParseSettings case handling.
TARGETS: HtmlTreeBuilder and XmlTreeBuilder must retain the intended parsed attributes on inserted elements.
ORACLES: Existing trigger assertions and their expected serialized HTML/XML attribute order and values.
CASES: HTML default parsing drops repeated case-insensitive keys, retaining the first occurrence.
CASES: Case-sensitive HTML retains keys differing only by case while dropping exact duplicates.
CASES: XML retains differently cased keys and drops duplicate keys without replacing earlier values.
RISKS: Parser/token internals are protected/package-private; test through public parser output where required.