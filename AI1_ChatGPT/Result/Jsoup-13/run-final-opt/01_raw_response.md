TARGETS: Node.attr, hasAttr, absUrl behavior for attribute keys prefixed with "abs:"  
ORACLES: Trigger assertions; Elements.absAttr/hasAbsAttr expose Node behavior  
CASES: Normal relative URL plus base URI: attr("abs:href") resolves to absolute URL  
CASES: hasAttr("abs:href") is true when underlying href exists and resolves  
CASES: Elements.absAttr("href") returns resolved URL; hasAbsAttr("href") reflects availability  
BOUNDARY: Absolute href/base URI behavior and missing underlying attribute  
RISKS: Exact resolution rules and empty/invalid base URI expectations are not provided  
RISKS: Node is abstract; tests must use existing concrete node construction APIs