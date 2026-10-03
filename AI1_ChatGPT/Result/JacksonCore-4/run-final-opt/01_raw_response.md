TARGETS: TextBuffer.expandCurrentSegment(), expandCurrentSegment(int), finishCurrentSegment(), append()
ORACLES: size(), getCurrentSegmentSize(), contentsAsString()/contentsAsArray(); trigger expects growth past 262144
CASES: Fill current segment to 262144, then expand/append one more char; verify capacity and retained content
CASES: Normal expansion, minSize-driven expansion, and exact-boundary versus boundary-plus-one requests
RISKS: Only failure evidence is TestTextBuffer::testExpand; allocator/initial segment setup is not provided