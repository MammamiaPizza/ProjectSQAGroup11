TARGETS: InnerClassProperty.assignIndex/getPropertyIndex delegate creator-property indexing correctly.
TARGETS: withName/withValueDeserializer preserve inner-class property behavior and delegate metadata.
TARGETS: deserializeAndSet instantiates inner value via _creator and assigns it to the containing bean.
ORACLES: Existing trigger InnerClassCreatorTest.testIssue1501 is the expected-result source.
ORACLES: Delegate SettableBeanProperty index is the observable source for assignIndex/getPropertyIndex.
CASES: Creator-bound inner property named "a": deserialization must not throw "no creator index".
CASES: Normal inner-class JSON value, renamed property, and replacement value deserializer paths.
CASES: Constructor-instantiation failure should follow unwrapAndThrowAsIAE IOException/IAE behavior.
RISKS: Context lacks constructor visibility and concrete fixture/JSON details; avoid assuming APIs or messages.