TARGETS: X7875_NewUnix local extra-field parsing and getLocalFileDataData() reserialization.
TARGETS: UID/GID decoding via getUID()/getGID(), including reset/default-state interaction.
ORACLES: Existing trigger testParseReparse reports expected 0 but receives 5 after parse/reparse.
ORACLES: Serialized local-data bytes and reported local-data length should remain consistent.
CASES: Parse then reserialize data containing zero-valued UID/GID components.
CASES: Normal UID/GID values set via setters, then serialize and reparse.
CASES: Boundary integer byte lengths, especially leading-zero trimming/minimum-length behavior.
RISKS: Parsing API/signature and exact wire-format expectations are not provided in this context.
