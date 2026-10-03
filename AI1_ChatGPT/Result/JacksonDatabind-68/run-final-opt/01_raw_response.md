TARGETS: BeanDeserializerBase.resolve creator/delegate setup; deserializeFromArray and deserializeFromString paths.
ORACLES: Existing SingleValueAsArrayTest trigger assertions define successful deserialization expectations.
CASES: Object with chained array creators deserializes from its documented JSON input without creator error.
CASES: Bean1421B deserializes single JSON string "test2" via its configured creator.
CASES: Preserve normal array/delegate creator behavior when single-value-as-array support is involved.
RISKS: Context omits source bodies, model classes, JSON inputs, and exact assertions; inspect existing trigger test only.