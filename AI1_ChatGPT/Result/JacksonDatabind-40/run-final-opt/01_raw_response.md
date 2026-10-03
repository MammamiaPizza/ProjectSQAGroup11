TARGETS: PrimitiveOrWrapperDeserializer.getNullValue; primitive Boolean/Byte/Short/Character/Integer/Long/Float/Double deserializers  
ORACLES: Trigger test TestSimpleTypes.testEmptyToNullCoercionForPrimitives; deserializer null-value contract  
CASES: Deserialize empty String into each primitive numeric/boolean/char target; verify coercion does not silently pass  
CASES: Compare empty String handling for primitive versus wrapper targets, whose configured null value is null  
CASES: Cover normal scalar values and boundary representations only where existing mapper APIs support them  
RISKS: Truncated source omits exact empty-string policy/error type and mapper invocation details  
RISKS: Do not infer behavior for Number, BigInteger, BigDecimal, or unknown types beyond available signatures