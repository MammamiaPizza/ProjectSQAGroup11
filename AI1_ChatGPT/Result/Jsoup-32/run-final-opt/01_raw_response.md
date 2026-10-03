TARGETS: Element.clone(), classNames(), classNames(Set), addClass(), removeClass(), toggleClass()
ORACLES: Trigger test name testClonesClassnames; public classNames/class attribute contract
CASES: Clone element with multiple classes; assert clone reports same class names
CASES: Mutate clone classes after cloning; verify original class names remain unchanged
CASES: Mutate original classes after cloning; verify clone class names remain unchanged
CASES: Clone element with no class attribute or an empty class-name set
RISKS: Clone may share mutable Attributes/class-name state rather than copying it
RISKS: No source diff or full trigger body is provided; avoid assumptions beyond exposed API/docs