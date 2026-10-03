TARGETS: JsonPointer.compile/valueOf parsing numeric-looking segments; _parseIndex element-index recognition.  
ORACLES: Trigger requires "/1e0" not to throw NumberFormatException; use exposed pointer matching state/toString.  
CASES: compile("/1e0"): property "1e0", index -1, mayMatchProperty true, mayMatchElement false.  
CASES: normal numeric "/0" and boundaries/non-index numeric forms such as "/01", "/-1", "/1e0".  
CASES: matchProperty/matchElement distinguish exact property names from valid array indexes.  
RISKS: No authoritative expected behavior beyond trigger/API; avoid assuming unprovided JSON Pointer edge rules.