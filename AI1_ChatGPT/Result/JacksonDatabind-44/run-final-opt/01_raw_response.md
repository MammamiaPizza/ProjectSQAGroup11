TARGETS: SimpleType.constructUnsafe/construct validation; type narrowing, handlers/static typing, canonical/signature behavior  
ORACLES: Trigger TestSubtypes.testIssue1125WithDefault; exception message/property set from reported failure  
CASES: Deserialize default subtype input containing "b"; expect Default1125 to accept recognized subtype property  
CASES: construct rejects Map, Collection, and array classes with IllegalArgumentException  
CASES: constructUnsafe and construct normal scalar classes; equality/toString/signature consistency  
RISKS: Modified source details and expected fixed behavior beyond trigger/spec are unavailable  
