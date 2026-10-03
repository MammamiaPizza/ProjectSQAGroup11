TARGETS: Entities.escape w/ escapeMode for XML attrs; EscapeMode.getMap for xhtml; ensure '>' maps
to ">".
ORACLES: Trigger test expects ">" -> ">" in XML attr; HTML spec + Jsoup prior behavior: no escape
for ">" in HTML.
CASES: "a>b" in XML attr => "a>b"; ">" alone; ">>"; "a>b" in HTML attr => unchanged.
CASES: "<p>One</p>" in XML attr => "<p>One</p>"; interleaved "<", ">", "&".
CASES: "]]>" => "]]>" (avoid CDATA misinterpretation); "x]>y" => "x]>y".
CASES: "a&b>c" => "a&b>c" (no double-escaping); attribute delimiter " or ' present.
CASES: empty string; string with only '>'; long attribute with many ">".
RISKS: Over-escaping if already entity-escaped; must avoid double-encoding (e.g., ">" stays ">").
RISKS: Changing escape map could affect other output contexts (text nodes); ensure scope limited to
attributes.
RISKS: Regressions in non-XML modes; performance regression due to larger replacement map.