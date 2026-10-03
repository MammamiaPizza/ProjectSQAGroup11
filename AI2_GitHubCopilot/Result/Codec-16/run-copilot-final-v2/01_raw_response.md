TARGETS: Base32(byte pad), Base32(boolean,byte), Base32(int,byte[],boolean,byte), encode, decode,
isInAlphabet
ORACLES: IllegalArgumentException only if pad is in Base32 alphabet (A-Z,2-7) or is whitespace char
ORACLES: Non-alphabet, non-whitespace pads (including default '=') must allow encode/decode without
exception
ORACLES: Round-trip encode→decode of any byte[] must produce original input regardless of pad choice
CASES: Default pad '=' (no exception); non-default pad e.g., '*', '~', '@'
CASES: Invalid pads: 'A' (alphabet), '2' (alphabet), ' ' (space), '\t' (tab), '\n' (newline)
CASES: Boundary pads: 0x00, 0xFF — expected accepted if not in alphabet or whitespace
RISKS: The failing testCodec200 uses a specific pad that was incorrectly rejected; reproduce that
exact scenario
RISKS: Behavior may depend on parent BaseNCodec; ensure Base32.isInAlphabet only checks octet
against static alphabet table