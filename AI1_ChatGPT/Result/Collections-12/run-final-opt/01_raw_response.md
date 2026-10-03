TARGETS: ExtendedProperties load(), PropertiesReader/PropertiesTokenizer, and collection-valued property retrieval.
ORACLES: Assertions in TestExtendedProperties.testCollections271; API-visible results from loaded input.
CASES: Trigger-equivalent loaded property; ordinary single/multiple values; delimiter/escape boundary inputs.
RISKS: Trigger body and exact COLLECTIONS-271 expected behavior are not provided; avoid assumptions beyond it.