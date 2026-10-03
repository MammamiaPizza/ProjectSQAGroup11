TARGETS: Node.toString() when parent==null; also Node.outerHtml(), Node.ownerDocument()
ORACLES: No NullPointerException; toString() returns node's HTML (matches outerHtml() output)
ORACLES: Expected from Jsoup design: detached nodes should serialize without document context
CASES: element.parentlessToString (trigger) – create Element with null parent, call toString()
CASES: parentless Node with children and attributes, parentless Node.outerHtml(), parentless Node
after remove()
CASES: Node with parent but not in a Document (ownerDocument()==null)
RISKS: NPE likely from ownerDocument().outputSettings() or baseUri resolution in OuterHtmlVisitor
RISKS: No access to fix version; must infer safe fallback from existing Node constructors and
baseUri field
RISKS: ElementTest.java not inspectable; need to replicate detached Element via new Element(Tag,
baseUri) with no parent