TARGETS: releaseReadIOBuffer(byte[]), releaseTokenBuffer(char[]), _verifyRelease byte/char overloads
TARGETS: allocReadIOBuffer(int), allocTokenBuffer(int) return sizes feeding release checks
ORACLES: TestIOContext.testAllocations expects IAE containing "smaller than original"; current wrong
message lacks it
CASES: release exact allocated buffer -> no exception; shorter array -> IAE "smaller than original"
CASES: release null -> exception (exact type unspecified); same-size different array -> ownership
IAE
CASES: boundary minSize 0, 1, exact size; one element shorter triggers size rejection before
ownership
RISKS: only one trigger known; full message/order for base64/concat/nameCopy not provided
RISKS: minSize/zero-size return values unverified; do not assume allocated length