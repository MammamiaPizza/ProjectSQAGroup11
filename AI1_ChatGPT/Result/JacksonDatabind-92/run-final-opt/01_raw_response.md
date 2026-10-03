TARGETS: BeanDeserializerFactory bean-type eligibility and illegal-class-name rejection paths  
ORACLES: IllegalTypesCheckTest.testJDKTypes1737 requires the tested JDK type not to deserialize successfully  
CASES: Trigger's JDK type through normal deserialization; assert rejection rather than successful bean creation  
CASES: Boundary around configured/default illegal class-name matching, if reachable through public mapper setup  
RISKS: Context omits the exact JDK type, exception class/message, and full modified method body  
RISKS: Do not infer behavior for other JDK classes or APIs beyond the supplied trigger/specification