TARGETS: DateTimeZone.forID(String), especially legacy/alias ID resolution and returned getID().
ORACLES: Trigger asserts DateTimeZone.forID("WET").getID() equals "WET", not "Europe/London".
CASES: Normal: forID("WET") preserves the requested legacy ID in the resulting zone ID.
CASES: Regression: assert returned ID exactly; avoid relying on locale names or current instant offsets.
RISKS: Zone provider data/configuration may affect available IDs and alias resolution.
RISKS: Context identifies only the WET-to-Europe/London mismatch; other aliases are unspecified.