TARGETS: MockSettingsImpl.serializable, extraInterfaces, isSerializable; MockUtil.createMock and mock detection/accessors  
ORACLES: Trigger MocksSerializationTest.shouldBeSerializeAndHaveExtraInterfaces; absence of NotSerializableException  
CASES: Mock configured serializable() with an additional extra interface, then Java serialization/deserialization  
CASES: Verify deserialized mock retains mock usability and configured extra-interface behavior  
CASES: Extra interfaces including Serializable directly; non-serializable settings as contrast  
RISKS: Exact expected assertions beyond the trigger name are unavailable; do not infer unlisted API behavior