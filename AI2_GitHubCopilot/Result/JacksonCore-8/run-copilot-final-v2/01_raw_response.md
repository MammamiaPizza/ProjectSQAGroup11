TARGETS: TextBuffer.contentsAsString(), getTextBuffer(), getTextOffset(), size(),
hasTextAsCharacters() after resetWithEmpty
ORACLES: NPE from contentsAsString() after resetWithEmpty triggers testEmpty failure; expect empty
string "" not null
CASES: resetWithEmpty(), then each accessor; resetWithShared(null,0,0); resetWithCopy(null,0,0);
resetWithString(null); empty buffer reads
RISKS: Unclear if NPE is in _currentSegment, _resultArray, or _inputBuffer; no spec for null-arg
behavior of resetWith* methods