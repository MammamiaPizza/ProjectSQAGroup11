TARGETS: Node.attr(String), Node.hasAttr(String), Node.absUrl(String); verify "abs:" prefix
delegation.
ORACLES: Trigger tests NodeTest.handlesAbsPrefix(OnHasAttr), ElementsTest.absAttr/hasAbsAttr;
expected ]8;id=md-y5dvl4;http://jsoup.orghttp://jsoup.org]8;;]8;;.]8;;
CASES: attr exact key; attr("abs:href") absolute URL; hasAttr("abs:href") true; hasAttr missing
"abs:" false/empty.
CASES: attr with multiple attributes; setAttr then re-query abs-prefixed key; Elements-level
attr/hasAttr aggregation.
RISKS: Supplied API only lists signatures; internal impl and exact assertions not fully shown.