TARGETS: XML declaration tokenization and XmlTreeBuilder handling of `<?xml ... ?>`.
ORACLES: Trigger expects XML declaration preserved, not serialized as `<!--?xml ... ?-->`.
CASES: `<?xml encoding='UTF-8' ?><body>One</body>` parses with declaration before body.
CASES: Verify declaration data/serialization retains `?xml encoding='UTF-8' ?`.
CASES: Boundary declaration spacing around attributes and closing `?>`.
RISKS: Available context omits public parser entry-point signatures and full expected XML semantics.