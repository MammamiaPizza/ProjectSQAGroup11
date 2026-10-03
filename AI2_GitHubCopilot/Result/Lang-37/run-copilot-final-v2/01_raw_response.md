TARGETS: private add(Object array,int index,Object element,Class<?> clss)
TARGETS: public generic add(T[],int index,T element) and its delegators
TARGETS: public addAll(T[],...T) if present; compound array insertion paths
ORACLES: new array must use original array component type (array.getClass().getComponentType())
ORACLES: successful insertion when element is assignable to array component type
ORACLES: copy elements from source and place element at index; length == old.length+1
CASES: add Double to Number[] (subtype → supertype array) – no ArrayStoreException
CASES: add null to Object[]; add at index 0; add at index == array.length
CASES: empty Object[] add; add to array of interface type with implementing element
RISKS: full method signatures truncated; missing addAll/insert variants
RISKS: exact exception semantics for incompatible element unclear without spec
RISKS: cannot consult fixed version; relying only on Java array type rules