TARGETS: DateTimeSerializerBase._asTimestamp(SerializerProvider) and format-driven timestamp selection  
TARGETS: DateTimeSerializerBase.serialize() implementations using _timestamp(T) versus formatted output  
ORACLES: Trigger test expects {"date":["1970-01-01"]}, not {"date":[0]}  
ORACLES: Bug report 1155 and DateSerializationTest::testDateDefaultShape supply expected behavior  
CASES: Date value at epoch with default-shape configuration producing an array  
CASES: Verify array shape retains formatted date text rather than numeric timestamp  
CASES: Exercise timestamp Boolean/custom DateFormat combinations through withFormat(...)  
RISKS: Abstract base class requires testing via existing concrete date serializer/ObjectMapper path  
RISKS: Context lacks full annotations, mapper configuration, and concrete serialize implementation