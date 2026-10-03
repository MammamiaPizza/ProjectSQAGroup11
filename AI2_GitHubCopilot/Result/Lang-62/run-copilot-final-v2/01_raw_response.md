TARGETS: Entities.unescape(String), Entities.unescape(Writer,String): handling numeric entities
exceeding Unicode max code point.
ORACLES: Per LANG-292 fix: numeric entities with value > U+10FFFF must remain literal, not be
truncated/converted to a wrong char.
CASES: Normal valid: &#60; -> <, &#x3C; -> <, &#9760; -> ☠; Boundary: &#1114111; (max code point) ->
􏿿, &#1114112; -> literal “&#1114112;”.
CASES: Overflow decimal: &#12345678; must stay literal (not 慎); hex overflow: &#xFFFFFFFF; ->
literal; negative/empty: &#-1; -> literal.
RISKS: Cannot see internal parse logic; possible overflow also in EntityMap.value storage, but
trigger test only covers unescape behavior.