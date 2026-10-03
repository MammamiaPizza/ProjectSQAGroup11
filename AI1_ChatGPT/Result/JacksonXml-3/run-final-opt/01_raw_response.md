TARGETS: FromXmlParser.nextTextValue(), nextToken(), and attribute/text token state transitions.
ORACLES: Trigger asserts nextTextValue returns "7", not null, for XML attributes.
CASES: Attribute followed by text value; verify nextTextValue yields text after attribute traversal.
CASES: Normal token iteration versus nextTextValue shortcut; verify equivalent text result.
RISKS: Parser state, _nextToken, and _currText may be stale or skipped around attributes.
RISKS: Input fixture and exact XML structure are not provided; derive expectations only from trigger.