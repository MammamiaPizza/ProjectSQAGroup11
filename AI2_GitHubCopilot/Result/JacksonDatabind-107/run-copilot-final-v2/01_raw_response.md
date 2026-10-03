TARGETS: _handleUnknownTypeId(ctxt,typeId), _handleMissingTypeId(ctxt,extraDesc)
ORACLES: Bug report: no NullPointerException when DeserializationProblemHandler is registered
CASES: Unknown typeId with handler, missing typeId, null typeId, empty typeId, known typeId fallback
RISKS: Only partial API visible; cannot see subclass behavior; fix may involve synchronization
changes