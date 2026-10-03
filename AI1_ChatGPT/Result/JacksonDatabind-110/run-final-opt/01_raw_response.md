TARGETS: JavaUtilCollectionsDeserializers.converter() selection for Collections unmodifiable list types  
TARGETS: JavaUtilCollectionsConverter.convert() handling TYPE_UNMODIFIABLE_LIST  
ORACLES: Trigger expects deserialization of Collections$UnmodifiableList, not InvalidDefinitionException  
ORACLES: Expected collection behavior/type is constrained by UtilCollectionsTypesTest trigger  
CASES: Deserialize JSON array into an unmodifiable list created from LinkedList  
CASES: Verify element order/content survives conversion through List input type  
CASES: Boundary empty and single-element JSON arrays for unmodifiable-list conversion  
RISKS: Concrete JDK wrapper class detection may vary by collection factory/JDK implementation  
RISKS: Context lacks full trigger source and expected assertions beyond reported construction failure