TARGETS: formData() – returns List<KeyVal> of form field name-value pairs.
ORACLES: test expects size 6; bug returns 7. Likely uncontrolled checkbox/radio added despite not
checked.
CASES: Normal: text, textarea, select, checked checkbox, checked radio, unchecked checkbox, disabled
inputs. Boundary: empty form, multiple same-name radios.
RISKS: Only limited signatures visible; cannot inspect actual source to see filter logic.