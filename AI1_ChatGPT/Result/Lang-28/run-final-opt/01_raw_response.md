TARGETS: NumericEntityUnescaper.translate(CharSequence,int,Writer) numeric entity decoding
ORACLES: Existing trigger test testSupplementaryUnescaping expected supplementary character 𐰢
CASES: Decimal numeric entity for 𐰢; verify produced UTF-16 surrogate pair, not truncated BMP char
CASES: Hexadecimal numeric entity for supplementary code point, if accepted by current parser
CASES: BMP numeric entity remains decoded correctly
CASES: Non-entity/plain input leaves translation behavior unchanged
RISKS: Context provides only one trigger; option handling and malformed-entity behavior are unspecified