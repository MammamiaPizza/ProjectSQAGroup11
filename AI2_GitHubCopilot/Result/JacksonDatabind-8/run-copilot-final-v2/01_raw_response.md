TARGETS: addStringCreator(AnnotatedWithParams,boolean),
verifyNonDup(AnnotatedWithParams,int,boolean)
ORACLES: No JsonMappingException when adding identical String creator a second time;
constructValueInstantiator returns valid instantiator
CASES: addStringCreator twice with identical AnnotatedWithParams for StringBuilder (explicit=true)
-> no conflict error
CASES: addStringCreator with two different constructors for same type -> expect JsonMappingException
"Conflicting String creators"
RISKS: Correct behavior (skip identical duplicates) only inferred from bug report; exact fix unknown