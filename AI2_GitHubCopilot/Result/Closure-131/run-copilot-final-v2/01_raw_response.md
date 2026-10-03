TARGETS: TokenStream.isKeyword(String); TokenStream.isJSIdentifier(String); token-keyword detection
for quoted property conversion.
ORACLES: Quoted-name conversion only when TokenStream treats key as a valid non-keyword JS
identifier; Compare ConvertToDottedPropertiesTest behavior against source keyword table in
TokenStream.
CASES: isKeyword("class"/"interface"/"public"/"protected"/"final" and other modifiers);
isKeyword(null/""/mixedcase/camelCase); isJSIdentifier keywords vs plain ids, digits, hyphens,
empty.
RISKS: Partial switch fall-through in provided snippet; identifiers sharing keyword prefixes;
expected results must come from Closure source only, not speculation.