TARGETS: Attributes.put,add,indexOfKeyIgnoreCase,getIgnoreCase; Token.Tag.finaliseTag,newAttribute;
HtmlTreeBuilder.insert; XmlTreeBuilder.process
ORACLES: HTML5: duplicate attrs case-insensitive, keep first; XML: case-sensitive, keep distinct
case; expected outputs in trigger assert messages
CASES: same-case dup, diff-case dup, case-sensitive parsing on, case-insensitive on, data- keys
preserved, boolean attrs, null key
RISKS: Private add()/checkCapacity not directly testable; dedup may depend on ParseSettings
internals; limited code visibility