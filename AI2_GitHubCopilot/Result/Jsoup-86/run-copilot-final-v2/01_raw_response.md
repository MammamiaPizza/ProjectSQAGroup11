TARGETS: org.jsoup.nodes.Comment.asXmlDeclaration(), isXmlDeclaration(),
constructors(Comment(String), Comment(String,String))
ORACLES: Must not throw IndexOutOfBoundsException; null for non-XML-declaration content;
XmlDeclaration fields not fully known
CASES: Comment(""), Comment("!"), Comment("?xml"), Comment("?xml version="1.0"?>"), Comment("<!--"),
Comment("invalid")
RISKS: Exact fix unknown; rely on no-throw; trimmed leading-tag/whitespace may affect
isXmlDeclaration; boundary between comment and declaration fragile