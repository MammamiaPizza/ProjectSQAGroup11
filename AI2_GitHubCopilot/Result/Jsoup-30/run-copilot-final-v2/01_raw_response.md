TARGETS: isValid(Document dirtyDocument) – returns true if no elements/attributes need removal
ORACLES: Whitelist.basic() allows <p>,<b>,<i>,<u>, etc.; excludes <script>,<img>, etc.
CASES: doc with only allowed <p> → true; with <script> → false; empty body → true
CASES: <p style="color:red"> with disallowed attribute → false; nested <div><p></div> → false
RISKS: Bug 246: isValid may incorrectly return true for documents containing disallowed tags