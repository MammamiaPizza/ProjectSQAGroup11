TARGETS: NodeUtil.getBooleanValue (or getPureBooleanValue, getImpureBooleanValue) for AST nodes,
returning TernaryValue.
ORACLES: Expected TernaryValue per JavaScript's ToBoolean if statically decidable; UNKNOWN when
result depends on runtime value.
CASES: Literals (true,false,0,1,null,undefined,"",NaN), comparison ops (LT,GT,LE,GE,NE), typeof,
arithmetic with NaN, logical NOT.
RISKS: getBooleanValue not in truncated API; must infer from
getPureBooleanValue/getImpureBooleanValue; optimizer context may differ from pure spec.