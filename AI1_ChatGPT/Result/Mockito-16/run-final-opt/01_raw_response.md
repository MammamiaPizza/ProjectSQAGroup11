TARGETS: Mockito.when and MockitoCore.when/stub interaction with mocks using RETURNS_MOCKS default answer.
ORACLES: Trigger expects stubbing a method call on a RETURNS_MOCKS-configured mock not to throw MissingMethodInvocationException.
CASES: Mock with RETURNS_MOCKS; invoke nested/returned mock method in when(...).thenReturn(...); assert configured value.
CASES: Direct stubbing on a RETURNS_MOCKS mock remains functional and returned mock behavior is preserved.
RISKS: Ongoing stubbing/progress state may be consumed or cleared by automatic mock-return creation.
RISKS: Context lacks trigger source/interface methods and exact expected values; derive assertions only from available behavior.