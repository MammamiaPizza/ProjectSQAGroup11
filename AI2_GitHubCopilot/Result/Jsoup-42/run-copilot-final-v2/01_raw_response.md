TARGETS: FormElement.formData() list size and values; checkbox without value handling
TARGETS: Element.val()/attr interaction for checked checkbox with no value
ORACLES: junit asserts size==6 (createsFormData) and value [on] (usesOnForCheckboxValueIfNoValueSet)
ORACLES: browser default for checked checkbox/radio with no value is "on"
CASES: normal text/select inputs counted exactly once
CASES: checked checkbox with explicit value keeps its value
CASES: checked checkbox without value returns "on"
CASES: unchecked checkbox/radio omitted from formData
CASES: boundary empty name/value attributes skipped
RISKS: only two failing tests provided; no full expected ordering/count beyond 6 and [on]