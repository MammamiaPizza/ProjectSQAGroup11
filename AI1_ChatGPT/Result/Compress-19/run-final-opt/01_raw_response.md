TARGETS: Zip64 extra-field central/local data lengths, data getters, parsing, and size-related setters/getters.
ORACLES: Trigger requires central-directory ZIP64 data length to match required data (expected 16, not 28).
CASES: Read ZIP64 central data needing only size and compressed size, with excess 28-byte extra-field data.
CASES: Exercise local parsing at 0, 16, 24, and 28 bytes; include undersized nonzero input.
CASES: Verify length/data consistency after setting size, compressed size, header offset, and disk start.
RISKS: Central-directory parse/reparse API is not listed; excess-data handling is only evidenced by the trigger.