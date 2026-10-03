TARGETS: FromStringDeserializer.deserialize(JsonParser, DeserializationContext) handling invalid String conversion failures  
TARGETS: Std._deserialize(String, DeserializationContext) and empty/embedded-value paths  
ORACLES: Existing ProblemHandlerTest.testWeirdStringHandling expected handler-mediated weird-string behavior  
ORACLES: Invalid UUID text currently produces InvalidFormatException; verify context/problem-handler outcome  
CASES: Invalid non-empty String ("not a uuid!") through ObjectMapper deserialization with a configured handler  
CASES: Valid String conversions for supported types; empty String and embedded object inputs  
RISKS: UUID conversion implementation is not listed among FromStringDeserializer supported Std types  
RISKS: Available context omits exact expected handler return/value and relevant ProblemHandler setup