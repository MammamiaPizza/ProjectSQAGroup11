TARGETS: LookupTranslator.translate(CharSequence,int,Writer) - codepoint consumption return value
ORACLES: translate() must return the length of the longest matching lookup key consumed, or 0 if no
match
ORACLES: Compare translate() return value against expected key length (from lookup table entries)
CASES: input contains a lookup-key substring at index; verify returned length equals key length
CASES: shortest key is 1 char; verify 1 returned when matched at boundary position
CASES: multiple keys share a prefix (e.g., "ab", "abc"); longest match must be consumed
CASES: no key matches at index; verify translate() returns 0 and writes nothing
CASES: input shorter than all keys; verify no false match and return 0
RISKS: Bug is translate() always returning 0 regardless of match length — verify return value