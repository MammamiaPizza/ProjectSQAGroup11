TARGETS: DataUtil.parseByteData BOM stripping logic (also load entry points)
ORACLES: Content "One" after removing UTF-8 BOM; compare parsed Document body text
CASES: BOM+text, no BOM, BOM-only, multi-byte char after BOM, BOM+empty, BOM+non-ASCII, charset spec
in meta
RISKS: Buffer position advance may discard first real character together with BOM; charset detection
could misparse