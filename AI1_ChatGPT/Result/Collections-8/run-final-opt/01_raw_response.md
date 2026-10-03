TARGETS: UnboundedFifoBuffer size, isEmpty, add, get, remove, iterator, serialization read/write behavior  
ORACLES: Trigger testCollections220 expects 1 but observes 0; use public API state/results  
CASES: Add one element; verify size/isEmpty, get without removal, then remove returns that element  
CASES: Serialize/deserialize a buffer containing one element; verify restored size and element access  
CASES: Iterate restored/nonempty buffer; verify next yields queued element and iteration count matches size  
RISKS: Exact serialization format and exception behavior are not provided; avoid assumptions beyond observable API