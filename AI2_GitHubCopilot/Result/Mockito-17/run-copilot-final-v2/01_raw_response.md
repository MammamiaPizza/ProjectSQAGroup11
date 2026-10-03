TARGETS: MockSettingsImpl.serializable(), extraInterfaces(), isSerializable(); MockUtil.createMock()
ORACLES: Check serializable() adds Serializable.class to extraInterfaces list; isSerializable()
returns true
CASES: normal: config with serializable()+extraInterfaces(), verify interfaces array includes
Serializable
CASES: boundary: extraInterfaces(null), isSerializable() false; multiple extraInterfaces calls
RISKS: CGLIB proxy class itself not serializable; bug is Serializable not propagated into generated
proxy