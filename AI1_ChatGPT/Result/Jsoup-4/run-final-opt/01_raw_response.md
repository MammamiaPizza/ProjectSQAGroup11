TARGETS: Entities.escape(String, CharsetEncoder, EscapeMode) named-entity selection and entity-name case  
TARGETS: Entities.escape(String, Document.OutputSettings) delegation/output settings behavior  
ORACLES: Trigger assertions require Å -> &Aring; and Ü -> &Uuml; (not lowercase names)  
ORACLES: Existing EntitiesTest::escape and ::caseSensitive provide expected escaping behavior  
CASES: Escape mixed text containing &, <, >, Å, å, π and verify exact entity output  
CASES: Verify uppercase/lowercase accented characters select distinct case-sensitive named entities  
CASES: Exercise named escaping under available EscapeMode values and a charset encoder  
RISKS: Entity maps may contain duplicate upper/lowercase names; reverse lookup can choose wrong casing  
RISKS: OutputSettings construction/configuration is not provided in context; avoid assuming its API