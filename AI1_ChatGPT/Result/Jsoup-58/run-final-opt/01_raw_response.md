TARGETS: Cleaner.isValid(Document), Cleaner.clean(Document), Jsoup.parse, Parser.parseBodyFragment  
ORACLES: Cleaner.isValid Javadoc: true only when no tags or attributes require removal  
ORACLES: Existing CleanerTest trigger assertions define expected body-HTML and document validity  
CASES: Valid whitelisted body HTML should remain valid after Jsoup parsing  
CASES: Body HTML with disallowed tags or attributes should be invalid  
CASES: Parsed documents with safe versus unsafe body content; compare isValid to cleaning need  
CASES: Boundary empty HTML and document/body wrapper handling during parsing and validation  
RISKS: Exact whitelist and failing HTML inputs are not provided in this context  
RISKS: Do not assume behavior of unlisted Whitelist rules or parser error recovery