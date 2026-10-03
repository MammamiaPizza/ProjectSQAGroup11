TARGETS: resolve(Type,Class,Type) resolves type vars to concrete types.
TARGETS: resolveTypeVariable(Type,Class,TypeVariable) recursive resolution logic.
TARGETS: subtypeOf(Type) / supertypeOf(Type) create WildcardType with bounds.
ORACLES: resolve nested wildcards must not duplicate bounds (e.g. <? super Number>, not <? super ?
super Number>).
ORACLES: Recursive resolve on self-referencing types must terminate without StackOverflowError.
ORACLES: canonicalize should normalize redundant recursive wildcards to their base bound.
CASES: Resolve ? super T where T resolves to ? super Number → output ? super Number.
CASES: Resolve ? extends T where T resolves to ? extends Number → output ? extends Number.
CASES: Resolve T extends Comparable<T> recursively → no stack overflow, produces resolved type.
RISKS: Only 7 trigger tests provided; no coverage of getMapKeyAndValueTypes or
getCollectionElementType resolution paths.