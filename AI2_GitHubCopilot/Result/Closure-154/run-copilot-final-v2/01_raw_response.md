TARGETS: TypeCheck.visitObjLitKey for interface property validation
TARGETS: TypeValidator mismatch registration for interface property conflicts
TARGETS: TypeCheck interface extends checks (duplicate properties across extended interfaces)
ORACLES: Expected warnings defined in TypeCheckTest trigger case (testInterfaceInheritanceCheck12)
ORACLES: Interface member validation messages embedded in TypeCheck
ORACLES: TypeValidator "not implemented by type" message on missing interface properties
CASES: Interface inheriting from multiple interfaces with the same property defined
CASES: Interface extending another interface where both declare the same property
CASES: Property with incompatible type compared to overridden property in extended interface
RISKS: Only partial TypeCheck API shown; visitObjLitKey internals may be incomplete
RISKS: Exact expected-warning strings from trigger test not visible in provided context