TARGETS: BuilderBasedDeserializer.deserializeFromObject and deserializeWithUnwrapped creator-property handling.
TARGETS: Builder completion via finishBuild after unwrapped fields and property-based creator values.
ORACLES: Trigger assertions: unwrapped name is "John"; unwrapped age is 30, not null/0.
CASES: Single creator parameter before unwrapped fields; multiple creator parameters before unwrapped fields.
CASES: Single and multiple creator parameters with unwrapped fields in the middle of input.
CASES: Verify created builder/object retains unwrapped values after creator instantiation and build.
RISKS: Context exposes no fixture JSON, builder definitions, annotations, or exact property names beyond failures.
