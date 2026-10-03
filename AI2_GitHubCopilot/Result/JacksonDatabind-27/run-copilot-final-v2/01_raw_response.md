TARGETS: BeanDeserializer.deserializeWithExternalTypeId,
deserializeUsingPropertyBasedWithExternalTypeId
TARGETS: _deserializeUsingPropertyBased; CreatorProperty fallback setter/field logic
ORACLES: No IllegalStateException; verify TestExternalId.testInverseExternalId928 passes (external
id + property-based creator)
ORACLES: External type id correctly resolves when creator properties used; fallback setters defined
CASES: External type id with default constructor; creator-only properties + external type id
(normal)
CASES: Inverse external id referencing creator property; missing external id property (error case)
CASES: Boundary: all creator arguments provided; external id property appears after other properties
(order)
RISKS: Internal fallback mechanism may be fragile; limited insight into exact fix; test may fail if
fallback defaults change
RISKS: CreatorProperty behavior may depend on annotations; manual verification of deserialization
correctness needed