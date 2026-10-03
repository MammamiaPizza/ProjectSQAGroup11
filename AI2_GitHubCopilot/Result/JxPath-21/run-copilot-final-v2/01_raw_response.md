TARGETS: equals(), hashCode(), getImmediateNode(), getLength(), isActual(),
getImmediateValuePointer()
ORACLES: equals/hashCode contract; null beans with same property name must be equal; null values
must appear in iteration (not be skipped)
CASES: same null bean, diff propertyName; propertyIndex=UNSPECIFIED_PROPERTY vs specific;
getImmediateNode() for null-bean pointer
CASES: iterator over collection that yields null; isActual() with null; boundary: propertyCount=0,
getLength() on null
RISKS: Abstract class requires test stub; trigger failures are the only given expected-value
sources; implementation unseen