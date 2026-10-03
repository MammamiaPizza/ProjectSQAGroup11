TARGETS: Parser.parse(String,baseUri); parseBodyFragment; parseBodyFragmentRelaxed -> Document.
TARGETS: inner parse() token loop; parseStartTag(); parseAttribute() for attr name/value.
TARGETS: parseAttribute() must handle quoted/unquoted and rough attribute endings without index
errors.
ORACLES: ParserTest#parsesQuiteRoughAttributes expects a returned Document and no
StringIndexOutOfBoundsException.
ORACLES: Summary gives only exception failure signal; expected DOM values are not specified.
CASES: normal attr a=b, quoted a="b c", empty value a=, single-char trailing value.
CASES: rough/error: unterminated quote, trailing "=" or punctuation at end, ">" inside value.
CASES: boundary: attribute value ending exactly at html length; repeated rough attrs;
whitespace-only.
RISKS: Exact ParserTest input unknown; index=14 suggests a specific literal, only approximate
reproduction.
RISKS: Private methods unreachable directly; drive tests only via public parse entry points.