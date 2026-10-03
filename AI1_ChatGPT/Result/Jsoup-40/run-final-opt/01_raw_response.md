TARGETS: DocumentType(String name,String publicId,String systemId,String baseUri) validation behavior  
TARGETS: DocumentType.nodeName() behavior after construction  
ORACLES: Bug 460 and DocumentTypeTest require blank name not to throw IllegalArgumentException  
ORACLES: HtmlParserTest requires invalid doctypes to be handled without empty-string validation failure  
CASES: Construct DocumentType with name="" and representative publicId/systemId/baseUri values  
CASES: Construct with blank name plus empty publicId and systemId boundary values  
CASES: Parse invalid doctypes that yield blank/empty names; assert no IllegalArgumentException  
RISKS: Exact nodeName output and parsed document serialization are not provided in context