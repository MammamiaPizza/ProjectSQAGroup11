TARGETS: NumberSerializers scalar serializers; empty-value behavior used by JsonInclude inclusion.
ORACLES: Trigger expects a bean/object with zero scalar omitted to serialize as `{}`.
CASES: Integer zero with NON_EMPTY/empty inclusion; verify nonzero integer remains included.
CASES: Cover registered wrappers: Long, Byte, Short, Float, Double zero versus nonzero inclusion.
ORACLES: Serialized JSON output from ObjectMapper and trigger's `{}` versus `{"value":0}` expectation.
RISKS: Signatures omit isEmpty behavior and inclusion configuration details; derive only from trigger context.
RISKS: Do not infer schema/visitor expectations or direct serializer error behavior from provided context.