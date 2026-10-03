TARGETS: WritableObjectId.writeAsId(...): id/reference writing, including first reference use.  
TARGETS: WritableObjectId.generateId(Object): generated-id storage and reuse.  
ORACLES: Bug 1255 trigger: deserialization must not throw UnresolvedForwardReference.  
ORACLES: Reference id and later "@id" definition must match (trigger shows 1 versus 2).  
CASES: Same object first written as a reference, then written as a full object.  
CASES: Repeated generateId calls for one POJO; initially unset id.  
RISKS: ObjectIdWriter configuration and exact JSON assertions are not provided.