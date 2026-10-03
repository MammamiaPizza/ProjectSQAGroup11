TARGETS: Attributes.html(Appendable, Document.OutputSettings) boolean-attribute serialization  
TARGETS: Attributes.put(String,String), put(String,boolean), and Attribute iteration/asList interaction  
ORACLES: Trigger ElementTest.booleanAttributeOutput expected HTML omits ="": noshade, nohref, async  
CASES: Serialize attributes with empty-string boolean keys; assert key-only output, not key=""  
CASES: Serialize mixed valued and boolean attributes; preserve valued src="foo" and boolean key-only forms  
CASES: Check put(key,true) versus put(key,false) behavior through HTML output  
RISKS: Boolean recognition/output settings may be delegated to Attribute or Document.OutputSettings  
RISKS: Context lacks exact constructor/setup and complete expected output for direct Attributes tests