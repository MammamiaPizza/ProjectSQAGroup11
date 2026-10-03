TARGETS: FieldFormatter.printTo, FieldFormatter.calculatePrintedLength (sign handling)
ORACLES: ISO standard format: negative periods must include "-" before affected field
CASES: negative‑only seconds, negative millis, negative minutes+seconds, zero, large negative
RISKS: Bug may only affect seconds/millis fields; other fields might already have sign