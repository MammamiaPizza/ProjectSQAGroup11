TARGETS: BeanDeserializer._deserializeUsingPropertyBased, deserializeUsingPropertyBasedWithUnwrapped
ORACLES: After last creator param, parse remaining JSON tokens into unwrapped map; verify map size
equals expected 4
CASES: NORMAL 4 unwrapped fields after last creator arg; BOUNDARY 0 fields; EDGE mixed
known+unwrapped; ERROR missing creator params
RISKS: Fix may affect other unwrapped deserialization; parser state after loop; unknown properties
handling; backward compat