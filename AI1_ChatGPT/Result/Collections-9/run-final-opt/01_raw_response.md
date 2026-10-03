TARGETS: load(InputStream), PropertiesReader.readProperty(), unescape(String), getString(String)
ORACLES: Existing trigger testCollections271 expects "\\\\192.168.1.91\\test" to retain two leading slashes
CASES: Load a property value beginning with two backslashes; assert getString preserves both
CASES: Boundary values with one leading backslash and backslashes within/at end of value
RISKS: Escaping may be consumed during parsing before retrieval; test through public load/getString path
RISKS: Context lacks source and property-input fixture details; derive expectations only from trigger report