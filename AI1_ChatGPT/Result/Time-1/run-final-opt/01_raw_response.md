TARGETS: Partial(DateTimeFieldType[],int[],Chronology) validation of field types, values, and chronology fields  
TARGETS: UnsupportedDurationField unsupported-operation methods and duration-field comparison behavior  
ORACLES: Trigger TestPartial_Constructors::testConstructorEx7_TypeArray_intArray assertion  
ORACLES: Constructor exception behavior is the only supplied expected-result source  
CASES: Type/value arrays containing a field backed by UnsupportedDurationField  
CASES: Valid ordered type/value arrays; null arrays/types; unequal lengths; invalid values  
CASES: Boundary ordering/contiguity when adjacent fields have supported versus unsupported durations  
RISKS: Exact exception type/message and Ex7 input data are not provided  
RISKS: Do not infer behavior from another Time version or unspecified Partial APIs