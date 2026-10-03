TARGETS: DocumentType constructor must accept empty/blank name without throwing.
ORACLES: nodeName() returns the passed name (including empty); no IllegalArgumentException.
CASES: name="" (empty), name=" " (blank), name=null (if allowed), name=valid with empty publicId.
RISKS: Buggy version throws on empty; fix must not re-introduce validation for name while keeping
other validations.
TRIGGERS: constructorValidationOkWithBlankName, handlesInvalidDoctypes (both expect no exception).