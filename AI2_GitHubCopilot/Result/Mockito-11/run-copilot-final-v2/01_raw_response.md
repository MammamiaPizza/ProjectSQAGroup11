TARGETS: DelegatingMethod.equals(Object)
ORACLES: use method name+parameterTypes+returnType equality; self must be true
CASES: same wrapped Method; distinct methods with equal signature; null; non-Method object
CASES: methods differing by name, params, returnType, declaring class
RISKS: equals semantics for MockitoMethod not documented in provided summary
RISKS: cannot inspect modified source; rely on trigger names only
RISKS: hashCode contract vs equals must stay consistent