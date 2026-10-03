TARGETS: TextBuffer.expandCurrentSegment(); TextBuffer.expand(); buf(int needed);
unshare/ensureNotShared
ORACLES: segment length >= requested minSize; contents unchanged; char[] instance valid
CASES: expand 262145 from 262144; grow 64k+n boundaries; minSize>current; force shared->unshare then
expand
RISKS: bug at 262144->262145 overshoot; unclear expected growth formula from summary; internal
buffer/segment state interplay