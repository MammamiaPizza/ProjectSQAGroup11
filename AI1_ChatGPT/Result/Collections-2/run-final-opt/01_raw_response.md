TARGETS: getInclude(), setInclude(String), and load(InputStream) include-directive recognition  
ORACLES: Trigger TestExtendedProperties::testInclude expects default include token "include", not "import"  
CASES: New instance: getInclude() returns "include"  
CASES: setInclude(custom) then getInclude() returns that custom directive name  
CASES: Load content using "include" directive; verify included properties are available  
RISKS: Include loading needs filesystem/resource setup; supplied context omits file syntax and fixture paths