TARGETS: Entities.escape(String, Document.OutputSettings), especially '>' handling in attribute serialization
ORACLES: Trigger expected HTML: XML attribute escapes '<' and '>'; HTML attribute leaves '>' unescaped
CASES: Serialize title containing "<p>One</p>" with XML output syntax; expect &lt;p>One&lt;/p>
CASES: Same attribute with HTML output syntax; expect &lt;p>One&lt;/p> with literal '>' characters
RISKS: Entities.escape is package-private; test via node/output serialization if direct access is unavailable
RISKS: Context gives no broader entity, charset, or unescape expectations