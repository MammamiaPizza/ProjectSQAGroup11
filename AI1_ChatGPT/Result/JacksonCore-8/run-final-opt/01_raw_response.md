TARGETS: TextBuffer.resetWithEmpty(), contentsAsString(), contentsAsArray(), getTextBuffer(), size()
ORACLES: Trigger TestTextBuffer::testEmpty identifies empty-buffer behavior; failure source is NPE
CASES: New buffer then resetWithEmpty(): access text/string/array/size without NullPointerException
CASES: Empty transitions after resetWithString, resetWithShared, resetWithCopy, and releaseBuffers
CASES: Append after empty reset; verify size and text accessors remain usable
RISKS: Internal segment/current-buffer state may be null after empty reset
RISKS: No source for exact empty string/array identity, allocation, or recycler behavior provided