TARGETS: FormElement.formData(); element selection and checkbox value handling.
ORACLES: Trigger assertions: form data count is 6; checkbox without value yields "on".
CASES: Successful controls produce expected key/value entries; unchecked checkbox is excluded.
CASES: Checked checkbox lacking a value attribute uses default value "on".
CASES: Boundary: disabled controls and non-submittable elements should not add entries.
RISKS: Context omits full formData rules and exact fixture markup; derive only from available triggers.