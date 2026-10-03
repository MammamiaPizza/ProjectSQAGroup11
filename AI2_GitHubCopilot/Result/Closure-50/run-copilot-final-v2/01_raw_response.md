TARGETS: tryFoldArrayJoin, tryFoldKnownStringMethods, tryFoldKnownMethods
ORACLES: assertEqual AST string after optimization vs expected normalized JS source
CASES: Array.join('')+string, string+Array.join(''), no-join fallback, empty array
RISKS: Bug likely in join folding when separator non-empty or in addition chains
CASES: boundary: null/undefined array, single-element array, nested join calls
RISKS: tryFoldArrayJoin may incorrectly fold or skip valid optimization patterns