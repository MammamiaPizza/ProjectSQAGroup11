TARGETS: RecordType.getLeastSupertype(JSType); RecordType.isSubtype(JSType); static isSubtype(ObjectType, RecordType)
ORACLES: Exact expected union strings from JSTypeTest least-supertype triggers and RecordTypeTest.testSupAndInf
CASES: Records {a:number,b:string} and {b:string,c:string,e:number} yield their union
CASES: Records {a:number,b:string} and {d:number,e:string,f:string} yield their union
CASES: Records {a:number,b:number} and {b:number,c:number} yield their union
RISKS: Available context omits RecordType construction APIs and full subtype/equivalence semantics