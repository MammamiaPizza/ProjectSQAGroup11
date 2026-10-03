TARGETS: deserializer for java.util.Collections$UnmodifiableList in JavaUtilCollectionsDeserializers
TARGETS: JavaUtilCollectionsConverter.convert for TYPE_UNMODIFIABLE_LIST kind
TARGETS: ObjectMapper.readValue into target type Collections$UnmodifiableList
ORACLES: result.getClass() equals Collections.unmodifiableList(Arrays.asList()).getClass()
ORACLES: calling add() on result throws UnsupportedOperationException
ORACLES: list size and order match input JSON array
CASES: empty array, single element, multiple elements, elements with null
CASES: input list underlying type LinkedList (from test name) and ArrayList
RISKS: converter may delegate to singleton or hashmap optimization for size=1; must verify via test
RISKS: no access to full converter source; inferred from bug description and partial signatures