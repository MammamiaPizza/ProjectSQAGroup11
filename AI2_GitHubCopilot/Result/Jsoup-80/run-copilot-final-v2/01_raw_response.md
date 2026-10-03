TARGETS: XmlTreeBuilder.process(Token) when token is XmlDeclaration; insertNode; popStackToClose
ORACLES: no exception thrown; returns a Document (possibly empty or with doctype) per TreeBuilder
contract
CASES: malformed XML decl ("<?xml>"), incomplete decl ("<?xml"), empty decl, normal decl, missing
closing "?>"
RISKS: IndexOutOfBoundsException indicates stack is empty; likely occurs in popStackToClose or
insertNode when stack check missing