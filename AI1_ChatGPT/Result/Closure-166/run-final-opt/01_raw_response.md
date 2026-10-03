TARGETS: PrototypeObjectType.matchRecordTypeConstraint(ObjectType) record-property inference  
TARGETS: getSlot/getPropertyType/getOwnPropertyNames expose inferred record properties  
ORACLES: Trigger expectations: inferred records contain a:boolean|undefined, b:string|undefined, prop:string|undefined  
CASES: Apply record constraints with one property and multiple properties; verify properties are retained/inferred  
CASES: Verify resulting property names/types through public ObjectType property queries  
RISKS: Property declaration/inference and prototype-chain behavior may affect visibility of inferred slots  
RISKS: Context lacks constructor/setup APIs for creating types and constraints; reuse existing test infrastructure