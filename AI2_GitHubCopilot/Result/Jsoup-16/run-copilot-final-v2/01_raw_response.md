TARGETS: org.jsoup.nodes.DocumentType.outerHtml(), DocumentType(String,String,String,String)
ORACLES: output must follow W3C: <!DOCTYPE name [PUBLIC "pub"] "sys"> or <!DOCTYPE name SYSTEM
"sys"> or <!DOCTYPE name>
ORACLES: blank name in constructor must throw IllegalArgumentException
CASES: name="html", pubId=null, sysId=null
CASES: name="html", pubId="", sysId="http..."; name="html", pubId="-//...", sysId=""
CASES: name="html", pubId="pub", sysId="sys"; name="", any pubId,sysId (expect IAE)
RISKS: no official Jsoup spec for DOCTYPE serialization; rely on standard HTML5/W3C rules
RISKS: quoting of empty public/system ids could differ from expected test snapshots