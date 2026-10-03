TARGETS: FormElement.formData(), addElement(), removeChild(), elements(), and submit() delegation behavior.
ORACLES: FormElementTest::createsFormData expects 6 entries; buggy result contains 7.
CASES: Construct/add form controls and assert formData() entry count and key/value contents.
CASES: Cover controls with empty value, verifying documented implementation fallback value "on".
CASES: Remove a previously added child and verify elements()/formData() no longer include it.
RISKS: Control eligibility/filtering rules are not provided; derive expectations only from available test context.