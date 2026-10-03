TARGETS: MappingIterator.hasNextValue(), nextValue(), hasNext(), next(), readAll() recovery behavior.
ORACLES: Trigger tests define expected recovery after invalid root/array values and subsequent Bean values.
CASES: Root sequence: invalid integral token then valid Bean; iterator continues to valid value.
CASES: Array sequence: invalid integral element then valid Bean; recovery preserves later element access.
CASES: Valid root Bean sequence and readAll() collection results/order.
CASES: hasNext()/next() exception wrapping versus hasNextValue()/nextValue() IOException behavior.
RISKS: Parser token advancement after deserialization failure may skip or re-read values.
RISKS: Available context lacks exact expected assertions and fixture JSON; derive only from trigger tests.