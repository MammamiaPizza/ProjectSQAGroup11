TARGETS: TokenBuffer.getOutputContext() and writeStartObject/writeFieldName/writeEndObject context updates.
ORACLES: Trigger expects output-context current name 'b' after writing object fields, matching JsonParser context.
CASES: Object: write field 'a', value, then field 'b'; assert getOutputContext().getCurrentName() is 'b'.
CASES: Nested object/array transitions; verify context parent/current name before and after end tokens.
CASES: Field-name overloads String and SerializableString should update the same output context.
RISKS: Context timing may differ while a field value is pending; assert only documented/trigger-relevant states.
RISKS: No full source or exact TestTokenBuffer sequence supplied; derive expectations only from trigger summary.